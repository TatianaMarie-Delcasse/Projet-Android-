package fr.isen.delcasse.isensmartcompanion

import fr.isen.delcasse.isensmartcompanion.AgendaScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import fr.isen.delcasse.isensmartcompanion.data.AppDatabase
import fr.isen.delcasse.isensmartcompanion.data.InteractionDao
import fr.isen.delcasse.isensmartcompanion.data.Interaction
import android.content.Context
import androidx.room.Room
import fr.isen.delcasse.isensmartcompanion.data.Event
import fr.isen.delcasse.isensmartcompanion.ChooseCourseScreen


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("home", "events", "history", "agenda")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.CalendarToday, Icons.Filled.History, Icons.Filled.DateRange)
    val labels = listOf("Accueil", "Événements", "Historique", "Agenda")


    var selectedItem by rememberSaveable { mutableStateOf(0) }

    NavigationBar {
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = labels[index]) },
                label = { Text(labels[index]) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(screen)
                }
            )
        }
    }
}

fun getEventsWithReminders(context: Context, events: List<Interaction>): List<Interaction> {
    val prefs = context.getSharedPreferences("event_prefs", Context.MODE_PRIVATE)
    return events.filter { prefs.getBoolean(it.id.toString(), false) }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier,
    interactionDao: InteractionDao
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("home") { AssistantUI(interactionDao) }
        composable("events") { EventsScreen(navController) }
        composable("history") { HistoryScreen(interactionDao) }
        composable(route = "agenda") { AgendaScreen(navController) }
        composable("chooseCourses") {
            ChooseCourseScreen(navController)
        }
        composable("calendar") { CalendarScreen(navController) }
    }
}




