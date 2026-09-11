package com.dolarcop.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dolarcop.app.data.Currencies
import com.dolarcop.app.data.Currency
import com.dolarcop.app.data.ExchangeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CalculatorUiState(
    val amountText: String = "1",
    val selectedCurrency: Currency = Currencies.ALL.first { it.code == "USD" },
    val resultCOP: Double? = null,
    val isLoading: Boolean = true,
    val isFromCache: Boolean = false,
    val lastUpdatedText: String = "",
    val errorMessage: String? = null,
    val rates: Map<String, Double> = emptyMap()
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExchangeRepository(application)

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        loadInitial()
    }

    private fun loadInitial() {
        viewModelScope.launch {
            // Primero mostramos lo que haya en caché (si existe) para respuesta instantánea.
            repository.getCachedRates()?.let { cached ->
                applyRates(cached.rates, cached.lastUpdatedMillis, fromCache = true)
            }
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.refreshRates()
                .onSuccess { result ->
                    applyRates(result.rates, result.lastUpdatedMillis, result.fromCache)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo actualizar. Mostrando último valor disponible."
                    )
                }
        }
    }

    private fun applyRates(rates: Map<String, Double>, updatedMillis: Long, fromCache: Boolean) {
        val sdf = SimpleDateFormat("d MMM yyyy, HH:mm", Locale("es", "CO"))
        _uiState.value = _uiState.value.copy(
            rates = rates,
            isLoading = false,
            isFromCache = fromCache,
            lastUpdatedText = sdf.format(Date(updatedMillis)),
            errorMessage = null
        )
        recalculate()
    }

    fun onAmountChanged(newText: String) {
        // Solo dígitos y un separador decimal.
        val filtered = newText.filter { it.isDigit() || it == '.' || it == ',' }
        _uiState.value = _uiState.value.copy(amountText = filtered)
        recalculate()
    }

    fun onCurrencySelected(currency: Currency) {
        _uiState.value = _uiState.value.copy(selectedCurrency = currency)
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val amount = state.amountText.replace(",", ".").toDoubleOrNull()
        if (amount == null || state.rates.isEmpty()) {
            _uiState.value = state.copy(resultCOP = null)
            return
        }
        val result = repository.convertToCOP(amount, state.selectedCurrency.code, state.rates)
        _uiState.value = state.copy(resultCOP = result)
    }
}
