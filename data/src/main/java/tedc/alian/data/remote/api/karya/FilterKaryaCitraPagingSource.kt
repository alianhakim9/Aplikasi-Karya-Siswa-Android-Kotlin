package tedc.alian.data.remote.api.karya

import androidx.paging.PagingSource
import androidx.paging.PagingState

class FilterKaryaCitraPagingSource(
    private val api: KaryaApi,
    private val kategori: String
) : PagingSource<Int, KaryaCitraDto>() {
    override fun getRefreshKey(state: PagingState<Int, KaryaCitraDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, KaryaCitraDto> {
        try {
            val page = params.key ?: 1
            val response = api.filterKaryaCitra(
                page, kategori
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