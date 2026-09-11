package com.dolarcop.app.widget

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Programa (una sola vez) el trabajo periódico que mantiene actualizadas
 * las tasas de cambio y el widget, incluso con la app cerrada.
 */
object RatesUpdateScheduler {

    private const val WORK_NAME = "update_rates_work"

    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<UpdateRatesWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
