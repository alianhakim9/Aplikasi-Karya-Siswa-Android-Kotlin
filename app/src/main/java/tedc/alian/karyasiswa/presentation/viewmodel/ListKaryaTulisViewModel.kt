package tedc.alian.karyasiswa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.data.repository.ListKaryaTulisRepository
import javax.inject.Inject


@HiltViewModel
class ListKaryaTulisViewModel @Inject constructor(
    private val repository: ListKaryaTulisRepository
) : ViewModel() {

    fun getItems(): Flow<PagingData<KaryaTulisDto>> {
        return repository.getKaryaTulis().cachedIn(viewModelScope)
    }

    fun getAllKaryaTulisKu(): Flow<PagingData<KaryaTulisDto>> {
        return repository.getKaryaTulisKu().cachedIn(viewModelScope)
    }

    fun filterKaryaTulis(query: String): Flow<PagingData<KaryaTulisDto>> {
        return repository.filterKaryaTulis(query).cachedIn(viewModelScope)
    }
}