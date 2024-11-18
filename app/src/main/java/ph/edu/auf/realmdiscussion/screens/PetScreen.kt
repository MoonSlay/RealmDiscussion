package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.components.ItemPet
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.viewmodels.OwnerViewModel


@Composable
fun PetScreen(
    petViewModel: PetViewModel = viewModel(),
    ownerViewModel: OwnerViewModel = viewModel()
) {
    val pets by petViewModel.pets.collectAsState()
    val owners by ownerViewModel.owners.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var snackbarShown by remember { mutableStateOf(false) }
    var showAddPetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(petViewModel.showSnackbar) {
        petViewModel.showSnackbar.collect { message ->
            if (!snackbarShown) {
                snackbarShown = true
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                    when (result) {
                        SnackbarResult.Dismissed, SnackbarResult.ActionPerformed -> {
                            snackbarShown = false
                        }
                    }
                }
            }
        }
    }

    if (showAddPetDialog) {
        AddPetDialog(
            onDismiss = { showAddPetDialog = false },
            onAddPet = { name, type, age, ownerId ->
                petViewModel.addPet(name, type, age, ownerId)
            },
            owners = owners,
            onAddOwner = { ownerName ->
                ownerViewModel.addOwner(ownerName)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Pets") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )
                LazyColumn {
                    val filteredPets = pets.filter { it.name.contains(searchQuery, ignoreCase = true) }
                    itemsIndexed(
                        items = filteredPets,
                        key = { _, item -> item.id }
                    ) { _, petContent ->
                        ItemPet(petContent, onRemove = petViewModel::deletePet)
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showAddPetDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Pet")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetDialog(
    onDismiss: () -> Unit,
    onAddPet: (String, String, Int, String?) -> Unit,
    owners: List<OwnerModel>,
    onAddOwner: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedOwner by remember { mutableStateOf<OwnerModel?>(null) }
    var newOwnerName by remember { mutableStateOf("") }
    var showAddOwnerDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showAddOwnerDialog) {
        AlertDialog(
            onDismissRequest = { showAddOwnerDialog = false },
            title = { Text(text = "Add New Owner") },
            text = {
                TextField(
                    value = newOwnerName,
                    onValueChange = { newOwnerName = it },
                    label = { Text("Owner Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            onAddOwner(newOwnerName)
                            newOwnerName = ""
                            showAddOwnerDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showAddOwnerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add New Pet") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Pet Name") }
                )
                TextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Pet Type") }
                )
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Pet Age") }
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedOwner?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Owner") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        owners.forEach { owner ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedOwner = owner
                                    expanded = false
                                },
                                text = { Text(owner.name) }
                            )
                        }
                    }
                }
                Button(onClick = { showAddOwnerDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Owner")
                    Text("Add New Owner")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val petAge = age.toIntOrNull() ?: 0
                    onAddPet(name, type, petAge, selectedOwner?.id)
                    onDismiss()
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}