package fr.isen.delcasse.isensmartcompanion

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson

@Composable
fun CalendarScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("selected_courses", Context.MODE_PRIVATE)
    val selectedCourses = prefs.getStringSet("selected_courses", emptySet())?.toList() ?: emptyList()

    var savedEvents by remember { mutableStateOf<List<Event>>(emptyList()) }

    LaunchedEffect(Unit) {
        val agendaPrefs = context.getSharedPreferences("AgendaPrefs", Context.MODE_PRIVATE)
        val savedEventsSet = agendaPrefs.getStringSet("agenda_events", mutableSetOf()) ?: mutableSetOf()
        savedEvents = savedEventsSet.map { Gson().fromJson(it, Event::class.java) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Calendrier 📅", fontSize = 30.sp)

        if (selectedCourses.isNotEmpty()) {
            LazyColumn {
                items(selectedCourses) { course ->
                    Text("Cours: $course", fontSize = 20.sp)
                }
            }
        }

        if (savedEvents.isNotEmpty()) {
            Text("📌 Événements Rappelés :", fontSize = 24.sp)
            LazyColumn {
                items(savedEvents) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${event.title} - ${event.date}")
                        }
                    }
                }
            }
        }

        Button(onClick = { navController.navigate("agenda") }) {
            Text("Retour")
        }
    }
}
