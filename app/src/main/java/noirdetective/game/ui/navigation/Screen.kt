package noirdetective.game.ui.navigation

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object Story : Screen("story/{storyId}") {
        fun createRoute(storyId: String) = "story/$storyId"
    }
    object Notebook : Screen("notebook")
    object Office : Screen("office")
    object Files : Screen("files")
    object WallBoard : Screen("wall_board")
    object EvidenceDetail : Screen("evidence/{evidenceId}") {
        fun createRoute(evidenceId: String) = "evidence/$evidenceId"
    }
    object Ending : Screen("ending/{endingType}") {
        fun createRoute(endingType: String) = "ending/$endingType"
    }
}
