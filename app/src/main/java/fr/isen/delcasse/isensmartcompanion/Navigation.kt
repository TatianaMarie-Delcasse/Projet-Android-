package fr.isen.delcasse.isensmartcompanion

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

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("home", "events", "history")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.CalendarToday, Icons.Filled.History)
    val labels = listOf("Accueil", "Événements", "Historique")

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

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier,
    interactionDao: InteractionDao // Ajout du paramètre manquant
) {
    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("home") { AssistantUI(interactionDao = interactionDao) }
        composable("events") { EventsScreen(navController) }
        composable("history") { HistoryScreen(interactionDao) } // Passe bien interactionDao ici
    }
}


