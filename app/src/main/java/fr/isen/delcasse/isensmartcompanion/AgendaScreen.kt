package fr.isen.delcasse.isensmartcompanion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import fr.isen.delcasse.isensmartcompanion.data.Course
import fr.isen.delcasse.isensmartcompanion.data.Event
import fr.isen.delcasse.isensmartcompanion.data.Interaction
import fr.isen.delcasse.isensmartcompanion.data.AppDatabase
import kotlinx.coroutines.runBlocking

fun getStudentCourses(): List<Course> {
    return listOf(
        Course("Mathématiques", "Lundi", "08:00", "10:00", "Salle A101"),
        Course("Programmation", "Mardi", "10:00", "12:00", "Salle B202"),
        Course("Réseaux", "Mercredi", "14:00", "16:00", "Salle C303"),
        Course("IA & Big Data", "Jeudi", "09:00", "11:00", "Salle D404"),
        Course("Physique", "Vendredi", "13:00", "15:00", "Salle E505")
    )
}

@Composable
fun AgendaScreen(events: List<Event>, db: AppDatabase) {
    val studentCourses = getStudentCourses()
    val interactions = db.interactionDao().getAll().collectAsState(initial = emptyList()).value

    val filteredEvents = events.filter { event ->
        interactions.any { interaction ->
            interaction.message?.contains(event.title, ignoreCase = true) ?: false
        }
    }




    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Agenda",
            fontSize = 30.sp,
            color = Color(0xFFFF9800),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(studentCourses) { course ->
                CourseItem(course)
            }

            items(filteredEvents) { event ->
                AgendaItem(event)
            }
        }
    }
}


@Composable
fun CourseItem(course: Course) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD0F0C0)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "${course.day} : ${course.startTime} - ${course.endTime}", color = Color.DarkGray)
            Text(text = "Salle : ${course.location}", color = Color.Gray)
        }
    }
}

@Composable
fun AgendaItem(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF82B1FF)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Date : ${event.date}", color = Color.DarkGray)
            Text(text = "Lieu : ${event.location}", color = Color.Gray)
        }
    }
}
