package tedc.alian.data.local.dao

import androidx.room.*
import tedc.alian.data.remote.api.siswa.SiswaDto

@Dao
interface SiswaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(siswa: SiswaDto)

    @Query("DELETE FROM siswa WHERE userId =:userId")
    suspend fun delete(userId: String)

    @Query("SELECT * FROM siswa WHERE userId =:id LIMIT 1")
    suspend fun getSiswa(id: String): SiswaDto?
}