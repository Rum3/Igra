package noirdetective.game.ui.screens.notebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import noirdetective.game.data.local.entity.EvidenceItem
import noirdetective.game.data.local.entity.NotebookEntry
import noirdetective.game.data.repository.EvidenceRepository
import noirdetective.game.data.repository.NotebookRepository
import javax.inject.Inject

@HiltViewModel
class NotebookViewModel @Inject constructor(
    private val evidenceRepository: EvidenceRepository,
    private val notebookRepository: NotebookRepository
) : ViewModel() {

    val collectedEvidence: StateFlow<List<EvidenceItem>> = evidenceRepository.allEvidence
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userNotes: StateFlow<List<NotebookEntry>> = notebookRepository.getNotesForStory("general")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNote(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val newNote = NotebookEntry(
                storyId = "general",
                content = content,
                timestamp = System.currentTimeMillis()
            )
            notebookRepository.insertNote(newNote)
        }
    }

    fun deleteNote(note: NotebookEntry) {
        viewModelScope.launch {
            notebookRepository.deleteNote(note)
        }
    }

    fun togglePinEvidence(item: EvidenceItem) {
        viewModelScope.launch {
            evidenceRepository.updateEvidence(item.copy(isPinned = !item.isPinned))
        }
    }
}
