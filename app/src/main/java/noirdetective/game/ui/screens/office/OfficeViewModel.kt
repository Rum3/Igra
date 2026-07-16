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
            chapter6Completed = gameStateRepository.hasVisitedAll(listOf("chapter_06_end"))
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
        return chapter6Completed
    }
}
