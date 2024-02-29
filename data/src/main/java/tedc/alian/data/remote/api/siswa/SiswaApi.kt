package tedc.alian.data.remote.api.siswa

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import tedc.alian.data.remote.dto.abstraksi.BaseResponse

interface SiswaApi {

    companion object {
        const val SISWA_PROFIL_URL = "api/siswa/profil"
    }

    @GET(SISWA_PROFIL_URL)
    suspend fun getProfilSiswa(): Response<BaseResponse<SiswaDto>>

    @POST("$SISWA_PROFIL_URL/update/{id}?_method=PUT")
    @Multipart
    suspend fun editProfilSiswa(
        @Path("id") id: String,
        @Part fotoProfil: MultipartBody.Part,
        @Part("nama_lengkap") namaLengkap: RequestBody,
        @Part("nisn") nisn: RequestBody,
        @Part("jenis_kelamin") jenisKelamin: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("ttl") ttl: RequestBody,
        @Part("agama") agama: RequestBody
    ): Response<BaseResponse<SiswaDto>>

    @POST("$SISWA_PROFIL_URL/update/{id}?_method=PUT")
    @FormUrlEncoded
    suspend fun editProfilTanpaFoto(
        @Path("id") idSiswa: String,
        @Field("nama_lengkap") namaLengkap: String,
        @Field("nisn") nisn: String,
        @Field("jenis_kelamin") jenisKelamin: String,
        @Field("alamat") alamat: String,
        @Field("ttl") ttl: String,
        @Field("agama") agama: String
    ): Response<BaseResponse<SiswaDto>>
}