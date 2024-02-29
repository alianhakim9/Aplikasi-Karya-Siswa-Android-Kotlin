package tedc.alian.karyasiswa.di

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tedc.alian.data.local.dao.GuruDao
import tedc.alian.data.local.dao.KaryaTulisDao
import tedc.alian.data.local.dao.SiswaDao
import tedc.alian.data.local.dao.TimDao
import tedc.alian.data.local.repository.MySharedPref
import tedc.alian.data.remote.api.auth.AuthApi
import tedc.alian.data.remote.api.builder.ApiBuilder
import tedc.alian.data.remote.api.guru.GuruApi
import tedc.alian.data.remote.api.karya.KaryaApi
import tedc.alian.data.remote.api.siswa.SiswaApi
import tedc.alian.data.remote.api.tim.TimApi
import tedc.alian.data.repository.*
import tedc.alian.karyasiswa.application.KaryaSiswaApp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideKaryaSiswaApp(
        @ApplicationContext app: Context
    ): KaryaSiswaApp = app as KaryaSiswaApp

    @Singleton
    @Provides
    fun provideAuthApi(
        apiBuilder: ApiBuilder
    ): AuthApi = apiBuilder.builder(AuthApi::class.java)

    @Singleton
    @Provides
    fun provideSiswaApi(
        apiBuilder: ApiBuilder
    ): SiswaApi = apiBuilder.builder(SiswaApi::class.java)

    @Singleton
    @Provides
    fun provideKaryaAPI(
        apiBuilder: ApiBuilder
    ): KaryaApi = apiBuilder.builder(KaryaApi::class.java)

    @Singleton
    @Provides
    fun provideGuruApi(apiBuilder: ApiBuilder): GuruApi = apiBuilder.builder(GuruApi::class.java)

    @Singleton
    @Provides
    fun provideTimApi(apiBuilder: ApiBuilder): TimApi = apiBuilder.builder(TimApi::class.java)

    @Singleton
    @Provides
    fun provideAuthRepository(
        authApi: AuthApi,
        context: Application,
        mySharedPref: MySharedPref
    ): AuthRepository = AuthRepository(authApi, context, mySharedPref)

    @Singleton
    @Provides
    fun provideSiswaRepository(
        api: SiswaApi,
        @ApplicationContext context: Context,
        siswaDao: SiswaDao,
        preferences: MySharedPref
    ): SiswaRepository =
        SiswaRepository(api, context, siswaDao, preferences)

    @Singleton
    @Provides
    fun provideKaryaRepository(
        api: KaryaApi,
        preferences: MySharedPref,
        @ApplicationContext context: Context,
        karyaTulisDao: KaryaTulisDao
    ): KaryaRepository = KaryaRepository(api, context, preferences, karyaTulisDao)

    @Singleton
    @Provides
    fun provideGuruRepository(
        api: GuruApi,
        guruDao: GuruDao,
        preferences: MySharedPref,
        @ApplicationContext context: Context
    ): GuruRepository = GuruRepository(context, guruDao, preferences, api)

    @Singleton
    @Provides
    fun provideListKaryaCitraRepository(
        api: KaryaApi,
        preferences: MySharedPref
    ): ListKaryaCitraRepository = ListKaryaCitraRepository(api, preferences)

    @Provides
    fun provideeTimRepository(
        api: TimApi,
        @ApplicationContext context: Context,
        preferences: MySharedPref,
        dao: TimDao
    ): TimRepository = TimRepository(api, context, dao, preferences)

    @Provides
    fun provideImageRequest(
        @ApplicationContext context: Context
    ) = ImageRequest.Builder(context)

    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader {
        return ImageLoader(context)
            .newBuilder()
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }.build()
    }

    @Provides
    fun provideMySharedPref(
        @ApplicationContext context: Context
    ) = MySharedPref(context)
}