package tedc.alian.data.remote.api.karya


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class KaryaCitraDitolakDto(
    @SerializedName("caption")
    val caption: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("excerpt")
    val excerpt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("id_siswa")
    val idSiswa: String,
    @SerializedName("jumlah_like")
    val jumlahLike: String,
    @SerializedName("karya")
    val karya: String,
    @SerializedName("kategori_karya_citra_id")
    val kategoriKaryaCitraId: String,
    @SerializedName("nama_karya")
    val namaKarya: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("status_karya")
    val statusKarya: StatusKarya,
    @SerializedName("format")
    val format: String,
    @SerializedName("nama_kategori")
    val namaKategori: String
) : Parcelable

@Parcelize
data class StatusKarya(
    @SerializedName("id")
    val id: String,
    @SerializedName("id_karya_citra")
    val idKaryaCitra: String,
    @SerializedName("keterangan")
    val keterangan: String,
) : Parcelable
