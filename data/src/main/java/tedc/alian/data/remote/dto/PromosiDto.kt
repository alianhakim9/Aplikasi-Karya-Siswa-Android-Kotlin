package tedc.alian.data.remote.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PromosiDto(
    @SerializedName("gambar")
    val gambar: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("keterangan")
    val keterangan: String,
    @SerializedName("nama_promosi")
    val namaPromosi: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("tanggal_promosi")
    val tanggalPromosi: String,
    @SerializedName("tim_ppdb_id")
    val timPpdbId: Int,
    @SerializedName("tim_p_p_d_b")
    val timPPDB: TimDto
) : Parcelable