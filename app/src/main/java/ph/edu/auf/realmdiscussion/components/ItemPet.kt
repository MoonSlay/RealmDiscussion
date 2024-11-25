package ph.edu.auf.realmdiscussion.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onEdit(currentItem)
                    return@rememberSwipeToDismissBoxState false
                }
                SwipeToDismissBoxValue.Settled -> {
                    return@rememberSwipeToDismissBoxState false
                }
            }
            return@rememberSwipeToDismissBoxState true
        },
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState) },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                // Rest of your existing Card content remains the same
                Row(modifier = Modifier.padding(16.dp)) {
                    val petType = petTypes.find { it.name == petModel.petType }
                    val imageRes = petType?.imageRes ?: R.drawable.none
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = petModel.petType,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pet Name: ${petModel.name.ifEmpty { "N/A" }}",
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pet Age: ${if (petModel.age > 0) "${petModel.age} years old" else "N/A"}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pet Type: ${petModel.petType.ifEmpty { "N/A" }}",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        owner?.let {
                            Text(
                                text = "Pet Owner: ${it.name}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        }
                    }
                    if (owner == null) {
                        Button(
                            onClick = { onAdopt(petModel) },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text("Adopt Pet")
                        }
                    }
                }
            }
        }
    )
}