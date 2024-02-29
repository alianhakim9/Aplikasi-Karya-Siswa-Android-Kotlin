package tedc.alian.data.remote.api.guru


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import tedc.alian.data.remote.api.auth.UserDto
import tedc.alian.data.remote.dto.abstraksi.BaseGuruSiswaDto

@Parcelize
@Entity(tableName = "guru")
data class GuruDto(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    @SerializedName("gelar")
    var gelar: String,
    @SerializedName("jabatan")
    var jabatan: String,
    @SerializedName("nuptk")
    var nuptk: String,
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
) : Parcelable, BaseGuruSiswaDto()