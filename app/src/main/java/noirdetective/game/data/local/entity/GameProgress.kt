package noirdetective.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_progress")
data class GameProgress(
    @PrimaryKey val storyId: String,
    val currentChapterId: String,
    val lastUpdateTime: Long
)
