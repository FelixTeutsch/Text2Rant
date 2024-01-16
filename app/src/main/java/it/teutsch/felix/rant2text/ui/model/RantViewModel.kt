package it.teutsch.felix.rant2text.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.teutsch.felix.rant2text.data.dao.RantDao
import it.teutsch.felix.rant2text.data.model.RantTableModel
import it.teutsch.felix.rant2text.ui.enumeration.EDialog
import it.teutsch.felix.rant2text.ui.state.RantViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RantViewModel(private val dao: RantDao) : ViewModel() {
    private val _rantViewState = MutableStateFlow(RantViewState())
    val rantViewState = _rantViewState.asStateFlow()

    fun getRants() {
        viewModelScope.launch {
            dao.getRants().collect { rants ->
                _rantViewState.update { it.copy(rants = rants) }
            }
        }
    }

    fun deleteRant(rant: RantTableModel) {
        dismissDialog()
        viewModelScope.launch {
            dao.deleteRant(rant)
        }
    }

    /**
     * Create & Edit Modals
     * if rant is null, create a new one
     * else edit the given rant
     * @param rant: RantTableModel? = null
     */
    fun openEditModal(rant: RantTableModel? = null) {
        _rantViewState.update {
            it.copy(
                targetRant = rant ?: RantTableModel(),
                dialog = if (rant == null) EDialog.CREATE_RANT else EDialog.EDIT_RANT
            )
        }
    }

    fun saveRant(rant: RantTableModel): Int {
        dismissDialog()
        var newRantId = 0L
        viewModelScope.launch {
            newRantId = dao.insertRant(rant)
        }
        return newRantId.toInt()
    }

    fun updateRant(rant: RantTableModel) {
        dismissDialog()
        viewModelScope.launch {
            dao.updateRant(rant)
        }
    }

    fun dismissDialog() {
        _rantViewState.update {
            it.copy(
                targetRant = RantTableModel(),
                dialog = EDialog.NONE
            )
        }
    }

    fun clickDeleteRant(rant: RantTableModel) {
        _rantViewState.update {
            it.copy(
                targetRant = rant,
                dialog = EDialog.DELETE_RANT
            )
        }
    }

}