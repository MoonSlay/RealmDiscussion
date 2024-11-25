package ph.edu.auf.realmdiscussion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import ph.edu.auf.realmdiscussion.navigation.AppNavigation
import ph.edu.auf.realmdiscussion.ui.theme.RealmDiscussionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealmDiscussionTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}