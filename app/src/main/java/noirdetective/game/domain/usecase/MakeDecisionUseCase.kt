package noirdetective.game.domain.usecase

import noirdetective.game.data.repository.GameStateRepository
import noirdetective.game.data.local.entity.GameProgress
import noirdetective.game.domain.model.Choice

class MakeDecisionUseCase(private val repository: GameStateRepository) {
    suspend operator fun invoke(storyId: String, choice: Choice) {
        val progress = GameProgress(
            storyId = storyId,
            currentChapterId = choice.nextChapterId,
            lastUpdateTime = System.currentTimeMillis()
        )
        repository.saveProgress(progress)
    }
}
