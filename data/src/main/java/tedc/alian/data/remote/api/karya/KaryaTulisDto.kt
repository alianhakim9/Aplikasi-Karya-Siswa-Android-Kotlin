package tedc.alian.data.remote.api.karya


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import tedc.alian.data.remote.api.siswa.SiswaDto

@Parcelize
data class KaryaTulisDto(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("excerpt")
    val excerpt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("id_siswa")
    val idSiswa: String,
    @SerializedName("judul_karya")
    val judulKarya: String,
    @SerializedName("jumlah_like")
    val jumlahLike: String,
    @SerializedName("kategori_karya_tulis_id")
    val kategoriKaryaTulisId: String,
    @SerializedName("konten_karya")
    val kontenKarya: String,
    @SerializedName("slug")
    val slug: String,
//    @SerializedName("sumber")
//    val sumber: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val siswa: SiswaDto,
    val komentar: List<KomentarDto>,
    @SerializedName("nama_kategori")
    val namaKategori: String? = null
) : Parcelable {
    override fun hashCode(): Int {
        return id.hashCode()
    }
}