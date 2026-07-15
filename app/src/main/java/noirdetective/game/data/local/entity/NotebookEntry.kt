package noirdetective.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notebook_entries")
data class NotebookEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val storyId: String,
    val content: String,
    val timestamp: Long
)
