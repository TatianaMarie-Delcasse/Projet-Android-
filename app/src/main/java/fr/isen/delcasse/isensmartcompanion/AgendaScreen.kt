package fr.isen.delcasse.isensmartcompanion

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun AgendaScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("selected_courses", Context.MODE_PRIVATE)
    val selectedCourses = prefs.getStringSet("selected_courses", emptySet())?.toList() ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCBFF5722)) // Fond orange clair
            .padding(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Agenda",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 50.dp)
        )

        Spacer(modifier = Modifier.height(150.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    navController.navigate("chooseCourses")
                },
                modifier = Modifier
                    .height(70.dp)
                    .width(160.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFE1D9D8)) // Bleu
            ) {
                Text("Choisir les cours", fontSize = 20.sp, color = Color.Black)
            }

            Button(
                onClick = {
                    navController.navigate("calendar")
                },
                modifier = Modifier
                    .height(70.dp)
                    .width(160.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFE1D9D8)) // Bleu
            ) {
                Text("Afficher l'agenda", fontSize = 20.sp, color = Color.Black)
            }
        }
    }
}