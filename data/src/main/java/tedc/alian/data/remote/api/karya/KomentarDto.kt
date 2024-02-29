package tedc.alian.data.remote.api.karya


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import tedc.alian.data.remote.api.auth.UserDto
import tedc.alian.data.remote.api.guru.GuruDto
import tedc.alian.data.remote.api.siswa.SiswaDto

@Parcelize
data class KomentarDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("karya_citra_id")
    val karyaCitraId: Int,
    @SerializedName("komentar")
    val komentar: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("siswa")
    val siswa: SiswaDto? = null,
    @SerializedName("guru")
    val guru: GuruDto? = null,
    @SerializedName("created_at")
    val createdAt: String
) : Parcelable