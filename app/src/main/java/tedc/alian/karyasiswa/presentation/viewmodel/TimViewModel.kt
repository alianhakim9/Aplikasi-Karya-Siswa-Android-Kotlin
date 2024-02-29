package tedc.alian.karyasiswa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.tim.TambahPromosiRequest
import tedc.alian.data.remote.api.tim.UbahPromosiRequest
import tedc.alian.data.remote.dto.PromosiDto
import tedc.alian.data.remote.dto.TimDto
import tedc.alian.data.repository.TimRepository
import tedc.alian.utils.Resource
import javax.inject.Inject

@HiltViewModel
class TimViewModel @Inject constructor(
    private val repository: TimRepository
) : ViewModel() {

    private val loading = Resource.loading("loading")

    private val _profil =
        MutableStateFlow<Resource<TimDto?>>(loading)
    val profil: SharedFlow<Resource<TimDto?>> get() = _profil

    private val _editProfil = MutableSharedFlow<Resource<String?>>()
    val editProfil: SharedFlow<Resource<String?>> get() = _editProfil

    private val _tambahPromosi = MutableSharedFlow<Resource<String>>()
    val tambahPromosi: SharedFlow<Resource<String>> get() = _tambahPromosi

    private val _listPromosi = MutableStateFlow<Resource<List<PromosiDto>?>>(loading)
    val listPromosi: SharedFlow<Resource<List<PromosiDto>?>> get() = _listPromosi

    private val _hapusPromosi = MutableSharedFlow<Resource<String?>>()
    val hapusPromosi: SharedFlow<Resource<String?>> get() = _hapusPromosi

    private val _ubahPromosi = MutableSharedFlow<Resource<String?>>()
    val ubahPromosi: SharedFlow<Resource<String?>> get() = _ubahPromosi

    private val _activePromosi = MutableStateFlow<Resource<PromosiDto?>>(loading)
    val activePromosi: SharedFlow<Resource<PromosiDto?>> get() = _activePromosi
    fun getProfil() {
        viewModelScope.launch {
            _profil.emit(repository.getProfil())
        }
    }

    fun editProfil(
        namaLengkap: String,
        jabatan: String
    ) {
        viewModelScope.launch {
            _editProfil.emit(loading)
            _editProfil.emit(repository.editProfil(namaLengkap, jabatan))
        }
    }

    fun tambahPromosi(request: TambahPromosiRequest) {
        viewModelScope.launch {
            _tambahPromosi.emit(loading)
            _tambahPromosi.emit(repository.tambahPromosi(request))
        }
    }

    fun listPromosi() {
        viewModelScope.launch {
            _listPromosi.emit(repository.getListPromosi())
        }
    }

    fun getListPromosiByTim() {
        viewModelScope.launch {
            _listPromosi.emit(repository.getListPromosiByTim())
        }
    }

    fun hapusDataPromosi(id: String) {
        viewModelScope.launch {
            _hapusPromosi.emit(loading)
            _hapusPromosi.emit(repository.hapusDataPromosi(id))
        }
    }

    fun ubahPromosiTanpaGambar(id: String, request: UbahPromosiRequest) {
        viewModelScope.launch {
            _ubahPromosi.emit(loading)
            _ubahPromosi.emit(repository.ubahPromosiTanpaGambar(id, request))
        }
    }


    fun ubahPromosi(id: String, request: UbahPromosiRequest) {
        viewModelScope.launch {
            _ubahPromosi.emit(loading)
            _ubahPromosi.emit(repository.ubahPromosi(id, request))
        }
    }

    fun getActivePromosi() {
        viewModelScope.launch {
            _activePromosi.emit(repository.getActivePromosi())
        }
    }
}