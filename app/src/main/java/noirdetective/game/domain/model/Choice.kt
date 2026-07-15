package noirdetective.game.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Choice(
    val id: String,
    val text: String,
    val nextChapterId: String,
    val consequences: String? = null
)
