package noirdetective.game.domain.model

data class NoteEntry(
    val id: Int = 0,
    val storyId: String,
    val content: String,
    val timestamp: Long
)
