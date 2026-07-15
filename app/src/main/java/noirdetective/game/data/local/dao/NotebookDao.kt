package noirdetective.game.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import noirdetective.game.data.local.entity.NotebookEntry

@Dao
interface NotebookDao {
    @Query("SELECT * FROM notebook_entries WHERE storyId = :storyId ORDER BY timestamp DESC")
    fun getNotesForStory(storyId: String): Flow<List<NotebookEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotebookEntry)

    @Delete
    suspend fun deleteNote(note: NotebookEntry)
}
