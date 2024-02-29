package tedc.alian.karyasiswa.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tedc.alian.data.local.DatabaseLokal
import tedc.alian.data.local.dao.GuruDao
import tedc.alian.data.local.dao.KaryaTulisDao
import tedc.alian.data.local.dao.SiswaDao
import tedc.alian.data.local.dao.TimDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Singleton
    @Provides
    fun provideAppLocalDatabase(
        @ApplicationContext context: Context
    ): DatabaseLokal = Room.databaseBuilder(
        context, DatabaseLokal::class.java, "karya_siswa_db"
    ).build()

    @Singleton
    @Provides
    fun provideSiswaDao(database: DatabaseLokal): SiswaDao = database.siswaDao()

    @Singleton
    @Provides
    fun provideGuruDao(database: DatabaseLokal): GuruDao = database.guruDao()

    @Singleton
    @Provides
    fun provideTimDao(database: DatabaseLokal): TimDao = database.timDao()

    @Singleton
    @Provides
    fun provideKaryaTulisDao(database: DatabaseLokal): KaryaTulisDao = database.karyaTulisDao()
}