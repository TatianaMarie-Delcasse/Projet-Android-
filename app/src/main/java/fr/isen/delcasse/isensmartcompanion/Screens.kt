package fr.isen.delcasse.isensmartcompanion

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainScreen() {
    AssistantUI()
}

@Composable
fun AssistantUI() {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    val messages = remember { mutableStateListOf<Pair<String, String>>() }

    // 🔥 Configuration du modèle Gemini AI
    val apiKey = "AIzaSyDyrL1QqsgY6zBBR7Wb3Gl_0E1onoY1tc4" // Remplace par ta clé API Google AI
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

        Text(
            text = "ISEN",
            fontSize = 60.sp,
            color = Color.Red
        )

        Text(
            text = "Smart Companion",
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages.reversed()) { (question, answer) ->
                MessageBubble(question, answer)
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
                        messages.add(question to "⏳ Chargement...")
                        userInput = TextFieldValue("")

                        // 🔥 Appel à l'API Gemini AI
                        getGeminiResponse(generativeModel, question) { response ->
                            if (messages.isNotEmpty()) { // Vérification avant suppression
                                messages.removeAt(messages.size - 1) // Supprime le message "⏳ Chargement..."
                            }
                            messages.add(question to response)
                        }
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Red, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Envoyer",
                    tint = Color.White
                )
            }
        }
    }
}

// ✅ Fonction qui envoie la requête à Gemini AI
fun getGeminiResponse(model: GenerativeModel, input: String, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = model.generateContent(input) // Exécute dans une coroutine
            val textResponse = response.text ?: "Erreur lors de la récupération de la réponse."

            withContext(Dispatchers.Main) { // Retourne sur le thread principal pour mettre à jour l'UI
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
fun MessageBubble(question: String, answer: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "👤 $question",
            fontSize = 16.sp,
            color = Color.Blue,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🤖 $answer",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(4.dp)
        )
    }
}
