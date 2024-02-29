package tedc.alian.data.remote.dto

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tim")
@Parcelize
data class TimDto(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    @SerializedName("nama_lengkap")
    var namaLengkap: String,
    @SerializedName("jabatan")
    val jabatan: String,
    @SerializedName("user_id")
    val userId: String
) : Parcelable
