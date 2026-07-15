package noirdetective.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decision_log")
data class DecisionLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chapterId: String,
    val choiceId: String,
    val timestamp: Long
)
