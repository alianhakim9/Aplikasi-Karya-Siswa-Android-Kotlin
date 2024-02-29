package tedc.alian.data.remote.api.karya

import com.google.gson.annotations.SerializedName

data class KaryaCitraBaseResponse<T>(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("data")
    val data: T,
    @SerializedName("first_page_url")
    val firstPageUrl: String,
    @SerializedName("from")
    val from: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("last_page_url")
    val lastPageUrl: String,
    @SerializedName("links")
    val links: List<Link>,
    @SerializedName("next_page_url")
    val nextPageUrl: Any,
    @SerializedName("path")
    val path: String,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("prev_page_url")
    val prevPageUrl: Any,
    @SerializedName("to")
    val to: Int,
    @SerializedName("total")
    val total: Int
)

data class Link(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("label")
    val label: String,
    @SerializedName("url")
    val url: String
)

