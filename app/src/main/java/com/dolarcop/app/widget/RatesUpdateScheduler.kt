package com.dolarcop.app.widget

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Programa las tareas en segundo plano que mantienen actualizadas las tasas
 * de cambio y el widget de escritorio, funcione o no la app abierta.
 */
object RatesUpdateScheduler {

    private const val PERIODIC_WORK_NAME = "update_rates_periodic_work"
    private const val IMMEDIATE_WORK_NAME = "update_rates_immediate_work"

    /** Actualización recurrente cada 6 horas (mientras haya internet). */
    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<UpdateRatesWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    /**
     * Dispara una actualización inmediata (una sola vez) apenas haya internet.
     * Se usa cuando se agrega el widget al escritorio, para que no dependa de
     * que el usuario haya abierto la app primero, ni de esperar el ciclo de 6h.
     */
    fun triggerImmediateUpdate(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<UpdateRatesWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}
