package tedc.alian.data.remote.api.karya

import com.google.gson.annotations.SerializedName
import tedc.alian.data.remote.api.siswa.SiswaDto

data class NotifikasiDto(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_karya_citra")
    val idKaryaCitra: Int,
    @SerializedName("id_siswa")
    val idSiswa: Int,
    @SerializedName("notifikasi")
    val notifikasi: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("karya_citra")
    val karyaCitra: KaryaCitraDto,
    @SerializedName("siswa")
    val siswa: SiswaDto
)