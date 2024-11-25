package ph.edu.auf.realmdiscussion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.database.RealmHelper
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel

class OwnerViewModel : ViewModel() {
    private val _owners = MutableStateFlow<List<OwnerModel>>(emptyList())
    val owners: StateFlow<List<OwnerModel>> get() = _owners

    // State to track if an error occurred (e.g., owner already exists)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _showSnackbar = MutableStateFlow<String?>(null)
    val showSnackbar: StateFlow<String?> get() = _showSnackbar

    init {
        loadOwners()
    }

    private fun loadOwners() {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            val results = realm.query(OwnerModel::class).find()
            _owners.value = results
        }
    }

    fun addOwner(owner: OwnerModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()

            // Check if the owner already exists
            val existingOwner = realm.query<OwnerModel>("name == $0", owner.name).first().find()
            if (existingOwner != null) {
                // Set an error message if the owner already exists
                _errorMessage.emit("Owner with name '${owner.name}' already exists!")
                return@launch
            }

            realm.write {
                // Proceed to add the new owner if no duplicate was found
                val newOwner = copyToRealm(owner)
                val unmanagedOwner = realm.copyFromRealm(newOwner)
                _owners.update { it + unmanagedOwner }
            }

            // Clear any previous error message
            _errorMessage.update { null }
            _showSnackbar.emit("Added owner: ${owner.name}")
        }
    }

    fun updateOwner(owner: OwnerModel, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val existingOwner = this.query<OwnerModel>("id == $0", owner.id).first().find()
                existingOwner?.name = newName
                val unmanagedOwner = realm.copyFromRealm(existingOwner!!)
                _owners.update { owners ->
                    owners.map { if (it.id == owner.id) unmanagedOwner else it }
                }
            }
            _showSnackbar.emit("Updated owner: ${owner.name}")
        }
    }

    fun deleteOwner(owner: OwnerModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val existingOwner = this.query<OwnerModel>("id == $0", owner.id).first().find()
                existingOwner?.let {
                    delete(it)
                    _owners.update { owners -> owners.filter { it.id != owner.id } }
                }
            }
            _showSnackbar.emit("Deleted owner: ${owner.name}")
        }
    }
}
