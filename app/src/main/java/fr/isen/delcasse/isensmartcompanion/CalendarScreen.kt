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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import fr.isen.delcasse.isensmartcompanion.data.Course
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun CalendarScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("selected_courses", Context.MODE_PRIVATE)
    val selectedCourses = prefs.all
        .filterKeys { it.startsWith("cours_") }
        .values
        .mapNotNull { it as? String }
        .map { Gson().fromJson(it, Course::class.java) }

    var savedEvents by remember { mutableStateOf<List<Event>>(emptyList()) }

    LaunchedEffect(Unit) {
        val agendaPrefs = context.getSharedPreferences("AgendaPrefs", Context.MODE_PRIVATE)
        val savedEventsSet = agendaPrefs.getStringSet("agenda_events", mutableSetOf()) ?: mutableSetOf()
        savedEvents = savedEventsSet.map { Gson().fromJson(it, Event::class.java) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                "Calendrier 📅",
                fontSize = 30.sp,
                color = Color(0xFF131313),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Titre "Cours Sélectionnés" toujours affiché
        item {
            Text("Cours Sélectionnés :", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(
                0xFF37853A
            )
            )
        }

        if (selectedCourses.isNotEmpty()) {
            // Affichage des cours sélectionnés
            items(selectedCourses) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xDAB5E38A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📚 ${course.title}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Jour : ${course.day}", fontSize = 16.sp)
                        Text("Heure : ${course.startTime} - ${course.endTime}", fontSize = 16.sp)
                        Text("Salle : ${course.location}", fontSize = 16.sp)
                    }
                }
            }
        } else {
            // Message lorsqu'aucun cours n'est sélectionné
            item {
                Text(
                    "Aucun cours sélectionné",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Section "Événements Rappelés"
        item {
            Text("📌 Événements Rappelés :", fontSize = 24.sp, color = Color(0xFFFF9800))
        }

        if (savedEvents.isNotEmpty()) {
            items(savedEvents) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xCDEA8542))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${event.title} - ${event.date}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        } else {
            item {
                Text(
                    "Aucun événement rappelé",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        item {
            Button(
                onClick = { navController.navigate("agenda") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(Color(0xCD232523))
            ) {
                Text("Retour")
            }
        }
    }
}
