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

    // Chapter 2 & 5 Mechanic
    var actionPoints by mutableIntStateOf(3)
    private val hubChoices = mutableSetOf<String>()

    fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            isLoading = true
            
            // Reset Action Points for Chapter 2 Hub
            val isReturningFromCh2Sub = currentChapter?.id?.startsWith("chapter_02_2") == true
            if (chapterId == "chapter_02_1" && !isReturningFromCh2Sub) {
                actionPoints = 3
                hubChoices.clear()
            }

            // Reset Action Points for Chapter 5 Hub
            val isReturningFromCh5Sub = currentChapter?.id?.startsWith("chapter_05_ask") == true || 
                                       currentChapter?.id == "chapter_05_meeting_start" ||
                                       currentChapter?.id == "chapter_05_meeting_details"
            if (chapterId == "chapter_05_camden_hub" && !isReturningFromCh5Sub) {
                actionPoints = 2
                hubChoices.clear()
            }

            // Reset Action Points for Chapter 6 Hub
            val isReturningFromCh6Sub = currentChapter?.id?.startsWith("chapter_06_") == true && 
                                       currentChapter?.id != "chapter_06_1"
            if (chapterId == "chapter_06_1" && !isReturningFromCh6Sub) {
                actionPoints = 2
                hubChoices.clear()
            }

            // Logic for Chapter 2 redirection
            var targetChapterId = chapterId

            // Debug shortcuts (Hidden in title)
            if (chapterId == "debug_chapter_03") {
                targetChapterId = "chapter_03_1"
                // Simulate failure in Ch 2:
                if (!_visitedChapters.contains("chapter_02_outcome_fail")) {
                    _visitedChapters.add("chapter_02_outcome_fail")
                }
                _visitedChapters.remove("chapter_02_outcome_success")
            }
            
            if (chapterId == "debug_chapter_04") {
                targetChapterId = "chapter_04_1"
                // Simulate completion of Ch 3
                if (!visitedChapters.contains("chapter_03_final_files")) {
                    _visitedChapters.add("chapter_03_final_files")
                }
            }

            if (chapterId == "debug_chapter_05") {
                targetChapterId = "chapter_05_1"
                // Simulate completion of Ch 4
                if (!visitedChapters.contains("chapter_04_restaurant_detail")) {
                    _visitedChapters.add("chapter_04_restaurant_detail")
                }
            }

            if (chapterId == "debug_chapter_06_res") {
                targetChapterId = "chapter_06_1"
                // Simulate Restaurant Path from Ch 4
                if (!visitedChapters.contains("chapter_04_restaurant_detail")) {
                    _visitedChapters.add("chapter_04_restaurant_detail")
                }
                // Simulate completion of Ch 3 and Ch 5
                if (!visitedChapters.contains("chapter_03_final_files")) _visitedChapters.add("chapter_03_final_files")
                if (!visitedChapters.contains("chapter_05_end")) _visitedChapters.add("chapter_05_end")
            }

            if (chapterId == "chapter_03_check_outcome") {
                targetChapterId = if (_visitedChapters.contains("chapter_02_outcome_success")) {
                    "chapter_03_success_branch"
                } else {
                    "chapter_03_fail_branch"
                }
            }

            if (chapterId == "chapter_02_1" && actionPoints == 0) {
                val hasAutopsy = hubChoices.contains("chapter_02_2b") || hubChoices.contains("chapter_02_2b_full")
                val hasArthur = hubChoices.contains("chapter_02_2c_final_success")
                val hasRestaurant = hubChoices.contains("chapter_02_2a") || hubChoices.contains("chapter_02_2a_enter")
                
                targetChapterId = if (hasAutopsy && hasArthur && !hasRestaurant) {
                    "chapter_02_outcome_success"
                } else {
                    "chapter_02_outcome_fail"
                }
            }

            // Logic for Chapter 5 redirection
            if (chapterId == "chapter_05_camden_hub" && actionPoints == 0) {
                targetChapterId = "chapter_05_end"
            }

            // Logic for Chapter 6 redirection
            if (chapterId == "chapter_06_study_logic") {
                targetChapterId = if (_visitedChapters.contains("chapter_04_restaurant_detail")) {
                    "chapter_06_audit_analysis"
                } else {
                    "chapter_06_map_analysis"
                }
            }

            if (chapterId == "chapter_06_1" && actionPoints == 0) {
                targetChapterId = "chapter_06_end"
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

            // Logic for Chapter 3 evidence collection
            if (targetChapterId == "chapter_03_profile_reveal") {
                addChapter3Evidence("sterling_profile")
            } else if (targetChapterId == "chapter_03_final_files") {
                addChapter3Evidence("atlas_chat")
            }

            // Logic for Chapter 4 evidence collection
            if (targetChapterId == "chapter_04_restaurant_detail") {
                addChapter4Evidence("atlas_audit")
            } else if (targetChapterId == "chapter_04_warehouse_discovery") {
                addChapter4Evidence("syndicate_logistics")
            }

            // Logic for Chapter 5 evidence collection
            if (targetChapterId == "chapter_05_drugs_path") {
                addChapter5Evidence()
            }

            // Logic for Chapter 6 evidence collection
            if (targetChapterId == "chapter_06_dealer_success") {
                addChapter6Evidence("the_cleaners")
            } else if (targetChapterId == "chapter_06_audit_analysis" || targetChapterId == "chapter_06_map_analysis") {
                addChapter6Evidence("financial_schemes")
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

    private suspend fun addChapter3Evidence(type: String) {
        when (type) {
            "sterling_profile" -> {
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "julian_sterling",
                    name = "Julian Sterling Dossier",
                    shortDescription = "Senior Analyst at Atlas Foundation. Vane's informant.",
                    fullDescription = "Leaked documents about Syndicate money laundering. Former mentor to Thomas Vane. Currently missing.",
                    isCollected = true,
                    isPinned = true
                ))
            }
            "atlas_chat" -> {
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "atlas_chat",
                    name = "Vane-Spider Chat Log",
                    shortDescription = "Vane linked Atlas Foundation to Syndicate money laundering.",
                    fullDescription = "Recovered from the encrypted drive. Vane was trying to get testimony from 'Sterling'.",
                    isCollected = true
                ))
            }
        }
    }

    private suspend fun addChapter4Evidence(type: String) {
        when (type) {
            "atlas_audit" -> {
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "atlas_audit",
                    name = "Atlas Internal Audit",
                    shortDescription = "Audit printout with several names highlighted in red.",
                    fullDescription = "Found at The Blackwood restaurant coat check. It was left by Julian Sterling for whoever came with his alias. The names seem to be high-level Atlas executives.",
                    isCollected = true
                ))
            }
            "syndicate_logistics" -> {
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "syndicate_logistics",
                    name = "Syndicate Logistics Map",
                    shortDescription = "Notebook containing cash drop-off points.",
                    fullDescription = "Recovered from a hidden compartment in Sterling's hideout. It details how the Syndicate moves physical cash across the city.",
                    isCollected = true,
                    isPinned = true
                ))
            }
        }
    }

    private suspend fun addChapter5Evidence() {
        evidenceRepository.updateEvidence(EvidenceItem(
            id = "vane_vial",
            name = "Mysterious Vial",
            shortDescription = "An empty vial with Vane's initials found in a back alley.",
            fullDescription = "Provided by a street dealer. Contains traces of a synthetic substance. The dealer claims Vane was a user, but the evidence feels too perfectly placed.",
            isCollected = true
        ))
    }

    private suspend fun addChapter6Evidence(type: String) {
        when (type) {
            "the_cleaners" -> {
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "the_cleaners",
                    name = "The Cleaners Identification",
                    shortDescription = "Syndicate muscle group operating out of Camden.",
                    fullDescription = "Information from the Camden dealer. 'The Cleaners' handle the Syndicate's dirty work and are funded by Atlas shell companies.",
                    isCollected = true
                ))
            }
            "financial_schemes" -> {
                evidenceRepository.updateEvidence(EvidenceItem(
                    id = "atlas_schemes",
                    name = "Complex Financial Schemes",
                    shortDescription = "Circular debt and money laundering pattern found in Atlas audits.",
                    fullDescription = "A sophisticated system used by the Atlas Foundation to launder Syndicate money through non-existent shell companies.",
                    isCollected = true,
                    isPinned = true
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

            // Check if we are making a move in Chapter 2 or 5 Hub
            if ((currentChapter?.id == "chapter_02_1" || currentChapter?.id == "chapter_05_camden_hub") && actionPoints > 0) {
                actionPoints--
                hubChoices.add(nextId)
            }
            loadChapter(nextId)
        }
    }
}
