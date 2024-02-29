package tedc.alian.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import tedc.alian.data.local.repository.MySharedPref
import tedc.alian.data.remote.api.karya.FilterKaryaTulisPagingSource
import tedc.alian.data.remote.api.karya.KaryaApi
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.data.remote.api.karya.KaryaTulisKuPagingSource
import tedc.alian.data.remote.api.karya.KaryaTulisPagingSource
import javax.inject.Inject

class ListKaryaTulisRepository @Inject constructor(
    private val api: KaryaApi,
    private val preferences: MySharedPref
) {
    fun getKaryaTulis(): Flow<PagingData<KaryaTulisDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                KaryaTulisPagingSource(api)
            }
        ).flow
    }

    fun getKaryaTulisKu(): Flow<PagingData<KaryaTulisDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                KaryaTulisKuPagingSource(api, preferences)
            }
        ).flow
    }

    fun filterKaryaTulis(query: String): Flow<PagingData<KaryaTulisDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FilterKaryaTulisPagingSource(api, query)
            }
        ).flow
    }
}