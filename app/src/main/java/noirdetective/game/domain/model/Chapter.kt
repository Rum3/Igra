package noirdetective.game.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val id: String,
    val title: String,
    val content: String,
    val choices: List<Choice>,
    val caseType: CaseType,
    val chapterNumber: Int,
    val backgroundImage: String? = null
)
