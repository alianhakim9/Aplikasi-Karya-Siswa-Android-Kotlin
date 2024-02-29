package tedc.alian.data.remote.api.tim

import com.google.gson.annotations.SerializedName
import java.io.File

data class TambahPromosiRequest(
    @SerializedName("nama_promosi")
    val namaPromosi: String,
    @SerializedName("gambar")
    val gambar: File? = null,
    @SerializedName("keterangan")
    val keterangan: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("tanggal_promosi")
    val tanggalPromosi: String
)
