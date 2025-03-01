package fr.isen.delcasse.isensmartcompanion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import fr.isen.delcasse.isensmartcompanion.data.Interaction
import fr.isen.delcasse.isensmartcompanion.data.InteractionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainScreen(interactionDao: InteractionDao) {
    AssistantUI(interactionDao)
}

@Composable
fun AssistantUI(interactionDao: InteractionDao) {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    val viewModel = remember { HistoryViewModel(interactionDao) }
    val history by viewModel.history.collectAsState()

    val apiKey = "AIzaSyDyrL1QqsgY6zBBR7Wb3Gl_0E1onoY1tc4"
    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCBFF5722)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(text = "ISEN", fontSize = 60.sp, color = Color.Red)
        Text(text = "Smart Companion", fontSize = 20.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(history.reversed().chunked(2)) { messages ->
                if (messages.size == 2) {
                    MessageCard(messages[0], messages[1])
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                placeholder = { Text("Posez votre question...") },
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    if (userInput.text.isNotEmpty()) {
                        val question = userInput.text
                        viewModel.addInteraction(Interaction(sender = "User", message = question))
                        userInput = TextFieldValue("")

                        CoroutineScope(Dispatchers.IO).launch {
                            getGeminiResponse(generativeModel, question) { response ->
                                viewModel.addInteraction(Interaction(sender = "AI", message = response))
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Red, shape = CircleShape)
            ) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Envoyer", tint = Color.White)
            }
        }
    }
}

fun getGeminiResponse(model: GenerativeModel, input: String, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = model.generateContent(input)
            val textResponse = response.text ?: "Erreur lors de la récupération de la réponse."

            withContext(Dispatchers.Main) {
                onResult(textResponse)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult("❌ Erreur : ${e.message}")
            }
        }
    }
}

@Composable
fun MessageCard(question: Interaction, response: Interaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1D9D8))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "👤 ${question.sender}", fontSize = 16.sp, color = Color.Blue)
            Text(text = question.message, fontSize = 16.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "🤖 ${response.sender}", fontSize = 16.sp, color = Color.Blue)
            Text(text = response.message, fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}