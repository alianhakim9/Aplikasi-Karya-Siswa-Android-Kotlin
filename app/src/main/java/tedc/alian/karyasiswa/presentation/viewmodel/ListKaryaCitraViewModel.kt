package tedc.alian.karyasiswa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import tedc.alian.data.remote.api.karya.KaryaCitraDitolakDto
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.data.repository.ListKaryaCitraRepository
import javax.inject.Inject


@HiltViewModel
class ListKaryaCitraViewModel @Inject constructor(
    private val repository: ListKaryaCitraRepository,
) : ViewModel() {

    fun getItems(): Flow<PagingData<KaryaCitraDto>> {
        return repository.getKaryaCitra().cachedIn(viewModelScope)
    }

    fun getAllKaryaCitraKu(): Flow<PagingData<KaryaCitraDto>> {
        return repository.getAllKaryaCitraKu().cachedIn(viewModelScope)
    }

    fun getAllKaryaDitolak(): Flow<PagingData<KaryaCitraDitolakDto>> {
        return repository.getAllKaryaDitolak().cachedIn(viewModelScope)
    }

    fun filterKaryaCitra(kategori: String): Flow<PagingData<KaryaCitraDto>> {
        return repository.filterKaryaCitra(kategori)
    }
}