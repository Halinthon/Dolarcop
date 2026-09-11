package com.dolarcop.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Guardamos en una sola fila el último set de tasas conocido (en formato JSON),
 * para poder funcionar sin conexión mostrando la última cotización disponible.
 */
@Entity(tableName = "rates_cache")
data class RatesCache(
    @PrimaryKey val id: Int = 0,
    val baseCode: String,
    val ratesJson: String,
    val lastUpdatedMillis: Long,
    val lastUpdateApi: String?
)
