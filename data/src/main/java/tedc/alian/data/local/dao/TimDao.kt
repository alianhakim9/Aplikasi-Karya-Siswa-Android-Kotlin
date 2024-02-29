package tedc.alian.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tedc.alian.data.remote.dto.TimDto

@Dao
interface TimDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tim: TimDto)

    @Query("DELETE FROM tim WHERE userId =:userId")
    suspend fun delete(userId: String)

    @Query("SELECT * FROM tim WHERE userId =:id LIMIT 1")
    suspend fun getTim(id: String): TimDto?
}