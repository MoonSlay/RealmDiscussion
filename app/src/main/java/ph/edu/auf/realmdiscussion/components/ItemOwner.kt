package ph.edu.auf.realmdiscussion.components

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemOwner(
    ownerModel: OwnerModel,
    onRemove: (OwnerModel) -> Unit,
    onEdit: (OwnerModel) -> Unit
) {
    val currentItem by rememberUpdatedState(ownerModel)

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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = ownerModel.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Owns ${ownerModel.pets.size} pets",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}