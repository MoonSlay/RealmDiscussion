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

    fun addOwner(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val newOwner = copyToRealm(OwnerModel().apply {
                    this.name = name
                })
                val unmanagedOwner = realm.copyFromRealm(newOwner)
                _owners.update { it + unmanagedOwner }
            }
        }
    }

    fun updateOwner(id: String, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val owner = this.query<OwnerModel>("id == $0", id).first().find()
                owner?.name = newName
                val unmanagedOwner = realm.copyFromRealm(owner!!)
                _owners.update { owners ->
                    owners.map { if (it.id == id) unmanagedOwner else it }
                }
            }
        }
    }

    fun deleteOwner(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val owner = this.query<OwnerModel>("id == $0", id).first().find()
                owner?.let {
                    delete(it)
                    _owners.update { owners -> owners.filter { it.id != id } }
                }
            }
        }
    }
}