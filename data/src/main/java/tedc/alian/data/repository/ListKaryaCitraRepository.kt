package tedc.alian.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import tedc.alian.data.local.repository.MySharedPref
import tedc.alian.data.remote.api.karya.FilterKaryaCitraPagingSource
import tedc.alian.data.remote.api.karya.KaryaApi
import tedc.alian.data.remote.api.karya.KaryaCitraDitolakDto
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.data.remote.api.karya.KaryaCitraKuDitolakPagingSource
import tedc.alian.data.remote.api.karya.KaryaCitraKuPagingSource
import tedc.alian.data.remote.api.karya.KaryaCitraPagingSource
import javax.inject.Inject

class ListKaryaCitraRepository @Inject constructor(
    private val api: KaryaApi,
    private val preferences: MySharedPref
) {
    fun getKaryaCitra(): Flow<PagingData<KaryaCitraDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                KaryaCitraPagingSource(api)
            }
        ).flow
    }

    fun getAllKaryaCitraKu(): Flow<PagingData<KaryaCitraDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                KaryaCitraKuPagingSource(api, preferences)
            }
        ).flow
    }

    fun getAllKaryaDitolak(): Flow<PagingData<KaryaCitraDitolakDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                KaryaCitraKuDitolakPagingSource(api)
            }
        ).flow
    }

    fun filterKaryaCitra(query: String): Flow<PagingData<KaryaCitraDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FilterKaryaCitraPagingSource(api, query)
            }
        ).flow
    }
}