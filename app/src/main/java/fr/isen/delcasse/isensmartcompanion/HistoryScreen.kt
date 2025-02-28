package fr.isen.delcasse.isensmartcompanion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.delcasse.isensmartcompanion.data.Interaction
import fr.isen.delcasse.isensmartcompanion.data.InteractionDao
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun HistoryScreen(interactionDao: InteractionDao) {
    val viewModel = remember { HistoryViewModel(interactionDao) }
    val history by viewModel.history.collectAsState()


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Historique des conversations",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.clearAll() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("🗑️ Supprimer tout l'historique")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(history) { interaction: Interaction ->
                HistoryItem(interaction, onDelete = { viewModel.deleteInteraction(interaction) })
            }
        }
    }
}


@Composable
fun HistoryItem(interaction: Interaction, onDelete: () -> Unit)
{
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBB86FC))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "👤 ${interaction.sender}", fontWeight = FontWeight.Bold)
            Text(text = "🤖 ${interaction.message}", color = Color.Gray)
            Text(
                text = "📅 ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(interaction.timestamp))}",
                fontSize = 12.sp
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer")
            }
        }
    }
}