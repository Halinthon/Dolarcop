package com.dolarcop.app.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dolarcop.app.data.ExchangeRepository

/**
 * Worker en segundo plano que refresca las tasas de cambio periódicamente
 * y actualiza el widget del escritorio con el nuevo valor.
 */
class UpdateRatesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = ExchangeRepository(applicationContext)
        return try {
            repository.refreshRates()
            DolarWidgetProvider.updateAll(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
