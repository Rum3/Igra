package noirdetective.game.ui.screens.office

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import noirdetective.game.data.repository.EvidenceRepository
import noirdetective.game.data.repository.GameStateRepository
import javax.inject.Inject

@HiltViewModel
class OfficeViewModel @Inject constructor(
    private val evidenceRepository: EvidenceRepository,
    private val gameStateRepository: GameStateRepository
) : ViewModel() {

    var hasStartedCase by mutableStateOf(false)
    var hasVisitedJournal by mutableStateOf(false)
    var hasVisitedFiles by mutableStateOf(false)
    var hasVisitedWallBoard by mutableStateOf(false)
    var allInitialEvidenceViewed by mutableStateOf(false)

    init {
        checkProgress()
    }

    fun checkProgress() {
        viewModelScope.launch {
            val evidence = evidenceRepository.allEvidence.first()
            hasStartedCase = evidence.isNotEmpty()
            
            // Required chapters from Chapter 1 to unlock Chapter 2
            val requiredChapters = listOf(
                "chapter_01_2a", // Crime Scene Report
                "chapter_01_2b", // Suicide Note
                "chapter_01_2c", // Personal Effects
                "chapter_01_2g"  // Web Search
            )
            
            allInitialEvidenceViewed = gameStateRepository.hasVisitedAll(requiredChapters)
        }
    }

    fun canProgressToChapter2(): Boolean {
        return allInitialEvidenceViewed
    }
}
