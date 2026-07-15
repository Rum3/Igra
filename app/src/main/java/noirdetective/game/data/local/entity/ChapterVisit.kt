package noirdetective.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapter_visits")
data class ChapterVisit(
    @PrimaryKey val chapterId: String,
    val timestamp: Long
)
