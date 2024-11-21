package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.components.ItemOwner
import ph.edu.auf.realmdiscussion.viewmodels.OwnerViewModel
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel

@Composable
fun OwnerScreen(ownerViewModel: OwnerViewModel = viewModel(), onBack: () -> Unit) {
    val owners by ownerViewModel.owners.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var snackbarShown by remember { mutableStateOf(false) }
    var showAddOwnerDialog by remember { mutableStateOf(false) }
    var showEditOwnerDialog by remember { mutableStateOf<OwnerModel?>(null) }
    val errorMessage by ownerViewModel.errorMessage.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(ownerViewModel.showSnackbar) {
        ownerViewModel.showSnackbar.collect { message ->
            message?.let {
                if (!snackbarShown) {
                    snackbarShown = true
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = it,
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
    }

    if (showAddOwnerDialog) {
        AddOwnerDialog(
            onDismiss = { showAddOwnerDialog = false },
            onAddOwner = { ownerName ->
                val newOwner = OwnerModel().apply { name = ownerName }
                ownerViewModel.addOwner(newOwner)
                showAddOwnerDialog = false
            }
        )
    }

    if (showEditOwnerDialog != null) {
        EditOwnerDialog(
            owner = showEditOwnerDialog!!,
            onDismiss = { showEditOwnerDialog = null },
            onSave = { newName ->
                ownerViewModel.updateOwner(showEditOwnerDialog!!, newName)
                showEditOwnerDialog = null
            }
        )
    }

    val filteredOwners = owners.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(paddingValues)
            ) {
                //owner title
                Text(
                    text = "===-- Owner List --===",
                    style = MaterialTheme.typography.headlineSmall
                )
                //search field
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Owners") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
                //Message for adding same owner name
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                //function for scrolling through the owners
                LazyColumn {
                    itemsIndexed(
                        items = filteredOwners,
                        key = { _, item -> item.id }
                    ) { _, ownerContent ->
                        //owner content
                        ItemOwner(
                            ownerModel = ownerContent,
                            onRemove = ownerViewModel::deleteOwner,
                            onEdit = { showEditOwnerDialog = it }
                        )
                    }
                }
            }
        }
        //Floating action button for adding owner
        FloatingActionButton(
            onClick = { showAddOwnerDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Owner")
        }

        //Floating action button for going back
        FloatingActionButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }
}

@Composable
fun EditOwnerDialog(
    owner: OwnerModel,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    //owner new name
    var newName by remember { mutableStateOf(owner.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Owner Name") },
        text = {
            Column {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Owner Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pets owned by ${owner.name}:")
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Name", style = MaterialTheme.typography.bodyMedium)
                        owner.pets.forEach { pet ->
                            Text(text = pet.name)
                        }
                        if (owner.pets.isEmpty()) {
                            Text(text = "None")
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Type", style = MaterialTheme.typography.bodyMedium)
                        owner.pets.forEach { pet ->
                            Text(text = pet.petType)
                        }
                        if (owner.pets.isEmpty()) {
                            Text(text = "N/A")
                        }
                    }
                }
            }
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

@Composable
fun AddOwnerDialog(
    onDismiss: () -> Unit,
    onAddOwner: (String) -> Unit
) {
    var ownerName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add New Owner") },
        text = {
            TextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = { Text("Owner Name") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )
        },
        confirmButton = {
            Button(onClick = {
                onAddOwner(ownerName)
                onDismiss()
            }) {
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