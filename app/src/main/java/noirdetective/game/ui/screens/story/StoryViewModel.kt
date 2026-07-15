package noirdetective.game.ui.screens.story

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import noirdetective.game.data.local.entity.EvidenceItem
import noirdetective.game.data.repository.EvidenceRepository
import noirdetective.game.data.repository.GameStateRepository
import noirdetective.game.data.repository.StoryRepository
import noirdetective.game.domain.model.Chapter
import noirdetective.game.domain.model.Choice
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val gameStateRepository: GameStateRepository,
    private val evidenceRepository: EvidenceRepository
) : ViewModel() {

    var currentChapter by mutableStateOf<Chapter?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val _visitedChapters = mutableStateListOf<String>()
    val visitedChapters: List<String> get() = _visitedChapters

    // Chapter 2 Mechanic
    var actionPoints by mutableIntStateOf(3)
    private val chapter2Choices = mutableSetOf<String>()

    fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            isLoading = true
            
            // Reset Action Points if starting Chapter 2 fresh (from Office or Debug)
            // But DON'T reset if returning from any sub-screen of Chapter 2
            val isReturningFromSubScreen = currentChapter?.id?.startsWith("chapter_02_2") == true
            
            if (chapterId == "chapter_02_1" && !isReturningFromSubScreen) {
                actionPoints = 3
                chapter2Choices.clear()
            }

            // Logic for Chapter 2 redirection
            var targetChapterId = chapterId
            if (chapterId == "chapter_02_1" && actionPoints == 0) {
                // Success only if you visited BOTH Autopsy and Arthur (and didn't waste points on the restaurant)
                val hasAutopsy = chapter2Choices.contains("chapter_02_2b") || chapter2Choices.contains("chapter_02_2b_full")
                val hasArthur = chapter2Choices.contains("chapter_02_2c_final_success")
                val hasRestaurant = chapter2Choices.contains("chapter_02_2a") || chapter2Choices.contains("chapter_02_2a_enter")
                
                targetChapterId = if (hasAutopsy && hasArthur && !hasRestaurant) {
                    "chapter_02_outcome_success"
                } else {
                    "chapter_02_outcome_fail"
                }
            }

            currentChapter = storyRepository.getChapter(targetChapterId)
            
            // Record visit to database
            gameStateRepository.recordChapterVisit(targetChapterId)
            
            if (!_visitedChapters.contains(targetChapterId)) {
                _visitedChapters.add(targetChapterId)
            }
            
            // Logic for Chapter 1 initial evidence
            val initialEvidence = listOf("chapter_01_2a", "chapter_01_2b", "chapter_01_2c", "chapter_01_2g")
            if (targetChapterId in initialEvidence) {
                addInitialEvidence(targetChapterId)
            }

            // Logic for Chapter 2 evidence collection
            if (targetChapterId == "chapter_02_2b_full") {
                addChapter2Evidence("autopsy")
            } else if (targetChapterId == "chapter_02_2c_final_success") {
                addChapter2Evidence("interrogation")
            }
            
            isLoading = false
        }
    }

    private suspend fun addInitialEvidence(chapterId: String) {
        when (chapterId) {
            "chapter_01_2c" -> {
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "wristwatch",
                    name = "Silver Wristwatch",
                    shortDescription = "Broken silver wristwatch, hands frozen at 11:42.",
                    fullDescription = "A high-quality piece. Damage suggests it was struck or dropped during a struggle.",
                    isCollected = true
                ))
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "wallet",
                    name = "Leather Wallet",
                    shortDescription = "Contains £120 in cash, ID, and Arthur's business card.",
                    fullDescription = "The cash suggests robbery wasn't the motive. On the back of Arthur Pendleton's card is a handwritten handle: @pendleton_res.",
                    isCollected = true
                ))
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "restaurant_receipt",
                    name = "Financial District Receipt",
                    shortDescription = "Receipt from a high-end restaurant, dated afternoon before death.",
                    fullDescription = "Found in the wallet. Might provide an alibi or a lead on who he was with.",
                    isCollected = true
                ))
            }
        }
    }

    private suspend fun addChapter2Evidence(type: String) {
        when (type) {
            "autopsy" -> {
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "autopsy_report",
                    name = "Autopsy Report Summary",
                    shortDescription = "Estimated time of death: 21:30 - 22:30. Minor bruising on wrist.",
                    fullDescription = "Official post-mortem. Physiological death occurred earlier than the reported police discovery.",
                    isCollected = true
                ))
            }
            "interrogation" -> {
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "mr_sterling",
                    name = "Mr. Sterling Reservation",
                    shortDescription = "Meeting at 'The Blackwood' at 8:30 PM under alias 'Mr. Sterling'.",
                    fullDescription = "Information from Vane's assistant. Vane explicitly wanted this meeting wiped from his digital records.",
                    isCollected = true,
                    isPinned = true // Automatically pin this as it's a key lead
                ))
            }
        }
    }

    fun makeChoice(choice: Choice) {
        viewModelScope.launch {
            var nextId = choice.nextChapterId
            
            // Special logic for Arthur's pressure path (50% chance of failure)
            if (nextId == "chapter_02_2c_a_roll") {
                val roll = (1..100).random()
                nextId = if (roll > 50) "chapter_02_2c_a_success" else "chapter_02_2c_a_blocked"
            }

            // Check if we are making a move in Chapter 2 Hub
            if (currentChapter?.id == "chapter_02_1" && actionPoints > 0) {
                actionPoints--
                chapter2Choices.add(nextId)
            }
            loadChapter(nextId)
        }
    }
}
