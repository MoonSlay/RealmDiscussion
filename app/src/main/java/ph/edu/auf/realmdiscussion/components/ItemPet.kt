package ph.edu.auf.realmdiscussion.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ph.edu.auf.realmdiscussion.R
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import ph.edu.auf.realmdiscussion.screens.petTypes
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPet(
    petModel: PetModel,
    petOwners: Map<String, OwnerModel>, // Updated parameter
    onRemove: (PetModel) -> Unit,
    onEdit: (PetModel) -> Unit,
    onAdopt: (PetModel) -> Unit
) {
    val currentItem by rememberUpdatedState(petModel)
    val owner = petOwners[petModel.id] // Get owner from mapping

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onRemove(currentItem)
                    return@rememberSwipeToDismissBoxState false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onEdit(currentItem)
                    return@rememberSwipeToDismissBoxState false
                }
                SwipeToDismissBoxValue.Settled -> {
                    return@rememberSwipeToDismissBoxState false
                }
            }
        },
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            DismissBackground(dismissState)
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pet Image
                val petType = petTypes.find { it.name == petModel.petType }
                AsyncImage(
                    model = petType?.imageRes ?: R.drawable.ic_pet,
                    contentDescription = "Pet image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Pet Information
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = petModel.name.ifEmpty { "N/A" },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (petModel.age > 0) "${petModel.age} years old" else "Age: N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Type: ${petModel.petType.ifEmpty { "N/A" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    owner?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Owner: ${it.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Adopt Icon
                if (owner == null) {
                    IconButton(
                        onClick = { onAdopt(petModel) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_owner),
                            contentDescription = "Adopt Pet"
                        )
                    }
                }
            }
        }
    }
}