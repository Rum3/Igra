package noirdetective.game.domain.model

data class Evidence(
    val id: String,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val personalNotes: String? = null
)
