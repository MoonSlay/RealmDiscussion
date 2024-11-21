package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ph.edu.auf.realmdiscussion.components.DismissBackground
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.viewmodels.OwnerViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerScreen(ownerViewModel: OwnerViewModel = viewModel()) {
    val owners by ownerViewModel.owners.collectAsState()
    var editingOwner by remember { mutableStateOf<OwnerModel?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize())
    {
        Text(
            text = "===-- Owner List --===",
            style = MaterialTheme.typography.headlineSmall
        )
        Scaffold { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                itemsIndexed(
                    items = owners,
                    key = { _, item -> item.id }
                ) { _, ownerContent ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            when (it) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    ownerViewModel.deleteOwner(ownerContent)
                                }
                                SwipeToDismissBoxValue.EndToStart -> {
                                    editingOwner = ownerContent
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
                                        text = ownerContent.name,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Owns ${ownerContent.pets.size} pets",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    editingOwner?.let { owner ->
        EditOwnerDialog(
            owner = owner,
            onDismiss = { editingOwner = null },
            onSave = { newName ->
                ownerViewModel.updateOwner(owner, newName)
            }
        )
    }
}

@Composable
fun EditOwnerDialog(
    owner: OwnerModel,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf(owner.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Owner Name") },
        text = {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Owner Name") }
            )
        },
        confirmButton = {
            Button(onClick = {
                onSave(newName)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}