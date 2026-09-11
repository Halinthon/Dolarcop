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

/** Indica cuál de los dos campos editó el usuario por última vez, para saber cuál recalcular. */
enum class EditedSide { CURRENCY, COP }

data class CalculatorUiState(
    val currencyAmountText: String = "1",
    val copAmountText: String = "",
    val selectedCurrency: Currency = Currencies.ALL.first { it.code == "USD" },
    val lastEditedSide: EditedSide = EditedSide.CURRENCY,
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
        recalc()
    }

    /** El usuario escribió un monto en la moneda seleccionada (ej. USD, EUR...). */
    fun onCurrencyAmountChanged(newText: String) {
        val filtered = sanitize(newText)
        _uiState.value = _uiState.value.copy(
            currencyAmountText = filtered,
            lastEditedSide = EditedSide.CURRENCY
        )
        recalc()
    }

    /** El usuario escribió un monto en pesos colombianos (COP). */
    fun onCopAmountChanged(newText: String) {
        val filtered = sanitize(newText)
        _uiState.value = _uiState.value.copy(
            copAmountText = filtered,
            lastEditedSide = EditedSide.COP
        )
        recalc()
    }

    fun onCurrencySelected(currency: Currency) {
        _uiState.value = _uiState.value.copy(selectedCurrency = currency)
        recalc()
    }

    private fun sanitize(text: String): String = text.filter { it.isDigit() || it == '.' || it == ',' }

    /**
     * Recalcula el campo "opuesto" al que el usuario acaba de editar, manteniendo
     * el campo editado intacto. Esto es lo que hace la calculadora bidireccional.
     */
    private fun recalc() {
        val state = _uiState.value
        if (state.rates.isEmpty()) return

        when (state.lastEditedSide) {
            EditedSide.CURRENCY -> {
                val amount = state.currencyAmountText.replace(",", ".").toDoubleOrNull()
                val cop = amount?.let {
                    repository.convertToCOP(it, state.selectedCurrency.code, state.rates)
                }
                _uiState.value = state.copy(copAmountText = cop?.let { formatPlain(it) } ?: "")
            }
            EditedSide.COP -> {
                val amountCop = state.copAmountText.replace(",", ".").toDoubleOrNull()
                val inCurrency = amountCop?.let {
                    repository.convertFromCOP(it, state.selectedCurrency.code, state.rates)
                }
                _uiState.value = state.copy(currencyAmountText = inCurrency?.let { formatPlain(it) } ?: "")
            }
        }
    }

    private fun formatPlain(value: Double): String {
        return String.format(Locale.US, "%.2f", value)
    }
}
