package fr.isen.delcasse.isensmartcompanion

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
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

    // Gradient background for a more modern look
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFF36A3E), Color(0xFFEF5549))  // Degraded orange-red
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground) // Use gradient background
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Center the content
    ) {
        Spacer(modifier = Modifier.height(40.dp)) // Top spacer for spacing

        Text(
            "Agenda",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White, // Title color white for better contrast
            modifier = Modifier.padding(bottom = 30.dp)
        )

        // Adding some more space here for better alignment
        Spacer(modifier = Modifier.height(100.dp))

        // Using Column to stack buttons vertically
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally, // Center the buttons horizontally
            verticalArrangement = Arrangement.spacedBy(20.dp) // Spacing between buttons
        ) {
            // Button 1: "Choisir les cours"
            Button(
                onClick = {
                    navController.navigate("chooseCourses")
                },
                modifier = Modifier
                    .height(80.dp)  // Increase the height
                    .fillMaxWidth(0.8f), // Set the width to 80% of the parent
                shape = RoundedCornerShape(12.dp), // Rounded corners for a modern look
                colors = ButtonDefaults.buttonColors(Color(0xFFF39915)) // A nice orange color for buttons
            ) {
                Text("Choisir les cours", fontSize = 20.sp, color = Color.White)
            }

            // Button 2: "Afficher l'agenda"
            Button(
                onClick = {
                    navController.navigate("calendar")
                },
                modifier = Modifier
                    .height(80.dp)  // Increase the height
                    .fillMaxWidth(0.8f), // Set the width to 80% of the parent
                shape = RoundedCornerShape(12.dp), // Rounded corners
                colors = ButtonDefaults.buttonColors(Color(0xFFF39915)) // Same color for consistency
            ) {
                Text("Afficher l'agenda", fontSize = 20.sp, color = Color.White)
            }
        }
    }
}
