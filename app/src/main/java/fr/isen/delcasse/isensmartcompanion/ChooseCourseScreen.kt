package fr.isen.delcasse.isensmartcompanion

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.gson.reflect.TypeToken
import fr.isen.delcasse.isensmartcompanion.data.Course
import java.io.InputStreamReader

@Composable
fun ChooseCourseScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("selected_courses", Context.MODE_PRIVATE)
    val selectedCourses = remember { mutableStateListOf<String>() }

    val courses = remember {
        val inputStream = context.resources.openRawResource(R.raw.courses)
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Course>>() {}.type
        Gson().fromJson<List<Course>>(reader, type)
    }

    // Charger les cours enregistrés au démarrage
    LaunchedEffect(Unit) {
        val savedCourses = prefs.all.keys.filter { it.startsWith("cours_") }
        savedCourses.forEach { key ->
            prefs.getString(key, null)?.let { json ->
                selectedCourses.add(json)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sélectionnez vos cours",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(courses) { course ->
                val jsonCourse = Gson().toJson(course)
                val isSelected = selectedCourses.contains(jsonCourse)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            if (isSelected) {
                                selectedCourses.remove(jsonCourse)
                            } else {
                                selectedCourses.add(jsonCourse)
                            }
                        },
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFEA8F77) else Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📚 ${course.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "🗓️ Jour: ${course.day}")
                        Text(text = "⏰ Horaire: ${course.startTime} - ${course.endTime}")
                        Text(text = "🏫 Salle: ${course.location}")

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (isSelected) {
                                        selectedCourses.remove(jsonCourse)
                                    } else {
                                        selectedCourses.add(jsonCourse)
                                    }
                                }
                            )
                            Text(text = "Sélectionner ce cours")
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val editor = prefs.edit()
                // Efface toutes les données avant de sauvegarder
                prefs.all.keys.filter { it.startsWith("cours_") }.forEach { key ->
                    editor.remove(key)
                }

                // Sauvegarde des cours sélectionnés
                selectedCourses.forEach { json ->
                    val course = Gson().fromJson(json, Course::class.java)
                    editor.putString("cours_${course.title}", json)
                }
                editor.apply()

                Toast.makeText(context, "Cours enregistrés ✅", Toast.LENGTH_SHORT).show()

                navController.navigate("agenda") {
                    popUpTo("chooseCourse") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50))
        ) {
            Text("Valider")
        }
    }
}
