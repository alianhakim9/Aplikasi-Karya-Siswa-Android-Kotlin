package tedc.alian.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tedc.alian.data.local.dao.GuruDao
import tedc.alian.data.local.dao.KaryaTulisDao
import tedc.alian.data.local.dao.SiswaDao
import tedc.alian.data.local.dao.TimDao
import tedc.alian.data.local.model.KaryaTulis
import tedc.alian.data.remote.api.guru.GuruDto
import tedc.alian.data.remote.api.siswa.SiswaDto
import tedc.alian.data.remote.dto.TimDto
import tedc.alian.data.remote.dto.converter.UserDtoTypeConverter

@Database(
    entities = [SiswaDto::class, GuruDto::class, TimDto::class, KaryaTulis::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(UserDtoTypeConverter::class)
abstract class DatabaseLokal : RoomDatabase() {
    abstract fun siswaDao(): SiswaDao
    abstract fun guruDao(): GuruDao
    abstract fun timDao(): TimDao
    abstract fun karyaTulisDao(): KaryaTulisDao
}