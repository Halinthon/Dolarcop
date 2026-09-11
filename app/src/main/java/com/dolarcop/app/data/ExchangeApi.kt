package com.dolarcop.app.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API gratuita y sin necesidad de API Key: open.er-api.com
 * Documentación: https://www.exchangerate-api.com/docs/free
 * Devuelve las tasas de cambio de todas las monedas respecto a una moneda base.
 * Se actualiza aproximadamente cada 24 horas en el plan gratuito sin registro.
 */
interface ExchangeApi {
    @GET("v6/latest/{base}")
    suspend fun getLatestRates(@Path("base") base: String = "USD"): ExchangeResponse
}

data class ExchangeResponse(
    @SerializedName("result") val result: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("time_last_update_utc") val lastUpdateUtc: String?,
    @SerializedName("rates") val rates: Map<String, Double>
)
