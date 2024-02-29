package tedc.alian.data.remote.api.karya

import com.google.gson.annotations.SerializedName
import java.io.File

data class TambahKaryaCitraRequest(
    @SerializedName("karya")
    val karya: File?,
    @SerializedName("nama_karya")
    override val namaKarya: String?,
    @SerializedName("caption")
    override val caption: String?,
    @SerializedName("kategori_karya_citra_id")
    override val kategoriKaryaCitraId: String?
) : KaryaCitraRequest()
