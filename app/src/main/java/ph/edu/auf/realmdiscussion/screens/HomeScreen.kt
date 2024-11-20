package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ph.edu.auf.realmdiscussion.navigation.AppNavRoutes
import ph.edu.auf.realmdiscussion.ui.theme.barrioFontFamily

@Composable
fun HomeScreen(navController: NavController){
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize())
    {
        Text(
            text = "Pet Realm \n Sampler",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(150.dp))
        Button(
            onClick = { navController.navigate(AppNavRoutes.PetList.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
        ) {
            Text("Pet list", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate(AppNavRoutes.OwnerList.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
        ) {
            Text("Owner list", style = MaterialTheme.typography.bodyMedium)
        }
    }
}