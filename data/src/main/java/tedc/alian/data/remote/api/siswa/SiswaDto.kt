package tedc.alian.data.remote.api.siswa


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import tedc.alian.data.remote.api.auth.UserDto
import tedc.alian.data.remote.dto.abstraksi.BaseGuruSiswaDto

@Parcelize
@Entity(tableName = "siswa")
data class SiswaDto(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    @SerializedName("nisn")
    var nisn: String,
    @SerializedName("agama")
    override var agama: String,
    @SerializedName("alamat")
    override var alamat: String,
    @SerializedName("foto_profil")
    override var fotoProfil: String,
    @SerializedName("jenis_kelamin")
    override var jenisKelamin: String,
    @SerializedName("nama_lengkap")
    override var namaLengkap: String,
    @SerializedName("ttl")
    override var ttl: String,
    @SerializedName("user_id")
    override var userId: String,
    @SerializedName("user")
    override var user: UserDto,
) : Parcelable, BaseGuruSiswaDto() {
    override fun hashCode(): Int {
        var result = id.hashCode()
        if (fotoProfil.isEmpty()) {
            result = 31 * result + fotoProfil.hashCode()
        }
        return result
    }
}