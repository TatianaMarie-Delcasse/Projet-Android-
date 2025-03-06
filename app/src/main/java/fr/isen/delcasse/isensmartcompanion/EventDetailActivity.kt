package fr.isen.delcasse.isensmartcompanion

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.work.*
import com.google.gson.Gson
import fr.isen.delcasse.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import fr.isen.delcasse.isensmartcompanion.utils.ReminderWorker
import java.util.concurrent.TimeUnit

class EventDetailActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("Permission", "Notification permission granted")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        val jsonEvent = intent.getStringExtra("event_json")
        val event = Gson().fromJson(jsonEvent, Event::class.java)

        setContent {
            ISENSmartCompanionTheme {
                EventDetailScreen(event, this)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "EVENT_REMINDER",
                "Rappels d'événements",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
fun EventDetailScreen(event: Event, activity: EventDetailActivity) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("event_prefs", Context.MODE_PRIVATE)
    var isReminderSet by remember { mutableStateOf(prefs.getBoolean(event.id, false)) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { activity.finish() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                Text("Retour")
            }

            IconButton(onClick = {
                isReminderSet = !isReminderSet
                prefs.edit().putBoolean(event.id, isReminderSet).apply()

                val agendaPrefs = context.getSharedPreferences("AgendaPrefs", Context.MODE_PRIVATE)
                val savedEvents = agendaPrefs.getStringSet("agenda_events", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

                if (isReminderSet) {
                    savedEvents.add(Gson().toJson(event))
                    scheduleNotification(context, event)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        activity.requestNotificationPermission()
                    }
                } else {
                    savedEvents.remove(Gson().toJson(event))
                }

                agendaPrefs.edit().putStringSet("agenda_events", savedEvents).apply()

            }) {
                Icon(
                    imageVector = if (isReminderSet) Icons.Filled.Notifications else Icons.Filled.NotificationsNone,
                    contentDescription = "Rappel",
                    tint = if (isReminderSet) Color.Red else Color.Gray
                )
            }
        }

        Text(text = event.title, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(text = event.date, fontSize = 20.sp)
        Text(text = event.location, fontSize = 20.sp)
        Text(text = event.description, fontSize = 16.sp)
    }
}

fun scheduleNotification(context: Context, event: Event) {
    val workManager = WorkManager.getInstance(context)
    val data = workDataOf("event_title" to event.title)

    val request = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(10, TimeUnit.SECONDS)
        .setInputData(data)
        .build()

    workManager.enqueue(request)
}
