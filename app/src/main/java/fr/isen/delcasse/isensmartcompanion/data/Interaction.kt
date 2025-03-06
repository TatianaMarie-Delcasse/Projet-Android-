package fr.isen.delcasse.isensmartcompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interaction_table")
data class Interaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String,   // User ou AI
    val message: String,  // Le texte du message
    val timestamp: Long = System.currentTimeMillis() // Timestamp automatique
)