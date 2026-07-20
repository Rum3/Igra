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

    // Chapter 2, 5, 6 & 8 Mechanic
    var actionPoints by mutableIntStateOf(3)
    private val hubChoices = mutableSetOf<String>()

    fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            isLoading = true
            
            // Sync visited chapters from database
            val allVisited = gameStateRepository.getAllVisitedChapters()
            _visitedChapters.clear()
            _visitedChapters.addAll(allVisited)

            // Reset Action Points for Chapter 2 Hub
            val isReturningFromCh2Sub = currentChapter?.id?.startsWith("chapter_02_2") == true
            if (chapterId == "chapter_02_1" && !isReturningFromCh2Sub) {
                actionPoints = 3
                hubChoices.clear()
            }

            // Reset Action Points for Chapter 5 Hub
            val isReturningFromCh5Sub = currentChapter?.id?.startsWith("chapter_05_ask") == true || 
                                       currentChapter?.id?.startsWith("chapter_05_meeting") == true
            if (chapterId == "chapter_05_camden_hub" && !isReturningFromCh5Sub) {
                actionPoints = 2
                hubChoices.clear()
            }

            // Reset Action Points for Chapter 6 Hub
            val isReturningFromCh6Sub = currentChapter?.id?.startsWith("chapter_06_") == true && 
                                       currentChapter?.id != "chapter_06_1"
            if ((chapterId == "chapter_06_1" || chapterId == "debug_chapter_06_res") && !isReturningFromCh6Sub) {
                actionPoints = 2
                hubChoices.clear()
            }

            // Reset Action Points for Chapter 8 Hub
            val isReturningFromCh8Sub = currentChapter?.id?.startsWith("chapter_08_") == true && 
                                       currentChapter?.id != "chapter_08_hub"
            if ((chapterId == "chapter_08_hub" || chapterId == "debug_chapter_08") && !isReturningFromCh8Sub) {
                actionPoints = 2
                hubChoices.clear()
            }

            // --- DEBUG REDIRECTS ---
            var targetChapterId = chapterId
            
            if (chapterId == "debug_chapter_08") {
                targetChapterId = "chapter_08_1"
                // REMOVE success state from DB and memory for this test
                gameStateRepository.deleteChapterVisit("chapter_06_dealer_success")
                _visitedChapters.remove("chapter_06_dealer_success")
                
                // Record refusal/mystery state
                gameStateRepository.recordChapterVisit("chapter_06_dealer_refuse")
                _visitedChapters.add("chapter_06_dealer_refuse")

                // Ensure history exists for Chapter 7
                gameStateRepository.recordChapterVisit("chapter_07_end")
                if (!_visitedChapters.contains("chapter_07_end")) _visitedChapters.add("chapter_07_end")
            }

            // --- LOGIC CHECKS ---
            if (chapterId == "chapter_08_camden_check") {
                // If we are in debug_chapter_08, we've already removed success from memory
                val knowsCleaners = _visitedChapters.contains("chapter_06_dealer_success")
                targetChapterId = if (knowsCleaners) "chapter_08_camden_cleaners" else "chapter_08_camden_mystery"
            }

            if (chapterId == "chapter_03_check_outcome") {
                val hasSuccess = _visitedChapters.contains("chapter_02_outcome_success")
                targetChapterId = if (hasSuccess) "chapter_03_success_branch" else "chapter_03_fail_branch"
            }

            if (chapterId == "chapter_02_1" && actionPoints == 0) {
                val hasAutopsy = _visitedChapters.contains("chapter_02_2b") || hubChoices.contains("chapter_02_2b")
                val hasArthur = _visitedChapters.contains("chapter_02_2d") || hubChoices.contains("chapter_02_2d")
                val hasRestaurant = _visitedChapters.contains("chapter_02_2a") || hubChoices.contains("chapter_02_2a")
                
                targetChapterId = if (hasAutopsy && hasArthur && !hasRestaurant) {
                    "chapter_02_outcome_success"
                } else {
                    "chapter_02_outcome_fail"
                }
            }

            if (chapterId == "chapter_05_camden_hub" && actionPoints == 0) targetChapterId = "chapter_05_end"

            if (chapterId == "chapter_06_study_logic") {
                targetChapterId = if (_visitedChapters.contains("chapter_04_restaurant_detail")) {
                    "chapter_06_audit_analysis"
                } else {
                    "chapter_06_map_analysis"
                }
            }

            // --- FINAL LOAD ---
            currentChapter = storyRepository.getChapter(targetChapterId)
            
            gameStateRepository.recordChapterVisit(targetChapterId)
            if (targetChapterId != chapterId) gameStateRepository.recordChapterVisit(chapterId)
            if (!_visitedChapters.contains(targetChapterId)) _visitedChapters.add(targetChapterId)

            // --- EVIDENCE LOGGING ---
            when (targetChapterId) {
                "chapter_02_2b_full" -> addChapter2Evidence("autopsy")
                "chapter_02_2c_final_success" -> addChapter2Evidence("interrogation")
                "chapter_03_profile_reveal", "chapter_03_profile_reveal_puzzle" -> addChapter3Evidence("sterling_profile")
                "chapter_03_final_files", "chapter_03_final_files_puzzle" -> addChapter3Evidence("atlas_chat")
                "chapter_04_restaurant_detail" -> addChapter4Evidence("atlas_audit")
                "chapter_04_warehouse_discovery" -> addChapter4Evidence("syndicate_logistics")
                "chapter_05_drugs_path" -> addChapter5Evidence()
                "chapter_06_dealer_success" -> addChapter6Evidence("the_cleaners")
                "chapter_06_audit_analysis", "chapter_06_map_analysis" -> addChapter6Evidence("financial_schemes")
                "chapter_07_end" -> addChapter7Evidence()
            }
            
            isLoading = false
        }
    }

    private suspend fun addChapter2Evidence(type: String) {
        when (type) {
            "autopsy" -> evidenceRepository.updateEvidence(EvidenceItem("autopsy_report", "Autopsy Report", "Physiological death: 21:30 - 22:30.", "Contradicts early police reports.", isCollected = true))
            "interrogation" -> evidenceRepository.updateEvidence(EvidenceItem("mr_sterling", "Mr. Sterling Lead", "Vane had a secret meeting as 'Mr. Sterling'.", "Arthur says Vane was terrified.", isCollected = true, isPinned = true))
        }
    }

    private suspend fun addChapter3Evidence(type: String) {
        when (type) {
            "sterling_profile" -> evidenceRepository.updateEvidence(EvidenceItem("julian_sterling", "Julian Sterling Dossier", "Senior Analyst at Atlas.", "Vane's mentor and informant. Missing.", isCollected = true, isPinned = true))
            "atlas_chat" -> evidenceRepository.updateEvidence(EvidenceItem("atlas_chat", "Vane-Spider Chat Log", "Atlas linked to the Syndicate.", "Confirmed criminal money laundering.", isCollected = true))
        }
    }

    private suspend fun addChapter4Evidence(type: String) {
        when (type) {
            "atlas_audit" -> evidenceRepository.updateEvidence(EvidenceItem("atlas_audit", "Atlas Internal Audit", "Lists executives in red ink.", "Proof of circular debt schemes.", isCollected = true))
            "syndicate_logistics" -> evidenceRepository.updateEvidence(EvidenceItem("syndicate_logistics", "Logistics Map", "Cash drop-off points.", "Maps the flow of black money.", isCollected = true, isPinned = true))
        }
    }

    private suspend fun addChapter5Evidence() {
        evidenceRepository.updateEvidence(EvidenceItem("vane_vial", "Mysterious Vial", "Vane's initials found in an alley.", "Potential drug connection.", isCollected = true))
    }

    private suspend fun addChapter6Evidence(type: String) {
        when (type) {
            "the_cleaners" -> evidenceRepository.updateEvidence(EvidenceItem("the_cleaners", "The Cleaners", "A hit squad used by Atlas.", "They operate out of Camden.", isCollected = true))
            "financial_schemes" -> evidenceRepository.updateEvidence(EvidenceItem("atlas_schemes", "Circular Debt Scheme", "Proof of money laundering.", "Needed to dismantle Atlas Foundation.", isCollected = true, isPinned = true))
        }
    }

    private suspend fun addChapter7Evidence() {
        evidenceRepository.updateEvidence(EvidenceItem("surveillance_alert", "Surveillance Photo", "A photo of your office taken from the street.", "Received after meeting Alistair Thorne.", isCollected = true, isPinned = true))
    }

    fun makeChoice(choice: Choice) {
        viewModelScope.launch {
            var nextId = choice.nextChapterId
            if (nextId == "chapter_02_2c_a_roll") {
                nextId = if ((1..100).random() > 50) "chapter_02_2c_a_success" else "chapter_02_2c_a_blocked"
            }
            if ((currentChapter?.id == "chapter_02_1" || currentChapter?.id == "chapter_05_camden_hub" || currentChapter?.id == "chapter_06_1" || currentChapter?.id == "chapter_08_hub") && actionPoints > 0) {
                actionPoints--
                hubChoices.add(nextId)
            }
            loadChapter(nextId)
        }
    }
}
