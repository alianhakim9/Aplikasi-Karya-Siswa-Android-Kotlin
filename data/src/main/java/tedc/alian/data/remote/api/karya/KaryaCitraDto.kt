package tedc.alian.data.remote.api.karya


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import tedc.alian.data.remote.api.siswa.SiswaDto
import tedc.alian.data.remote.dto.abstraksi.BaseKaryaCitraDto

@Parcelize
data class KaryaCitraDto(
    @SerializedName("id")
    override val id: String,
    @SerializedName("caption")
    override val caption: String,
    @SerializedName("excerpt")
    override val excerpt: String,
    @SerializedName("id_siswa")
    override val idSiswa: String,
    @SerializedName("jumlah_like")
    override val jumlahLike: String,
    @SerializedName("karya")
    override val karya: String,
    @SerializedName("kategori_karya_citra_id")
    override val kategoriKaryaCitraId: String,
    @SerializedName("nama_karya")
    override val namaKarya: String,
    @SerializedName("slug")
    override val slug: String,
    @SerializedName("status")
    override val status: String,
    @SerializedName("format")
    override val format: String,
    @SerializedName("komentar")
    override val komentar: List<KomentarDto>?,
    @SerializedName("siswa")
    override val siswa: SiswaDto,
    @SerializedName("created_at")
    override val createdAt: String,
    @SerializedName("updated_at")
    override val updatedAt: String,
    @SerializedName("nama_kategori")
    override val namaKategori: String,
) : Parcelable, BaseKaryaCitraDto() {
    override fun hashCode(): Int {
        var result = id.hashCode()
        if (karya.isEmpty()) {
            result = 31 * result + karya.hashCode()
        }
        return result
    }
}

