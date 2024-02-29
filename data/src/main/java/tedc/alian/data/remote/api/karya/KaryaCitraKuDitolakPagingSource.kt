package tedc.alian.data.remote.api.karya

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okio.IOException
import retrofit2.HttpException

class KaryaCitraKuDitolakPagingSource(
    private val api: KaryaApi) : PagingSource<Int, KaryaCitraDitolakDto>() {
    override fun getRefreshKey(state: PagingState<Int, KaryaCitraDitolakDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, KaryaCitraDitolakDto> {
        return try {
            val page = params.key ?: 1
            val response = api.getKaryaAllDitolak(
                page
            )
            val items = response.body()?.data?.map { it }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (items.isNullOrEmpty()) null else page + 1
            LoadResult.Page(
                data = items ?: emptyList(),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
