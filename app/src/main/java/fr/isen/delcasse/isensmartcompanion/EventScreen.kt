package fr.isen.delcasse.isensmartcompanion

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
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
import com.google.gson.Gson
import androidx.navigation.NavHostController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import fr.isen.delcasse.isensmartcompanion.RetrofitInstance
import retrofit2.http.GET


// Modèle d'événement
data class Event(
    val id: String, // Store id as a String
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String
) {
    fun getNumericId(): Int? {
        return id.toIntOrNull()
    } // Returns null if conversion fails } }
}

// Liste factice d'événements
val fakeEvents = listOf(
    Event("1", "WEI 2024-2025", "Week-end d'intégration de folie avec les hippies", "14 au 16 octobre 2024", "Camping des Cigales", "Evenement"),
    Event("2", "Journée Cohésion", "Une journée pour souder les étudiants.", "13/10/2025", "Campus ISEN", "Activité"),
    Event("3", "Soirée BDE", "Une super soirée organisée par le BDE.", "10/03/2025", "Salle des fêtes", "Fête"),
    Event("4", "Gala ISEN", "Le gala annuel de l'ISEN.", "20/04/2025", "Palais des Congrès", "Cérémonie"),
    Event("5", "Hackathon", "Concours de programmation sur 24h", "10/06/2025", "Salle informatique", "Compétition"),
)

//Create a Retrofit interface
//interface RetrofitService {
// @GET("events.json") // The URL is part of the base URL in Retrofit setup.
//fun getEvents(): Call<List<Event>> // This will return a list of Event objects
//}

@Composable
fun EventsScreen(navController: NavHostController) {
    val context = LocalContext.current
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        RetrofitInstance.retrofitService.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    events = response.body() ?: emptyList()
                    isLoading = false
                } else {
                    errorMessage = "Failed to fetch events"
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                errorMessage = "Error: ${t.localizedMessage}"
                isLoading = false
            }
        })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Événements", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(events) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                val gson = Gson()
                                val intent = Intent(context, EventDetailActivity::class.java)
                                val jsonEvent = gson.toJson(event)
                                intent.putExtra("event_json", jsonEvent)
                                context.startActivity(intent)
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xDDFF5722))
                    ) {
                        Text(
                            text = event.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xEDFF5722)),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Historique des événements", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
