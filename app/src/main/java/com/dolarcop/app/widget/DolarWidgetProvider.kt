package com.dolarcop.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.dolarcop.app.MainActivity
import com.dolarcop.app.R
import com.dolarcop.app.data.ExchangeRepository
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Widget de escritorio: muestra el valor actual del dólar (USD) en pesos colombianos.
 * Lee el valor desde SharedPreferences (guardado por ExchangeRepository) para que la
 * actualización del widget sea instantánea y no dependa de Room ni de la red directamente.
 */
class DolarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                android.content.ComponentName(context, DolarWidgetProvider::class.java)
            )
            for (id in ids) {
                updateWidget(context, manager, id)
            }
        }

        private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefs = context.getSharedPreferences("dolar_widget_prefs", Context.MODE_PRIVATE)
            val value = prefs.getFloat(ExchangeRepository.PREF_USD_TO_COP, -1f)
            val lastUpdated = prefs.getLong(ExchangeRepository.PREF_LAST_UPDATED, -1L)

            val views = RemoteViews(context.packageName, R.layout.widget_dolar)

            if (value > 0f) {
                val formatter = NumberFormat.getNumberInstance(Locale("es", "CO")).apply {
                    maximumFractionDigits = 2
                }
                views.setTextViewText(R.id.widget_value, "$ ${formatter.format(value.toDouble())}")
            } else {
                views.setTextViewText(R.id.widget_value, "Sin datos")
            }

            if (lastUpdated > 0L) {
                val sdf = SimpleDateFormat("d MMM, HH:mm", Locale("es", "CO"))
                views.setTextViewText(R.id.widget_updated, "Act: ${sdf.format(Date(lastUpdated))}")
            }

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_value, pendingIntent)
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
