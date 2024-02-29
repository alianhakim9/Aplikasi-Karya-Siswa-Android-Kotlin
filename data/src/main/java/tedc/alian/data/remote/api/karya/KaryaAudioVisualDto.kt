package tedc.alian.data.remote.api.karya

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import tedc.alian.data.remote.api.siswa.SiswaDto
import tedc.alian.data.remote.dto.abstraksi.BaseKaryaCitraDto

@Parcelize
data class KaryaAudioVisualDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("caption")
    val caption: String,
    @SerializedName("excerpt")
    val excerpt: String,
    @SerializedName("id_siswa")
    val idSiswa: String,
    @SerializedName("jumlah_like")
    val jumlahLike: String,
    @SerializedName("karya")
    val karya: String,
    @SerializedName("kategori_karya_audio_visual_id")
    val kategoriKaryaAudioVisualId: String,
    @SerializedName("nama_karya")
    val namaKarya: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("komentar")
    val komentar: List<KomentarDto>?,
    @SerializedName("siswa")
    val siswa: SiswaDto,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("nama_kategori")
    val namaKategori: String,
) : Parcelable {
    override fun hashCode(): Int {
        var result = id.hashCode()
        if (karya.isEmpty()) {
            result = 31 * result + karya.hashCode()
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KaryaAudioVisualDto

        if (id != other.id) return false
        if (caption != other.caption) return false
        if (excerpt != other.excerpt) return false
        if (idSiswa != other.idSiswa) return false
        if (jumlahLike != other.jumlahLike) return false
        if (karya != other.karya) return false
        if (kategoriKaryaAudioVisualId != other.kategoriKaryaAudioVisualId) return false
        if (namaKarya != other.namaKarya) return false
        if (slug != other.slug) return false
        if (format != other.format) return false
        if (komentar != other.komentar) return false
        if (siswa != other.siswa) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (namaKategori != other.namaKategori) return false

        return true
    }
}
