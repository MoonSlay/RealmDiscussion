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

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            val results = realm.query(PetModel::class).find()
            val unmanagedPets = realm.copyFromRealm(results)
            _pets.value = unmanagedPets
        }
    }

    fun deletePet(model: PetModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val pet = this.query<PetModel>("id == $0", model.id).first().find()
                if (pet != null) {
                    delete(pet)
                    _pets.update {
                        val list = it.toMutableList()
                        list.remove(model)
                        list
                    }
                }
            }
            _showSnackbar.emit("Removed ${model.name}")
        }
    }

    fun addPet(name: String, petType: String, age: Int, ownerId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val newPet = copyToRealm(PetModel().apply {
                    this.name = name
                    this.petType = petType
                    this.age = age
                })
                val unmanagedPet = realm.copyFromRealm(newPet)
                _pets.update { it + unmanagedPet }

                ownerId?.let {
                    val owner = this.query<OwnerModel>("id == $0", it).first().find()
                    owner?.pets?.add(newPet)
                }
            }
            _showSnackbar.emit("Added $name")
        }
    }

    fun updatePet(id: String, name: String, petType: String, age: Int, ownerId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val pet = this.query<PetModel>("id == $0", id).first().find()
                pet?.apply {
                    this.name = name
                    this.petType = petType
                    this.age = age
                }
                val unmanagedPet = realm.copyFromRealm(pet!!)
                _pets.update { pets ->
                    pets.map { if (it.id == id) unmanagedPet else it }
                }

                ownerId?.let {
                    val owner = this.query<OwnerModel>("id == $0", it).first().find()
                    owner?.pets?.add(pet)
                }
            }
            _showSnackbar.emit("Updated $name")
        }
    }
}