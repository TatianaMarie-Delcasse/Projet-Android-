package fr.isen.delcasse.isensmartcompanion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
            items(history.chunked(2)) { interactionPair ->
                if (interactionPair.size == 2) {
                    HistoryItem(interactionPair[0], interactionPair[1], onDelete = {
                        viewModel.deleteInteraction(interactionPair[0])
                        viewModel.deleteInteraction(interactionPair[1])
                    })
                }
            }
        }
    }
}

@Composable
fun HistoryItem(userInteraction: Interaction, aiInteraction: Interaction, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xC8E8A892))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "👤 ${userInteraction.sender}", fontWeight = FontWeight.Bold)
            Text(text = userInteraction.message, color = Color.Black)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "🤖 ${aiInteraction.sender}", fontWeight = FontWeight.Bold)
            Text(text = aiInteraction.message, color = Color.Black)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "📅 ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(userInteraction.timestamp))}",
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                }
            }
        }
    }
}
