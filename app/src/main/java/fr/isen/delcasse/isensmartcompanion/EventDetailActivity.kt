package fr.isen.delcasse.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.google.gson.Gson
import fr.isen.delcasse.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsonEvent = intent.getStringExtra("event_json") // Récupérer l'event en JSON
        val event = Gson().fromJson(jsonEvent, Event::class.java) // Convertir en objet Event

        setContent {
            ISENSmartCompanionTheme {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // 🔙 Bouton Retour avec une flèche (gris)
                    Button(
                        onClick = { finish() }, // Ferme l'activity et revient à la liste
                        modifier = Modifier.padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEEEEEE), // Gris clair
                            contentColor = Color.Black // Texte en noir
                        )
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retour aux événements", color = Color.Black) // Texte noir
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🟠 Titre principal (en dehors de la carte)
                    Text(
                        text = event.title,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)  // Couleur orange
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    // 🔲 Carte contenant la section Date / Lieu / Catégorie (gris)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE)), // Gris clair
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Date:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = event.date, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Lieu:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = event.location, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Catégorie:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = event.category, fontSize = 20.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))

                    // 🔹 Description de l'événement
                    Text(
                        text = event.description,
                        fontSize = 20.sp,
                        color = Color(0xFF0C0C0C)
                    )
                }
            }
        }
    }
}
