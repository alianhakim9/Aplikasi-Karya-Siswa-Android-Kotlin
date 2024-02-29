package tedc.alian.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tedc.alian.data.local.model.KaryaTulis

@Dao
interface KaryaTulisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(karyaTulis: KaryaTulis)

    @Query("SELECT * FROM karya_tulis WHERE tipe = 'tambah' LIMIT 1")
    suspend fun getKaryaTulis(): KaryaTulis?

    @Query("DELETE FROM karya_tulis")
    suspend fun deleteDraftKaryaTulis()

    @Query("SELECT * FROM karya_tulis WHERE tipe = 'ubah' AND judul_karya=:judulKarya LIMIT 1")
    suspend fun getKaryaTulisDraftUpdate(judulKarya: String): KaryaTulis?

    @Query("DELETE FROM karya_tulis WHERE tipe = 'ubah' AND judul_karya = :judulKarya")
    suspend fun deleteDraftKaryaTulisUpdate(judulKarya: String)

}