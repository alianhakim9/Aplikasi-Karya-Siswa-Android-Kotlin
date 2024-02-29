package tedc.alian.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CountKaryaDto(
    @SerializedName("count_karya_citra")
    val countKaryaCitra: Int,
    @SerializedName("count_karya_tulis")
    val countKaryaTulis: Int
)