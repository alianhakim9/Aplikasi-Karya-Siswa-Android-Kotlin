package tedc.alian.data.remote.api.karya

import androidx.paging.PagingSource
import androidx.paging.PagingState

class KaryaTulisPagingSource(
    private val api: KaryaApi
) : PagingSource<Int, KaryaTulisDto>() {
    override fun getRefreshKey(state: PagingState<Int, KaryaTulisDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, KaryaTulisDto> {
        try {
            val page = params.key ?: 1
            val response = api.getAllKaryaTulis(
                page
            )
            val items = response.body()?.data?.map { it }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (items?.isEmpty() == true) null else page + 1
            return LoadResult.Page(
                data = items!!,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}