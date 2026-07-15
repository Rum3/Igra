package noirdetective.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evidence_items")
data class EvidenceItem(
    @PrimaryKey val id: String,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val personalNotes: String? = null,
    val isCollected: Boolean = false,
    val isPinned: Boolean = false
)
