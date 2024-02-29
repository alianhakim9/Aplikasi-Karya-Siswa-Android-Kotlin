package tedc.alian.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "karya_tulis")
data class KaryaTulis(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "konten_karya")
    val kontenKarya: String,
    @ColumnInfo(name = "judul_karya")
    val judulKarya: String,
    @ColumnInfo(name = "kategori_karya_tulis_id")
    val kategoriKaryaTulisId: String,
    @ColumnInfo(name = "sumber")
    val sumber: String,
    @ColumnInfo(name = "tipe")
    val tipe: String = ""
)