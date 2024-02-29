package tedc.alian.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tedc.alian.data.remote.api.guru.GuruDto

@Dao
interface GuruDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(guru: GuruDto)

    @Query("DELETE FROM guru WHERE userId =:userId")
    suspend fun delete(userId: String)

    @Query("SELECT * FROM guru WHERE userId =:id LIMIT 1")
    suspend fun getGuru(id: String): GuruDto?
}