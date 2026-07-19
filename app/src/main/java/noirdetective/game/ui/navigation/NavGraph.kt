package noirdetective.game.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import noirdetective.game.ui.screens.files.FilesScreen
import noirdetective.game.ui.screens.notebook.NotebookScreen
import noirdetective.game.ui.screens.notebook.NotebookViewModel
import noirdetective.game.ui.screens.office.OfficeScreen
import noirdetective.game.ui.screens.office.OfficeViewModel
import noirdetective.game.ui.screens.story.StoryScreen
import noirdetective.game.ui.screens.story.StoryViewModel
import noirdetective.game.ui.screens.wallboard.WallBoardScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Office.route
    ) {
        composable(Screen.Office.route) {
            val officeViewModel: OfficeViewModel = hiltViewModel()
            OfficeScreen(
                viewModel = officeViewModel,
                onJournalClick = { navController.navigate(Screen.Notebook.route) },
                onOfficeClick = { navController.navigate(Screen.Files.route) },
                onCaseClick = { navController.navigate(Screen.Story.createRoute("chapter_01_1")) },
                onWallBoardClick = { navController.navigate(Screen.WallBoard.route) },
                onChapter2Click = { navController.navigate(Screen.Story.createRoute("chapter_02_1")) },
                onChapter4Click = { navController.navigate(Screen.Story.createRoute("chapter_04_1")) },
                onChapter5Click = { navController.navigate(Screen.Story.createRoute("chapter_05_1")) },
                onChapter6Click = { navController.navigate(Screen.Story.createRoute("chapter_06_1")) },
                onChapter7Click = { navController.navigate(Screen.Story.createRoute("chapter_07_1")) },
                onChapter8Click = { navController.navigate(Screen.Story.createRoute("chapter_08_1")) },
                onChapter9Click = { navController.navigate(Screen.Story.createRoute("chapter_09_1")) },
                onDebugClick = { navController.navigate(Screen.Story.createRoute("debug_chapter_08")) }
            )
        }
        
        composable(
            route = Screen.Story.route,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId") ?: "chapter_01_1"
            val viewModel: StoryViewModel = hiltViewModel()
            StoryScreen(
                storyId = storyId,
                viewModel = viewModel,
                onJournalClick = { navController.navigate(Screen.Notebook.route) },
                onOfficeClick = { navController.navigate(Screen.Office.route) }
            )
        }
        
        composable(Screen.Notebook.route) {
            val viewModel: NotebookViewModel = hiltViewModel()
            NotebookScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Files.route) {
            val viewModel: NotebookViewModel = hiltViewModel()
            FilesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.WallBoard.route) {
            val viewModel: NotebookViewModel = hiltViewModel()
            WallBoardScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.EvidenceDetail.route) {
        }
        
        composable(Screen.Ending.route) {
        }
        
        composable(Screen.MainMenu.route) {
        }
    }
}
