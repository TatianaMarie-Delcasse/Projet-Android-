package fr.isen.delcasse.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.google.ai.client.generativeai.*
import fr.isen.delcasse.isensmartcompanion.data.AppDatabase
import fr.isen.delcasse.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import retrofit2.Call
import retrofit2.http.GET
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                val navController = rememberNavController()
                val database = AppDatabase.getDatabase(applicationContext)
                val interactionDao = database.interactionDao()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavigationGraph(
                        navController,
                        Modifier.padding(innerPadding),
                        interactionDao = interactionDao
                    )
                }
            }
        }
    }
}

// Interface Retrofit pour récupérer les événements
interface RetrofitService {
    @GET("events.json")
    fun getEvents(): Call<List<Event>>
}