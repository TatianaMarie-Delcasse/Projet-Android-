package fr.isen.delcasse.isensmartcompanion.data


//import Interaction
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionDao {
    @Query("SELECT * FROM interaction_table")
    fun getAll(): Flow<List<Interaction>>

    @Query("SELECT * FROM interaction_table ORDER BY timestamp DESC")
    fun getAllInteractions(): Flow<List<Interaction>>

    @Insert
    fun insertInteraction(interactions: Interaction)

    @Delete
    fun deleteInteraction(interaction: Interaction)

    @Query("DELETE FROM interaction_table")
    fun clearAll()

    @Query("SELECT COUNT(*) FROM interaction_table WHERE message LIKE '%' || :eventTitle || '%'")
    fun countInteraction(eventTitle: String): Int

}