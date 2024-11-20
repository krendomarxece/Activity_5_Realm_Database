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
    val owners : StateFlow<List<OwnerModel>> get() = _owners

    init {
        loadOwners()
    }

    private fun loadOwners() {
        viewModelScope.launch(Dispatchers.IO){
            val realm = RealmHelper.getRealmInstance()
            val results = realm.query(OwnerModel::class).find()
            _owners.value = results
        }
    }
    fun deleteOwner(owner: OwnerModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val ownerToDelete = query<OwnerModel>("id == $0", owner.id).first().find()
                if (ownerToDelete != null) {
                    if (ownerToDelete.pets.isEmpty()) {
                        delete(ownerToDelete)
                        _owners.update { currentOwners ->
                            currentOwners.filter { it.id != owner.id }
                        }
                    }
                    else
                    {

                    }
                }
            }
        }
    }
}