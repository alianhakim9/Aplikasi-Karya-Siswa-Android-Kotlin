package tedc.alian.data.remote.api.karya

import com.google.gson.annotations.SerializedName

data class KategoriKaryaDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nama_kategori")
    val namaKategori: String,
)
