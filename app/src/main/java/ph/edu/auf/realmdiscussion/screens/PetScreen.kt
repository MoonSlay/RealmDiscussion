package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import ph.edu.auf.realmdiscussion.R
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import ph.edu.auf.realmdiscussion.viewmodels.OwnerViewModel

// Define a data class for PetType
data class PetType(val name: String, val imageRes: Int)

// Define a list of pet types with corresponding images
val petTypes = listOf(
    PetType("Dog", R.drawable.dog),
    PetType("Cat", R.drawable.cat),
    PetType("Bird", R.drawable.bird),
    PetType("Fish", R.drawable.fish),
    PetType("Snake", R.drawable.snake),
    PetType("Hamster", R.drawable.hamster),
)

@Composable
fun PetScreen(
    petViewModel: PetViewModel = viewModel(),
    ownerViewModel: OwnerViewModel = viewModel(),
    onBack: () -> Unit
) {
    val pets by petViewModel.pets.collectAsState()
    val owners by ownerViewModel.owners.collectAsState()
    val petOwners by petViewModel.petOwners.collectAsState() // Add this line
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var snackbarShown by remember { mutableStateOf(false) }
    var showAddPetDialog by remember { mutableStateOf(false) }
    var showEditPetDialog by remember { mutableStateOf<PetModel?>(null) }
    var showAdoptPetDialog by remember { mutableStateOf<PetModel?>(null) }

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
            onAddPet = { pet, owner ->
                petViewModel.addPet(pet, owner)
                showAddPetDialog = false
            },
            owners = owners,
            petOwners = petOwners, // Pass petOwners here
            onAddOwner = { ownerName ->
                val newOwner = OwnerModel().apply { name = ownerName }
                ownerViewModel.addOwner(newOwner)
            }
        )
    }

    if (showEditPetDialog != null) {
        AddPetDialog(
            onDismiss = { showEditPetDialog = null },
            onAddPet = { pet, owner ->
                petViewModel.updatePet(pet, owner)
                showEditPetDialog = null
            },
            owners = owners,
            petOwners = petOwners, // Pass petOwners here
            onAddOwner = { ownerName ->
                val newOwner = OwnerModel().apply { name = ownerName }
                ownerViewModel.addOwner(newOwner)
            },
            initialPet = showEditPetDialog
        )
    }

    if (showAdoptPetDialog != null) {
        AdoptPetDialog(
            pet = showAdoptPetDialog!!,
            owners = owners,
            onDismiss = { showAdoptPetDialog = null },
            onAdopt = { pet, owner ->
                petViewModel.adoptPet(pet, owner)
                showAdoptPetDialog = null
            },
            onAddOwner = { ownerName ->
                val newOwner = OwnerModel().apply { name = ownerName }
                ownerViewModel.addOwner(newOwner)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(paddingValues)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = onBack,
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = " Pet List ",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    FloatingActionButton(
                        onClick = { showAddPetDialog = true },
                        modifier = Modifier.padding(end = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Pet")
                    }
                }
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Pets") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    singleLine = true
                )
                LazyColumn {
                    val filteredPets = pets.filter { it.name.contains(searchQuery, ignoreCase = true) }
                    itemsIndexed(
                        items = filteredPets,
                        key = { _, item -> item.id }
                    ) { _, petContent ->
                        ItemPet(
                            petModel = petContent,
                            petOwners = petOwners, // Pass petOwners instead of owners
                            onRemove = petViewModel::deletePet,
                            onEdit = { showEditPetDialog = it },
                            onAdopt = { pet ->
                                showAdoptPetDialog = pet
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetDialog(
    onDismiss: () -> Unit,
    onAddPet: (PetModel, OwnerModel?) -> Unit,
    owners: List<OwnerModel>,
    petOwners: Map<String, OwnerModel>, // Add this parameter
    onAddOwner: (String) -> Unit,
    initialPet: PetModel? = null,
) {
    var name by remember { mutableStateOf(initialPet?.name ?: "") }
    var type by remember { mutableStateOf(initialPet?.petType ?: "") }
    var age by remember { mutableStateOf(initialPet?.age?.toString() ?: "") }
    var selectedOwner by remember { mutableStateOf(petOwners[initialPet?.id]) } // Use petOwners to set initial value
    var hasOwner by remember { mutableStateOf(selectedOwner != null) } // Set hasOwner based on selectedOwner
    var ownerExpanded by remember { mutableStateOf(false) }
    var newOwnerName by remember { mutableStateOf("") }
    var showAddOwnerDialog by remember { mutableStateOf(false) }
    var petTypeExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showError by remember { mutableStateOf(false) }

    if (showAddOwnerDialog) {
        AlertDialog(
            onDismissRequest = { showAddOwnerDialog = false },
            title = { Text(text = "Add New Owner") },
            text = {
                TextField(
                    value = newOwnerName,
                    onValueChange = { newOwnerName = it },
                    label = { Text("Owner Name") },
                    singleLine = true
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
        title = { Text(text = if (initialPet == null) "Add New Pet" else "Edit Pet") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Pet Name") },
                    isError = showError && name.isEmpty(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(5.dp))
                ExposedDropdownMenuBox(
                    expanded = petTypeExpanded,
                    onExpandedChange = { petTypeExpanded = !petTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pet Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = petTypeExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = showError && type.isEmpty()
                    )
                    ExposedDropdownMenu(
                        expanded = petTypeExpanded,
                        onDismissRequest = { petTypeExpanded = false }
                    ) {
                        petTypes.forEach { petType ->
                            DropdownMenuItem(
                                onClick = {
                                    type = petType.name
                                    petTypeExpanded = false
                                },
                                text = { Text(petType.name) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Pet Age") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = showError && age.isEmpty(),
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = hasOwner,
                        onCheckedChange = { hasOwner = it }
                    )
                    Text(text = "Assign Pet Owner")
                }
                if (hasOwner) {
                    ExposedDropdownMenuBox(
                        expanded = ownerExpanded,
                        onExpandedChange = { ownerExpanded = !ownerExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedOwner?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Owner") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = ownerExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = ownerExpanded,
                            onDismissRequest = { ownerExpanded = false }
                        ) {
                            owners.forEach { owner ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedOwner = owner
                                        ownerExpanded = false
                                    },
                                    text = { Text(owner.name) }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Button(onClick = { showAddOwnerDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Owner")
                        Text("Add New Owner")
                    }
                }
                if (showError) {
                    Text(
                        text = "All fields are required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isEmpty() || type.isEmpty() || age.isEmpty()) {
                        showError = true
                    } else {
                        val petAge = age.toIntOrNull() ?: 0
                        val pet = PetModel().apply {
                            this.name = name
                            this.petType = type
                            this.age = petAge
                            this.id = initialPet?.id ?: ""
                        }
                        onAddPet(pet, if (hasOwner) selectedOwner else null)
                        onDismiss()
                    }
                }
            ) {
                Text(if (initialPet == null) "Add" else "Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptPetDialog(
    pet: PetModel,
    owners: List<OwnerModel>,
    onDismiss: () -> Unit,
    onAdopt: (PetModel, OwnerModel) -> Unit,
    onAddOwner: (String) -> Unit
) {
    var selectedOwner by remember { mutableStateOf<OwnerModel?>(null) }
    var newOwnerName by remember { mutableStateOf("") }
    var showAddOwnerDialog by remember { mutableStateOf(false) }
    var ownerExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showAddOwnerDialog) {
        AlertDialog(
            onDismissRequest = { showAddOwnerDialog = false },
            title = { Text(text = "Add New Owner") },
            text = {
                TextField(
                    value = newOwnerName,
                    onValueChange = { newOwnerName = it },
                    label = { Text("Owner Name") },
                    singleLine = true
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
        title = { Text(text = "Adopt Pet") },
        text = {
            Column {
                Text(text = "Select an owner for ${pet.name}:")
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = ownerExpanded,
                    onExpandedChange = { ownerExpanded = !ownerExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedOwner?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Owner") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = ownerExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = ownerExpanded,
                        onDismissRequest = { ownerExpanded = false }
                    ) {
                        owners.forEach { owner ->
                            DropdownMenuItem(
                                text = { Text(owner.name) },
                                onClick = {
                                    selectedOwner = owner
                                    ownerExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showAddOwnerDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Owner")
                    Text("Add New Owner")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedOwner?.let { onAdopt(pet, it) }
                    onDismiss()
                }
            ) {
                Text("Adopt")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}