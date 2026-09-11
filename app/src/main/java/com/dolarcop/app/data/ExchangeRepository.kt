package com.dolarcop.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Fuente única de verdad para las tasas de cambio.
 * - Intenta refrescar desde la API.
 * - Si falla (sin internet, error del servidor), usa la última copia guardada en Room.
 * - Además guarda en SharedPreferences un resumen rápido (USD->COP) para que el
 *   widget de escritorio pueda leerlo de forma instantánea sin tocar Room/red.
 */
class ExchangeRepository(private val context: Context) {

    private val dao = AppDatabase.getInstance(context).ratesCacheDao()
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("dolar_widget_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PREF_USD_TO_COP = "usd_to_cop"
        const val PREF_LAST_UPDATED = "last_updated_millis"
        private const val BASE = "USD"
    }

    data class RatesResult(
        val rates: Map<String, Double>,
        val lastUpdatedMillis: Long,
        val lastUpdateApi: String?,
        val fromCache: Boolean
    )

    /** Intenta traer datos frescos de la API; si falla, cae a caché local. */
    suspend fun refreshRates(): Result<RatesResult> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.getLatestRates(BASE)
            val now = System.currentTimeMillis()
            val json = gson.toJson(response.rates)

            dao.save(
                RatesCache(
                    baseCode = response.baseCode,
                    ratesJson = json,
                    lastUpdatedMillis = now,
                    lastUpdateApi = response.lastUpdateUtc
                )
            )

            response.rates["COP"]?.let { copPerUsd ->
                prefs.edit()
                    .putFloat(PREF_USD_TO_COP, copPerUsd.toFloat())
                    .putLong(PREF_LAST_UPDATED, now)
                    .apply()
            }

            Result.success(
                RatesResult(
                    rates = response.rates,
                    lastUpdatedMillis = now,
                    lastUpdateApi = response.lastUpdateUtc,
                    fromCache = false
                )
            )
        } catch (e: Exception) {
            getCachedRates()?.let { Result.success(it) }
                ?: Result.failure(e)
        }
    }

    /** Lee solo la caché local (Room), sin llamar a la red. */
    suspend fun getCachedRates(): RatesResult? = withContext(Dispatchers.IO) {
        val cache = dao.getCache() ?: return@withContext null
        val type = object : TypeToken<Map<String, Double>>() {}.type
        val rates: Map<String, Double> = gson.fromJson(cache.ratesJson, type)
        RatesResult(
            rates = rates,
            lastUpdatedMillis = cache.lastUpdatedMillis,
            lastUpdateApi = cache.lastUpdateApi,
            fromCache = true
        )
    }

    /**
     * Convierte [amount] de la moneda [fromCode] a pesos colombianos (COP),
     * usando el mapa de tasas (base USD): rates[X] = cuántos X equivalen a 1 USD.
     */
    fun convertToCOP(amount: Double, fromCode: String, rates: Map<String, Double>): Double? {
        val rateFrom = rates[fromCode] ?: return null
        val rateCop = rates["COP"] ?: return null
        val amountInUsd = amount / rateFrom
        return amountInUsd * rateCop
    }

    /**
     * Convierte [amountCop] (pesos colombianos) al equivalente en la moneda [toCode].
     * Es la operación inversa de [convertToCOP].
     */
    fun convertFromCOP(amountCop: Double, toCode: String, rates: Map<String, Double>): Double? {
        val rateCop = rates["COP"] ?: return null
        val rateTo = rates[toCode] ?: return null
        val amountInUsd = amountCop / rateCop
        return amountInUsd * rateTo
    }
}
