package fr.isen.delcasse.isensmartcompanion

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
                // Permission accordée
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
            Button(
                onClick = { activity.finish() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1A1A))
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retour aux événements")
            }

            IconButton(onClick = {
                isReminderSet = !isReminderSet
                prefs.edit().putBoolean(event.id, isReminderSet).apply()

                if (isReminderSet) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        activity.requestNotificationPermission()
                    }
                    scheduleNotification(context, event)
                }
            }) {
                Icon(
                    imageVector = if (isReminderSet) Icons.Filled.Notifications else Icons.Filled.NotificationsNone,
                    contentDescription = "Set Reminder",
                    tint = if (isReminderSet) Color(0xFFC5050C) else Color.Gray,
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = event.title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF9800)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Date:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(event.date, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Lieu:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(event.location, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Catégorie:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(event.category, fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = event.description,
            fontSize = 20.sp,
            color = Color(0xFF0C0C0C)
        )
    }
}

fun scheduleNotification(context: Context, event: Event) {
    val workManager = WorkManager.getInstance(context)
    val data = workDataOf("event_title" to event.title)

    val notificationRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(10, TimeUnit.SECONDS)
        .setInputData(data)
        .build()

    workManager.enqueue(notificationRequest)
}