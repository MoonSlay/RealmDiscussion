package ph.edu.auf.realmdiscussion.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import ph.edu.auf.realmdiscussion.screens.petTypes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPet(
    petModel: PetModel,
    owners: List<OwnerModel>,
    onRemove: (PetModel) -> Unit,
    onEdit: (PetModel) -> Unit
) {
    val currentItem by rememberUpdatedState(petModel)

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

    val owner = owners.find { owner -> owner.pets.any { it.id == petModel.id } }

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
                Row(modifier = Modifier.padding(16.dp)) {
                    val petType = petTypes.find { it.name == petModel.petType }
                    petType?.let {
                        Image(
                            painter = painterResource(id = it.imageRes),
                            contentDescription = it.name,
                            modifier = Modifier
                                .height(100.dp)
                                .padding(start = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = petModel.name,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${petModel.age} years old",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = petModel.petType,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        owner?.let {
                            Text(
                                text = "Owner: ${it.name}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    )
}