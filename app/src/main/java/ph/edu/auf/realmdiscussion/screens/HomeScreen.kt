package ph.edu.auf.realmdiscussion.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ph.edu.auf.realmdiscussion.R
import ph.edu.auf.realmdiscussion.navigation.AppNavRoutes
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

@Composable
fun HomeScreen(navController: NavController) {
    var visible by remember { mutableStateOf(false) }

    // Launch animation effect
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F4F8),
                        Color(0xFFF0E6FA)
                    )
                )
            )
    ) {
        // Background Image with reduced opacity
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.homebg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f)
        )

        // Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            ) {
                // Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Welcome to Pet Realm",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Cards Section
            Column(
                modifier = Modifier.padding(vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pet List Card
                NavigationCard(
                    title = "Pet List",
                    description = "Manage your pets",
                    icon = painterResource(id = R.drawable.ic_pet),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = { navController.navigate(AppNavRoutes.PetList.route) }
                )

                // Owner List Card
                NavigationCard(
                    title = "Owner List",
                    description = "Manage pet owners",
                    icon = painterResource(id = R.drawable.ic_person),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { navController.navigate(AppNavRoutes.OwnerList.route) }
                )
            }

            // Footer
            Text(
                text = "Pet Realm Sampler © 2024",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationCard(
    title: String,
    description: String,
    icon: Painter,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon Container
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Text Content
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}