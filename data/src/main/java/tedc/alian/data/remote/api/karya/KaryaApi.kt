package tedc.alian.data.remote.api.karya

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import tedc.alian.data.remote.dto.CountKaryaDto
import tedc.alian.data.remote.dto.abstraksi.BaseResponse

interface KaryaApi {

    companion object {
        const val URL_KARYA_CITRA = "api/karya/citra"
        const val URL_KARYA_TULIS = "api/karya/tulis"
        const val URL_KARYA_AUDIO_VISUAL = "api/karya/audio-visual"
        const val URL_KATEGORI_KARYA_CITRA = "$URL_KARYA_CITRA/kategori"
        const val URL_KATEGORI_KARYA_TULIS = "$URL_KARYA_TULIS/kategori"
        const val URL_KATEGORI_KARYA_AUDIO_VISUAL = "$URL_KARYA_AUDIO_VISUAL/kategori"
        const val URL_KOMENTAR_KARYA_CITRA = "$URL_KARYA_CITRA/komentar"
        const val URL_KOMENTAR_KARYA_TULIS = "$URL_KARYA_TULIS/komentar"
        const val URL_KOMENTAR_KARYA_AUDIO_VISUAL = "$URL_KARYA_AUDIO_VISUAL/komentar"
        const val URL_LIKE_KARYA_CITRA = "$URL_KARYA_CITRA/like/{karya_citra_id}"
        const val URL_LIKE_KARYA_TULIS = "$URL_KARYA_TULIS/like/{karya_tulis_id}"
        const val URL_LIKE_KARYA_AUDIO_VISUAL =
            "$URL_KARYA_AUDIO_VISUAL/like/{karya_audio_visual_id}"

    }

    /** endpoint karya citra **/
    @GET("$URL_KARYA_CITRA/all")
    suspend fun getAllKarya(): Response<KaryaCitraBaseResponse<List<KaryaCitraDto>>>

    @GET("$URL_KARYA_CITRA/owner/{id_siswa}")
    suspend fun getKaryaCitraKu(
        @Path("id_siswa") idSiswa: String
    ): Response<KaryaCitraBaseResponse<List<KaryaCitraDto>>>

    @POST("$URL_KARYA_CITRA/store")
    @Multipart
    suspend fun tambahKaryaCitra(
        @Part karya: MultipartBody.Part,
        @Part("nama_karya") namaKarya: RequestBody,
        @Part("kategori_karya_citra_id") kategoriKaryaCitraId: RequestBody,
        @Part("caption") caption: RequestBody,
    ): Response<BaseResponse<KaryaCitraDto>>

    @GET("$URL_KARYA_CITRA/validasi/all")
    suspend fun getKaryaYangBelumDiValidasi(): Response<BaseResponse<List<KaryaCitraDto>>>

    @POST("$URL_KARYA_CITRA/validasi/terima/{karyaCitraId}")
    suspend fun terimaKarya(
        @Path("karyaCitraId") karyaCitraId: String,
        @Query("isWatermarked") isWatermared: String
    ): Response<BaseResponse<Nothing>>

    @POST("$URL_KARYA_CITRA/validasi/tolak/{karyaCitraId}")
    @FormUrlEncoded
    suspend fun tolakKarya(
        @Path("karyaCitraId") karyaCitraId: String,
        @Field("keterangan") keterangan: String
    ): Response<BaseResponse<Nothing>>

    @GET("$URL_KARYA_CITRA/all")
    suspend fun getAllKaryaCitra(
        @Query("page") page: Int,
    ): Response<KaryaCitraBaseResponse<List<KaryaCitraDto>>>

    @GET("$URL_KARYA_CITRA/owner/{id_siswa}")
    suspend fun getAllKaryaCitraKu(
        @Path("id_siswa") idSiswa: String,
        @Query("page") page: Int
    ): Response<KaryaCitraBaseResponse<List<KaryaCitraDto>>>

    @POST("$URL_KARYA_CITRA/update/{id}?_method=PUT")
    @Multipart
    suspend fun ubahKaryaCitra(
        @Path("id") karyaCitraId: String,
        @Part karya: MultipartBody.Part,
        @Part("nama_karya") namaKarya: RequestBody,
        @Part("kategori_karya_citra_id") kategoriKaryaCitraId: RequestBody,
        @Part("caption") caption: RequestBody,
    ): Response<BaseResponse<Nothing>>

    @POST("$URL_KARYA_CITRA/update/{id}?_method=PUT")
    @FormUrlEncoded
    suspend fun ubahKaryaCitraTanpaKarya(
        @Path("id") karyaCitraId: String,
        @Field("nama_karya") namaKarya: String,
        @Field("caption") caption: String,
        @Field("kategori_karya_citra_id") kategoriKaryaCitraId: String
    ): Response<BaseResponse<Nothing>>

    @DELETE("$URL_KARYA_CITRA/delete/{id}")
    suspend fun hapusKaryaCitra(
        @Path("id") karyaCitraId: String
    ): Response<BaseResponse<Nothing>>

    @GET("$URL_KARYA_CITRA/ditolak")
    suspend fun getKaryaAllDitolak(
        @Query("page") page: Int
    ): Response<KaryaCitraBaseResponse<List<KaryaCitraDitolakDto>>>

    @GET("api/count-karya-ku/{id_siswa}")
    suspend fun countKaryaKu(
        @Path("id_siswa") idSiswa: String
    ): Response<CountKaryaDto>

    @GET("${URL_KARYA_CITRA}/all")
    suspend fun filterKaryaCitra(
        @Query("page") page: Int,
        @Query("kategori") kategori: String
    ): Response<BaseResponse<List<KaryaCitraDto>>>

    /** endpoint kategori karya citra **/

    @GET("$URL_KATEGORI_KARYA_CITRA/all")
    suspend fun getKategoriKaryaCitra(
    ): Response<BaseResponse<List<KategoriKaryaDto>>>

    @GET("${URL_KARYA_CITRA}/detail/{id}")
    suspend fun getDetailKaryaCitra(
        @Path("id") id: String
    ): Response<KaryaCitraDto>

    @POST("$URL_KATEGORI_KARYA_CITRA/store")
    suspend fun storeKategoriKaryaCitra(): Response<Nothing>

    @DELETE("$URL_KATEGORI_KARYA_CITRA/delete/{id}")
    suspend fun deleteKategoriKaryaCitra(): Response<Nothing>

    /** endpoint karya tulis **/

    @GET("$URL_KARYA_TULIS/all")
    suspend fun getAllKaryaTulis(
        @Query("page") page: Int
    ): Response<BaseResponse<List<KaryaTulisDto>>>

    @POST("$URL_KARYA_TULIS/store")
    suspend fun tambahKaryaTulis(
        @Body request: UploadKaryaTulisRequest
    ): Response<BaseResponse<KaryaTulisDto>>

    @GET("$URL_KARYA_TULIS/owner/{id_siswa}")
    suspend fun getKaryaTulisKu(
        @Path("id_siswa") idSiswa: String
    ): Response<BaseResponse<List<KaryaTulisDto>>>

    @GET("$URL_KARYA_TULIS/owner/{id_siswa}")
    suspend fun getAllKaryaTulisKu(
        @Path("id_siswa") idSiswa: String,
        @Query("page") page: Int
    ): Response<BaseResponse<List<KaryaTulisDto>>>

    @POST("$URL_KARYA_TULIS/update/{id}?_method=PUT")
    suspend fun ubahKaryaTulis(
        @Path("id") karyaCitraId: String,
        @Body request: UpdateKaryaTulisRequest
    ): Response<BaseResponse<Nothing>>

    @DELETE("$URL_KARYA_TULIS/delete/{id}")
    suspend fun hapusKaryaTulis(
        @Path("id") karyaTulisId: String
    ): Response<BaseResponse<Nothing>>

    @GET("${URL_KARYA_TULIS}/all")
    suspend fun filterKaryaTulis(
        @Query("page") page: Int,
        @Query("kategori") kategori: String
    ): Response<BaseResponse<List<KaryaTulisDto>>>

    /** endpoint kategori karya tulis **/

    @GET("$URL_KATEGORI_KARYA_TULIS/all")
    suspend fun getKategoriKaryaTulis(
    ): Response<BaseResponse<List<KategoriKaryaDto>>>

    @POST("$URL_KOMENTAR_KARYA_CITRA/{karya_citra_id}")
    @FormUrlEncoded
    suspend fun tambahKomentarCitra(
        @Path("karya_citra_id") idKaryaCitra: String,
        @Field("komentar") komentar: String,
    ): Response<BaseResponse<Nothing>>

    @POST("$URL_KOMENTAR_KARYA_TULIS/{id}")
    @FormUrlEncoded
    suspend fun tambahKomentarTulis(
        @Path("id") idKaryaTulis: String,
        @Field("komentar") komentar: String
    ): Response<BaseResponse<Nothing>>

    @POST(URL_LIKE_KARYA_CITRA)
    suspend fun likeKaryaCitra(
        @Path("karya_citra_id") karyaCitraId: String
    ): Response<BaseResponse<Nothing>>

    @POST(URL_LIKE_KARYA_TULIS)
    suspend fun likeKaryaTulis(
        @Path("karya_tulis_id") karyaTulisId: String
    ): Response<BaseResponse<Nothing>>

    @GET("${URL_KARYA_TULIS}/detail/{id}")
    suspend fun getDetailKaryaTulis(
        @Path("id") karyaTulisId: String
    ): Response<KaryaTulisDto>

    @GET("/api/notifikasi")
    suspend fun getNotifikasi(): Response<NotifikasiResponse>

    @GET("/api/notifikasi/siswa")
    suspend fun getNotifikasiSiswa(): Response<NotifikasiResponse>

    @POST("/api/notifikasi/update/{id_karya}/{id}?_method=PUT")
    suspend fun updateNotifikasi(
        @Path("id_karya") idKarya: String,
        @Path("id") id: String
    ): Response<BaseResponse<Nothing>>

    @DELETE("/api/notifikasi/guru/hapus/{id_notifikasi}")
    suspend fun hapusNotifikasiGuru(
        @Path("id_notifikasi") idNotifikasi: String
    ): Response<BaseResponse<Nothing>>

    @DELETE("/api/notifikasi/siswa/hapus/{id_siswa}")
    suspend fun hapusNotifikasiSiswa(
        @Path("id_siswa") idSiswa: String
    ): Response<BaseResponse<Nothing>>
}