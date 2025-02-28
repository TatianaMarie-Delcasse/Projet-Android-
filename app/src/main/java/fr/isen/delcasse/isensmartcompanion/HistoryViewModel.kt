package fr.isen.delcasse.isensmartcompanion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.delcasse.isensmartcompanion.data.Interaction
import fr.isen.delcasse.isensmartcompanion.data.InteractionDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class HistoryViewModel(val interactionDao: InteractionDao) : ViewModel() {

    private val _history = MutableStateFlow<List<Interaction>>(emptyList())
    val history: StateFlow<List<Interaction>> = _history

    init {
        viewModelScope.launch {
            interactionDao.getAllInteractions().collect { interactions ->
                _history.value = interactions
            }
        }
    }

    fun addInteraction(interaction: Interaction) {
        viewModelScope.launch(Dispatchers.IO) {
            interactionDao.insertInteraction(interaction)
        }
    }

    fun deleteInteraction(interaction: Interaction) {
        viewModelScope.launch(Dispatchers.IO) {
            interactionDao.deleteInteraction(interaction)
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            interactionDao.clearAll()
        }
    }
}
