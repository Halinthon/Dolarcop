package com.dolarcop.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dolarcop.app.data.Currencies
import com.dolarcop.app.data.Currency
import com.dolarcop.app.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val state by viewModel.uiState.collectAsState()
    var showCurrencyPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Convertidor a COP", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar tasas")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }

            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }

            Text("Puedes escribir en cualquiera de los dos campos:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            // Campo 1: moneda seleccionada (ej. USD, EUR...)
            Text("Monto en moneda extranjera", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCurrencyPicker = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(state.selectedCurrency.flag, fontSize = 22.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(state.selectedCurrency.code, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Icon(Icons.Filled.Search, contentDescription = "Cambiar moneda", modifier = Modifier.padding(start = 4.dp).size(16.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.currencyAmountText,
                onValueChange = viewModel::onCurrencyAmountChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 22.sp),
                suffix = { Text(state.selectedCurrency.code) }
            )

            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Icon(
                    Icons.Filled.SwapVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(4.dp))

            // Campo 2: pesos colombianos
            Text("Monto en pesos colombianos (COP)", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = state.copAmountText,
                onValueChange = viewModel::onCopAmountChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                suffix = { Text("COP") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                )
            )

            Spacer(Modifier.height(20.dp))

            if (state.lastUpdatedText.isNotEmpty()) {
                Text(
                    (if (state.isFromCache) "Última tasa guardada: " else "Actualizado: ") + state.lastUpdatedText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showCurrencyPicker) {
        CurrencyPickerDialog(
            currencies = Currencies.ALL,
            onDismiss = { showCurrencyPicker = false },
            onSelect = {
                viewModel.onCurrencySelected(it)
                showCurrencyPicker = false
            }
        )
    }
}

@Composable
private fun CurrencyPickerDialog(
    currencies: List<Currency>,
    onDismiss: () -> Unit,
    onSelect: (Currency) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) currencies
        else currencies.filter {
            it.code.contains(query, ignoreCase = true) || it.name.contains(query, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Selecciona una moneda") },
        text = {
            Column(modifier = Modifier.heightIn(max = 420.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Buscar por código o nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                LazyColumn {
                    items(filtered) { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(currency) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(currency.flag, fontSize = 20.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(currency.code, fontWeight = FontWeight.Bold)
                                Text(currency.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    )
}
