package tedc.alian.data.remote.api.guru

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.data.remote.dto.abstraksi.BaseResponse

interface GuruApi {
    companion object {
        const val URL_PROFIL_GURU = "api/guru/profil"
        const val URL_KATEGORI_KARYA_CITRA = "api/karya/citra/kategori"
        const val URL_KATEGORI_KARYA_TULIS = "api/karya/tulis/kategori"
    }

    @GET(URL_PROFIL_GURU)
    suspend fun getProfilGuru(): Response<BaseResponse<GuruDto>>

    @POST("$URL_PROFIL_GURU/update/{id}?_method=PUT")
    @Multipart
    suspend fun editProfilGuru(
        @Path("id") id: String,
        @Part fotoProfil: MultipartBody.Part,
        @Part("nama_lengkap") namaLengkap: RequestBody,
        @Part("nuptk") nuptk: RequestBody,
        @Part("jenis_kelamin") jenisKelamin: RequestBody,
        @Part("agama") agama: RequestBody,
        @Part("ttl") ttl: RequestBody,
        @Part("jabatan") jabatan: RequestBody,
        @Part("gelar") gelar: RequestBody,
        @Part("alamat") alamat: RequestBody,
    ): Response<BaseResponse<GuruDto>>

    @POST("$URL_PROFIL_GURU/update/{id}?_method=PUT")
    @FormUrlEncoded
    suspend fun editProfilGuruTanpaFoto(
        @Path("id") idGuru: String,
        @Field("nama_lengkap") namaLengkap: String,
        @Field("nuptk") nuptk: String,
        @Field("jenis_kelamin") jenisKelamin: String,
        @Field("agama") agama: String,
        @Field("ttl") ttl: String,
        @Field("jabatan") jabatan: String,
        @Field("gelar") gelar: String,
        @Field("alamat") alamat: String,
    ): Response<BaseResponse<GuruDto>>

    @POST("$URL_KATEGORI_KARYA_CITRA/store")
    @FormUrlEncoded
    suspend fun tambahKategoriKaryaCitra(
        @Field("nama_kategori") namaKategori: String
    ): Response<BaseResponse<Nothing>>

    @GET("$URL_KATEGORI_KARYA_CITRA/all")
    suspend fun listKategoriKaryaCitra(): Response<BaseResponse<List<KategoriKaryaDto>>>

    @DELETE("$URL_KATEGORI_KARYA_CITRA/delete/{id}")
    suspend fun hapusKategorikaryaCitra(
        @Path("id") kategoriId: String
    ): Response<BaseResponse<Nothing>>

    @POST("$URL_KATEGORI_KARYA_TULIS/store")
    @FormUrlEncoded
    suspend fun tambahKategoriKaryaTulis(
        @Field("nama_kategori") namaKategori: String
    ): Response<BaseResponse<Nothing>>

    @GET("$URL_KATEGORI_KARYA_TULIS/all")
    suspend fun listKategoriKaryaTulis(): Response<BaseResponse<List<KategoriKaryaDto>>>

    @DELETE("$URL_KATEGORI_KARYA_TULIS/delete/{id}")
    suspend fun hapusKategoriKaryaTulis(
        @Path("id") kategoriId: String
    ): Response<BaseResponse<Nothing>>
}