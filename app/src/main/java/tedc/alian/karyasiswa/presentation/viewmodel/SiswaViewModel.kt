package tedc.alian.karyasiswa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.siswa.EditProfilSiswaRequest
import tedc.alian.data.remote.api.siswa.SiswaDto
import tedc.alian.data.repository.SiswaRepository
import tedc.alian.utils.Resource
import javax.inject.Inject

@HiltViewModel
class SiswaViewModel @Inject constructor(
    private val repository: SiswaRepository,
) : ViewModel() {

    private val loading = Resource.loading("loading")


    private val _profil =
        MutableStateFlow<Resource<SiswaDto?>>(loading)
    val profil: SharedFlow<Resource<SiswaDto?>> get() = _profil

    private val _editProfil = MutableSharedFlow<Resource<String?>>()
    val editProfil: SharedFlow<Resource<String?>> get() = _editProfil

    fun getProfil() {
        viewModelScope.launch {
            _profil.emit(repository.getProfilSiswa())
        }
    }

    fun editProfil(editProfilSiswaRequest: EditProfilSiswaRequest) {
        viewModelScope.launch {
            _editProfil.emit(loading)
            _editProfil.emit(repository.editProfilSiswa(editProfilSiswaRequest))
        }
    }

    fun editProfilTanpaFoto(editProfilSiswaRequest: EditProfilSiswaRequest) {
        viewModelScope.launch {
            _editProfil.emit(loading)
            _editProfil.emit(repository.editProfilSiswaTanpaFoto(editProfilSiswaRequest))
        }
    }
}