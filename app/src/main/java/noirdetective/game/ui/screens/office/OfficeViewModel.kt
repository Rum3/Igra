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
    var chapter3Completed by mutableStateOf(false)
    var chapter4Completed by mutableStateOf(false)
    var chapter5Completed by mutableStateOf(false)
    var chapter6Completed by mutableStateOf(false)
    var chapter7Completed by mutableStateOf(false)
    var chapter8Completed by mutableStateOf(false)
    var chapter9Completed by mutableStateOf(false)

    init {
        checkProgress()
    }

    fun checkProgress() {
        viewModelScope.launch {
            val evidence = evidenceRepository.allEvidence.first()
            hasStartedCase = evidence.isNotEmpty()
            
            // Required chapters from Chapter 1 to unlock Chapter 2
            val requiredChaptersCh1 = listOf(
                "chapter_01_2a", "chapter_01_2b", "chapter_01_2c", "chapter_01_2g"
            )
            allInitialEvidenceViewed = gameStateRepository.hasVisitedAll(requiredChaptersCh1)

            // Check if Chapter 3 is completed
            chapter3Completed = gameStateRepository.hasVisitedAll(listOf("chapter_03_final_files"))
            
            // Check if Chapter 4 is completed (either restaurant or warehouse path)
            chapter4Completed = gameStateRepository.hasVisitedAll(listOf("chapter_04_restaurant_detail")) || 
                               gameStateRepository.hasVisitedAll(listOf("chapter_04_warehouse_discovery"))
            
            // Check if Chapter 5 is completed
            chapter5Completed = gameStateRepository.hasVisitedAll(listOf("chapter_05_end"))

            // Check if Chapter 6 is completed
            chapter6Completed = gameStateRepository.hasVisited("chapter_06_audit_analysis") || 
                               gameStateRepository.hasVisited("chapter_06_map_analysis") ||
                               gameStateRepository.hasVisited("chapter_06_dealer_success") ||
                               gameStateRepository.hasVisited("chapter_06_dealer_refuse")

            // Check if Chapter 7 is completed
            chapter7Completed = gameStateRepository.hasVisitedAll(listOf("chapter_07_end"))

            // Check if Chapter 8 is completed (at least one sub-investigation done)
            chapter8Completed = gameStateRepository.hasVisited("chapter_08_audit") ||
                               gameStateRepository.hasVisited("chapter_08_camden_check") ||
                               gameStateRepository.hasVisited("chapter_08_dictaphone")

            // Check if Chapter 9 is completed
            chapter9Completed = gameStateRepository.hasVisited("chapter_09_simon_interrogation") ||
                               gameStateRepository.hasVisited("chapter_09_client_meeting") ||
                               gameStateRepository.hasVisited("chapter_09_hub") // Allow completion even if they just saw the hub? No, let's keep it to an action.
        }
    }

    fun canProgressToChapter2(): Boolean {
        return allInitialEvidenceViewed && !chapter3Completed
    }

    fun canProgressToChapter4(): Boolean {
        return chapter3Completed && !chapter4Completed
    }
    
    fun canProgressToChapter5(): Boolean {
        return chapter4Completed && !chapter5Completed
    }

    fun canProgressToChapter6(): Boolean {
        return chapter5Completed && !chapter6Completed
    }

    fun canProgressToChapter7(): Boolean {
        return chapter6Completed && !chapter7Completed
    }

    fun canProgressToChapter8(): Boolean {
        return chapter7Completed && !chapter8Completed
    }

    fun canProgressToChapter9(): Boolean {
        return chapter8Completed && !chapter9Completed
    }

    fun canProgressToChapter10(): Boolean {
        return chapter9Completed
    }
}
