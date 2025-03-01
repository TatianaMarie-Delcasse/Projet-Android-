package fr.isen.delcasse.isensmartcompanion.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import fr.isen.delcasse.isensmartcompanion.R

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val eventTitle = inputData.getString("event_title") ?: "Événement"
        sendNotification(eventTitle)
        return Result.success()
    }

    private fun sendNotification(eventTitle: String) {
        val context = applicationContext
        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "EVENT_REMINDER",
                "Rappels d'événements",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "EVENT_REMINDER")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Rappel d'événement")
            .setContentText("N'oubliez pas : $eventTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(eventTitle.hashCode(), notification)
    }
}
