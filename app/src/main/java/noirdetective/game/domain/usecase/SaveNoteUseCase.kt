package noirdetective.game.domain.usecase

import noirdetective.game.data.repository.NotebookRepository
import noirdetective.game.data.local.entity.NotebookEntry

class SaveNoteUseCase(private val repository: NotebookRepository) {
    suspend operator fun invoke(storyId: String, content: String) {
        val note = NotebookEntry(
            storyId = storyId,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        repository.insertNote(note)
    }
}
