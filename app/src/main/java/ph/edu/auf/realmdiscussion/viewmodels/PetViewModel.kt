package ph.edu.auf.realmdiscussion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.database.RealmHelper
import ph.edu.auf.realmdiscussion.database.realmodel.OwnerModel
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel

class PetViewModel : ViewModel() {
    private val _pets = MutableStateFlow<List<PetModel>>(emptyList())
    val pets: StateFlow<List<PetModel>> get() = _pets.asStateFlow()

    private val _showSnackbar = MutableSharedFlow<String>()
    val showSnackbar: SharedFlow<String> = _showSnackbar

    // New state to track pet-owner relationships
    private val _petOwners = MutableStateFlow<Map<String, OwnerModel>>(emptyMap())
    val petOwners: StateFlow<Map<String, OwnerModel>> get() = _petOwners.asStateFlow()

    init {
        loadPets()
        loadPetOwners()
    }

    private fun loadPets() {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            val results = realm.query(PetModel::class).find()
            val unmanagedPets = realm.copyFromRealm(results)
            _pets.value = unmanagedPets
        }
    }

    private fun loadPetOwners() {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            val owners = realm.query(OwnerModel::class).find()
            val petToOwnerMap = mutableMapOf<String, OwnerModel>()

            owners.forEach { owner ->
                owner.pets.forEach { pet ->
                    petToOwnerMap[pet.id] = realm.copyFromRealm(owner)
                }
            }
            _petOwners.value = petToOwnerMap
        }
    }

    fun deletePet(model: PetModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val pet = this.query<PetModel>("id == $0", model.id).first().find()
                if (pet != null) {
                    // Check if the pet has an owner
                    val owner = this.query<OwnerModel>("pets.id == $0", model.id).first().find()
                    if (owner != null) {
                        viewModelScope.launch {
                            _showSnackbar.emit("Unable to delete! ${model.name} has Owner")
                        }
                        return@write
                    }

                    delete(pet)

                    _pets.update {
                        val list = it.toMutableList()
                        list.remove(model)
                        list
                    }

                    // Update pet-owner mapping
                    _petOwners.update { currentMap ->
                        currentMap.toMutableMap().apply {
                            remove(model.id)
                        }
                    }
                }
            }
            viewModelScope.launch {
                _showSnackbar.emit("Removed ${model.name}")
            }
        }
    }

    fun addPet(pet: PetModel, owner: OwnerModel?) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                if (pet.id.isEmpty()) {
                    pet.id = java.util.UUID.randomUUID().toString()
                }
                val newPet = copyToRealm(pet)
                val unmanagedPet = realm.copyFromRealm(newPet)

                owner?.let {
                    val managedOwner = this.query<OwnerModel>("id == $0", it.id).first().find()
                    managedOwner?.pets?.add(newPet)

                    // Update pet-owner mapping
                    _petOwners.update { currentMap ->
                        currentMap.toMutableMap().apply {
                            put(pet.id, realm.copyFromRealm(managedOwner!!))
                        }
                    }
                }

                _pets.update { it + unmanagedPet }
            }
            _showSnackbar.emit("Added ${pet.name}")
        }
    }

    fun updatePet(pet: PetModel, owner: OwnerModel?) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val existingPet = this.query<PetModel>("id == $0", pet.id).first().find()
                existingPet?.apply {
                    this.name = pet.name
                    this.petType = pet.petType
                    this.age = pet.age
                }

                // Remove pet from previous owner's list
                val previousOwner = this.query<OwnerModel>("pets.id == $0", pet.id).first().find()
                previousOwner?.pets?.remove(existingPet)

                // Add pet to new owner's list
                owner?.let {
                    val managedOwner = this.query<OwnerModel>("id == $0", it.id).first().find()
                    existingPet?.let { it1 -> managedOwner?.pets?.add(it1) }

                    // Update pet-owner mapping
                    _petOwners.update { currentMap ->
                        currentMap.toMutableMap().apply {
                            put(pet.id, realm.copyFromRealm(managedOwner!!))
                        }
                    }
                } ?: run {
                    // If no new owner, remove from pet-owner mapping
                    _petOwners.update { currentMap ->
                        currentMap.toMutableMap().apply {
                            remove(pet.id)
                        }
                    }
                }

                val unmanagedPet = realm.copyFromRealm(existingPet!!)
                _pets.update { pets ->
                    pets.map { if (it.id == pet.id) unmanagedPet else it }
                }
            }
            _showSnackbar.emit("Updated ${pet.name}")
        }
    }

    fun adoptPet(pet: PetModel, owner: OwnerModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val managedPet = this.query<PetModel>("id == $0", pet.id).first().find()
                val managedOwner = this.query<OwnerModel>("id == $0", owner.id).first().find()

                // Remove from previous owner if exists
                val previousOwner = this.query<OwnerModel>("pets.id == $0", pet.id).first().find()
                previousOwner?.pets?.remove(managedPet)

                managedOwner?.pets?.add(managedPet!!)

                // Update pet-owner mapping
                _petOwners.update { currentMap ->
                    currentMap.toMutableMap().apply {
                        put(pet.id, realm.copyFromRealm(managedOwner!!))
                    }
                }
            }
            _showSnackbar.emit("Adopted ${pet.name} by ${owner.name}")
        }
    }
}