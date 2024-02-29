package tedc.alian.karyasiswa.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.guru.EditProfilGuruRequest
import tedc.alian.data.remote.api.guru.GuruDto
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.data.repository.GuruRepository
import tedc.alian.utils.Constants
import tedc.alian.utils.Resource
import javax.inject.Inject

@HiltViewModel
class GuruViewModel @Inject constructor(
    private val repository: GuruRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val loading = Resource.loading("loading")

    private val _profil =
        MutableStateFlow<Resource<GuruDto?>>(loading)
    val profil: SharedFlow<Resource<GuruDto?>> get() = _profil

    private val _editProfil = MutableSharedFlow<Resource<String?>>()
    val editProfil: SharedFlow<Resource<String?>> get() = _editProfil

    private var _tambahKategoriKarya = MutableSharedFlow<Resource<String?>>()
    val tambahKategorikarya: SharedFlow<Resource<String?>> get() = _tambahKategoriKarya

    private var _hapusKategoriKarya = MutableSharedFlow<Resource<String?>>()
    val hapusKategoriKarya: SharedFlow<Resource<String?>> get() = _hapusKategoriKarya

    private var _listKategoriKaryaCitra =
        MutableStateFlow<Resource<List<KategoriKaryaDto>?>>(loading)
    val listKategoriKaryaCitra: SharedFlow<Resource<List<KategoriKaryaDto>?>> get() = _listKategoriKaryaCitra

    private var _listKategoriKaryaTulis =
        MutableStateFlow<Resource<List<KategoriKaryaDto>?>>(loading)
    val listKategoriKaryaTulis: SharedFlow<Resource<List<KategoriKaryaDto>?>> get() = _listKategoriKaryaTulis

    init {
        getProfil()
    }

    fun getProfil() {
        viewModelScope.launch {
            _profil.emit(repository.getProfil())
        }
    }

    fun editProfil(request: EditProfilGuruRequest) {
        viewModelScope.launch {
            _editProfil.emit(loading)
            _editProfil.emit(repository.editProfilGuru(request))
        }
    }

    fun editProfilTanpaFoto(request: EditProfilGuruRequest) {
        viewModelScope.launch {
            _editProfil.emit(loading)
            _editProfil.emit(repository.editProfilGuruTanpaFoto(request))
        }
    }

    fun tambahKategoriKaryaCitra(namaKategori: String) {
        viewModelScope.launch {
            _tambahKategoriKarya.emit(loading)
            _tambahKategoriKarya.emit(repository.tambahKategoriKaryaCitra(namaKategori))
        }
    }

    fun tambahKategoriKaryaTulis(namaKategori: String) {
        viewModelScope.launch {
            _tambahKategoriKarya.emit(loading)
            _tambahKategoriKarya.emit(repository.tambahKategoriKaryaTulis(namaKategori))
        }
    }

    fun hapusKategoriKaryaCitra(id: String) {
        viewModelScope.launch {
            _hapusKategoriKarya.emit(loading)
            _hapusKategoriKarya.emit(repository.hapusKategoriKaryaCitra(id))
        }
    }

    fun hapusKategoriKaryaTulis(id: String) {
        viewModelScope.launch {
            _hapusKategoriKarya.emit(loading)
            _hapusKategoriKarya.emit(repository.hapusKategoriKaryaTulis(id))
        }
    }

    fun listKategoriKaryaCitra() {
        viewModelScope.launch {
            _listKategoriKaryaCitra.emit(repository.listKategoriKaryaCitra())
        }
    }

    fun listKategoriKaryaTulis() {
        viewModelScope.launch {
            _listKategoriKaryaTulis.emit(repository.listKategoriKaryaTulis())
        }
    }

    fun setKategori(kategori: String) {
        state[Constants.KATEGORI_KARYA_SAVED_KEY] = kategori
    }

    fun getKategori(): String = state[Constants.KATEGORI_KARYA_SAVED_KEY] ?: ""
}