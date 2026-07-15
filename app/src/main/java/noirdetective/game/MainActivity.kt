package noirdetective.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import noirdetective.game.ui.navigation.NavGraph
import noirdetective.game.ui.theme.NoirDetectiveGameTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoirDetectiveGameTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
