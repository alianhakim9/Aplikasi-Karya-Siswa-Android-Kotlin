package tedc.alian.karyasiswa.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tedc.alian.data.local.model.KaryaTulis
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.data.remote.api.karya.NotifikasiResponse
import tedc.alian.data.remote.api.karya.TambahKaryaCitraRequest
import tedc.alian.data.remote.api.karya.UbahKaryaCitraRequestRequest
import tedc.alian.data.remote.api.karya.UpdateKaryaTulisRequest
import tedc.alian.data.remote.api.karya.UploadKaryaTulisRequest
import tedc.alian.data.remote.dto.CountKaryaDto
import tedc.alian.data.repository.KaryaRepository
import tedc.alian.utils.Constants
import tedc.alian.utils.Resource
import javax.inject.Inject

@HiltViewModel
class KaryaViewModel @Inject constructor(
    private val repository: KaryaRepository,
    private val state: SavedStateHandle,

    ) : ViewModel() {

    private val loading = Resource.loading("loading")

    private var _karya =
        MutableStateFlow<Resource<List<KaryaCitraDto>>>(loading)
    val karya: SharedFlow<Resource<List<KaryaCitraDto>>> = _karya

    private var _karyaTulis =
        MutableStateFlow<Resource<List<KaryaTulisDto>>>(loading)
    val karyaTulis: SharedFlow<Resource<List<KaryaTulisDto>>> = _karyaTulis

    private var _uploadKarya =
        MutableSharedFlow<Resource<KaryaCitraDto?>>()
    val uploadKarya: SharedFlow<Resource<KaryaCitraDto?>> = _uploadKarya

    private var _uploadKaryaTulis = MutableSharedFlow<Resource<KaryaTulisDto?>>()
    val uploadKaryaTulis: SharedFlow<Resource<KaryaTulisDto?>> = _uploadKaryaTulis

    private var _kategoriKaryaTulis =
        MutableStateFlow<Resource<List<KategoriKaryaDto?>>>(loading)
    val kategoriKaryaTulis: SharedFlow<Resource<List<KategoriKaryaDto?>>> =
        _kategoriKaryaTulis

    private var _kategoriKaryaCitra =
        MutableStateFlow<Resource<List<KategoriKaryaDto?>>>(loading)
    val kategoriKaryaCitra: SharedFlow<Resource<List<KategoriKaryaDto?>>> =
        _kategoriKaryaCitra

    private var _karyaTulisDtoKu =
        MutableStateFlow<Resource<List<KaryaTulisDto>>>(loading)
    val karyaTulisDtoKu: SharedFlow<Resource<List<KaryaTulisDto>>> =
        _karyaTulisDtoKu

    private val _waitingValidateKarya =
        MutableStateFlow<Resource<List<KaryaCitraDto>>>(loading)
    val waitingValidateKarya: SharedFlow<Resource<List<KaryaCitraDto>>> get() = _waitingValidateKarya

    private val _validasiKarya =
        MutableSharedFlow<Resource<String>>()
    val validasiKarya: SharedFlow<Resource<String>> get() = _validasiKarya

    private val _tolakKarya =
        MutableSharedFlow<Resource<String>>()
    val tolakKarya: SharedFlow<Resource<String>> get() = _tolakKarya

    private val _deleteKarya =
        MutableSharedFlow<Resource<String>>()
    val deleteKarya: SharedFlow<Resource<String>> get() = _deleteKarya

    private val _updateKaryaTulis =
        MutableSharedFlow<Resource<String>>()
    val updateKaryaTulis: SharedFlow<Resource<String>> get() = _updateKaryaTulis

    private val _ubahKaryaCitra =
        MutableSharedFlow<Resource<String>>()
    val ubahKaryaCitra: SharedFlow<Resource<String>> get() = _ubahKaryaCitra

    private val _tambahKomentar = MutableSharedFlow<Resource<String?>>()
    val tambahKomentar: SharedFlow<Resource<String?>> get() = _tambahKomentar

    private val _like = MutableSharedFlow<Resource<String>>()
    val like: SharedFlow<Resource<String>> get() = _like

    private val _detailKaryaCitra =
        MutableStateFlow<Resource<KaryaCitraDto?>>(loading)
    val detailKaryaCitra: SharedFlow<Resource<KaryaCitraDto?>> get() = _detailKaryaCitra

    private val _countKarya = MutableStateFlow<Resource<CountKaryaDto>>(loading)
    val countKarya: SharedFlow<Resource<CountKaryaDto>> get() = _countKarya

    private val _detailKaryaTulis =
        MutableStateFlow<Resource<KaryaTulisDto?>>(loading)
    val detailKaryaTulis: SharedFlow<Resource<KaryaTulisDto?>> get() = _detailKaryaTulis

    private val _notifikasi = MutableStateFlow<Resource<NotifikasiResponse?>>(loading)
    val notifikasi: SharedFlow<Resource<NotifikasiResponse?>> get() = _notifikasi

    private val _updateNotifikasi = MutableSharedFlow<Resource<String?>>()
    val updateNotifikasi: SharedFlow<Resource<String?>> get() = _updateNotifikasi

    private val _hapusNotifikasi = MutableSharedFlow<Resource<String?>>()
    val hapusNotifikasi: SharedFlow<Resource<String?>> get() = _hapusNotifikasi


    private val _draftKaryaTulis = MutableSharedFlow<KaryaTulis>()
    val draftKaryaTulis: SharedFlow<KaryaTulis> get() = _draftKaryaTulis

    private val _drafKaryaTulisUpdate = MutableSharedFlow<KaryaTulis?>()
    val draftKaryaTulisUpdate: SharedFlow<KaryaTulis?> get() = _drafKaryaTulisUpdate

    val karyaCitraId = MutableLiveData("")
    val karyaTulisId = MutableLiveData("")
    val alasanDitolakKarya = MutableLiveData("")


    fun getAllKaryaCitra() {
        viewModelScope.launch {
            _karya.emit(repository.getAllKaryaCitra())
        }
    }

    fun tambahKaryaCitra(request: TambahKaryaCitraRequest) {
        viewModelScope.launch {
            _uploadKarya.emit(loading)
            _uploadKarya.emit(repository.tambahKaryaCitra(request))
        }
    }

    fun tambahKaryaTulis(request: UploadKaryaTulisRequest) {
        viewModelScope.launch {
            _uploadKaryaTulis.emit(loading)
            _uploadKaryaTulis.emit(
                repository.tambahKaryaTulis(request)
            )
        }
    }

    fun getKategoriKaryaTulis() {
        viewModelScope.launch {
            _kategoriKaryaTulis.emit(repository.getKategoriKaryaTulis())
        }
    }

    fun getKategoriKaryaCitra() {
        viewModelScope.launch {
            _kategoriKaryaCitra.emit(repository.getKategoriKaryaCitra())
        }
    }

    fun getKaryaTulisKu() {
        viewModelScope.launch {
            _karyaTulisDtoKu.emit(loading)
            _karyaTulisDtoKu.emit(repository.getKaryaTulisKu())
        }
    }

    fun getKaryaCitraKu() {
        viewModelScope.launch {
            _karya.emit(loading)
            _karya.emit(repository.getKaryaCitraKu())
        }
    }

    fun getKaryaYangBelumDivalidasi() {
        viewModelScope.launch {
            _waitingValidateKarya.emit(repository.getKaryaYangBelumDiValidasi())
        }
    }

    fun terimaKarya(isWatermarked: Boolean) {
        viewModelScope.launch {
            if (karyaCitraId.value != null) {
                _validasiKarya.emit(loading)
                _validasiKarya.emit(
                    repository.terimaKaryaCitra(
                        karyaCitraId = karyaCitraId.value.toString(),
                        isWatermarked
                    )
                )
            }
        }
    }

    fun tolakKarya() {
        viewModelScope.launch {
            _tolakKarya.emit(loading)
            _tolakKarya.emit(
                repository.tolakKarya(
                    karyaCitraId = karyaCitraId.value.toString(),
                    keterangan = getAlasan()
                )
            )
        }
    }

    fun getDetailKaryaCitra(karyaCitraId: String) {
        viewModelScope.launch {
            _detailKaryaCitra.emit(repository.getDetailKaryaCitra(karyaCitraId))
        }
    }

    fun getDetailKaryaTulis(karyaTulisId: String) {
        viewModelScope.launch {
            _detailKaryaTulis.emit(repository.getDetailKaryaTulis(karyaTulisId))
        }
    }

    fun getAllKaryaTulis() {
        viewModelScope.launch {
            _karyaTulis.emit(
                repository.getAllKaryaTulis()
            )
        }
    }

    fun hapusKaryaTulis() {
        viewModelScope.launch {
            _deleteKarya.emit(loading)
            _deleteKarya.emit(repository.hapusKaryaTulis(karyaTulisId = karyaTulisId.value.toString()))
        }
    }

    fun hapusKaryaCitra() {
        viewModelScope.launch {
            _deleteKarya.emit(loading)
            _deleteKarya.emit(
                repository.hapusKaryaCitra(karyaCitraId = karyaCitraId.value.toString())
            )
        }
    }

    fun ubahKaryaTulis(request: UpdateKaryaTulisRequest) {
        viewModelScope.launch {
            _updateKaryaTulis.emit(loading)
            _updateKaryaTulis.emit(
                repository.ubahKaryaTulis(
                    idKarya = karyaTulisId.value.toString(),
                    request = request
                )
            )
        }
    }

    fun ubahKaryaCitra(request: UbahKaryaCitraRequestRequest) {
        viewModelScope.launch {
            _ubahKaryaCitra.emit(loading)
            _ubahKaryaCitra.emit(
                repository.ubahKaryaCitra(
                    karyaCitraId = karyaCitraId.value.toString(),
                    request = request
                )
            )
        }
    }

    fun ubahKaryaCitraTanpaKarya(request: UbahKaryaCitraRequestRequest) {
        viewModelScope.launch {
            _ubahKaryaCitra.emit(loading)
            _ubahKaryaCitra.emit(
                repository.ubahKaryaCitraTanpaKarya(
                    karyaCitraId = karyaCitraId.value.toString(),
                    request = request
                )
            )
        }
    }

    fun tambahKomentarKaryaCitra(
        idKaryaCitra: String,
        komentar: String
    ) {
        viewModelScope.launch {
            _tambahKomentar.emit(loading)
            _tambahKomentar.emit(repository.tambahKomentarKaryaCitra(komentar, idKaryaCitra))
        }
    }

    fun tambahKomentarKaryaTulis(
        idKaryaTulis: String,
        komentar: String
    ) {
        viewModelScope.launch {
            _tambahKomentar.emit(loading)
            _tambahKomentar.emit(repository.tambahKomentarKaryaTulis(komentar, idKaryaTulis))
        }
    }

    fun tambahLikeKaryaCitra(karyaCitraId: String) {
        viewModelScope.launch {
            _like.emit(loading)
            _like.emit(repository.likeKaryaCitra(karyaCitraId))
        }
    }

    fun tambahLikeKaryaTulis(karyaTulisId: String) {
        viewModelScope.launch {
            _like.emit(loading)
            _like.emit(repository.likeKaryaTulis(karyaTulisId))
        }
    }

    fun getCountKarya() {
        viewModelScope.launch {
            _countKarya.emit(repository.countKarya())
        }
    }

    fun getNotifikasi() {
        viewModelScope.launch {
            _notifikasi.emit(repository.getNotifikasi())
        }
    }

    fun getNotifikasiSiswa() {
        viewModelScope.launch {
            _notifikasi.emit(repository.getNotifikasiSiswa())
        }
    }

    fun updateNotifikasi(idKarya: String, idNotifikasi: String) {
        viewModelScope.launch {
            _updateNotifikasi.emit(repository.updateNotifikasi(idKarya, idNotifikasi))
        }
    }

    fun hapusNotifikasiGuru(idNotifikasi: String) {
        viewModelScope.launch {
            _hapusNotifikasi.emit(loading)
            _hapusNotifikasi.emit(repository.hapusNotifikasiGuru(idNotifikasi))
        }
    }

    fun hapusNotifikasiSiswa() {
        viewModelScope.launch {
            _hapusNotifikasi.emit(loading)
            _hapusNotifikasi.emit(repository.hapusNotifikasiSiswa())
        }
    }

    fun setAlasan(alasan: String) {
        state[Constants.ALASAN_SAVED_KEY] = alasan
    }

    fun getAlasan(): String = state[Constants.ALASAN_SAVED_KEY] ?: ""

    fun saveKaryaTulisDraft(karyaTulis: KaryaTulis) {
        viewModelScope.launch {
            repository.saveKaryaTulisDraft(karyaTulis)
        }
    }

    fun getKaryaTulisDraft() {
        viewModelScope.launch {
            repository.getDraftKaryaTulis()?.let { _draftKaryaTulis.emit(it) }
        }
    }

    fun deleteKaryaTulisDraft() {
        viewModelScope.launch {
            repository.deleteDraftKaryaTulis()
        }
    }
}