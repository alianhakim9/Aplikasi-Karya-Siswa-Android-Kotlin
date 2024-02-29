package tedc.alian.data.remote.api.tim

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import tedc.alian.data.remote.dto.PromosiDto
import tedc.alian.data.remote.dto.TimDto
import tedc.alian.data.remote.dto.abstraksi.BaseResponse

interface TimApi {
    companion object {
        const val TIM_PROFIL_URL = "api/tim-ppdb/profil"
        const val URL_PROMOSI = "api/promosi"
    }

    @GET(TIM_PROFIL_URL)
    suspend fun getProfil(): Response<BaseResponse<TimDto?>>

    @POST("${TIM_PROFIL_URL}/update/{id}?_method=PUT")
    @FormUrlEncoded
    suspend fun editProfil(
        @Path("id") timId: String,
        @Field("nama_lengkap") namaLengkap: String,
        @Field("jabatan") jabatan: String
    ): Response<BaseResponse<TimDto>>

    @Multipart
    @POST("${URL_PROMOSI}/store")
    suspend fun tambahPromosi(
        @Part gambar: MultipartBody.Part,
        @Part("nama_promosi") namaPromosi: RequestBody,
        @Part("tanggal_promosi") tanggalPromosi: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("status") status: RequestBody,
    ): Response<BaseResponse<Nothing>>

    @GET("${URL_PROMOSI}/all")
    suspend fun getListPromosi(): Response<BaseResponse<List<PromosiDto>>>

    @GET("${URL_PROMOSI}/owner/{id}")
    suspend fun getListPromosiByTim(
        @Path("id") idTim: String
    ): Response<BaseResponse<List<PromosiDto>>>

    @DELETE("${URL_PROMOSI}/delete/{id}")
    suspend fun hapusPromosi(
        @Path("id") idPromosi: String
    ): Response<BaseResponse<Nothing>>

    @POST("${URL_PROMOSI}/update/{id}?_method=PUT")
    @FormUrlEncoded
    suspend fun ubahPromosiTanpaGambar(
        @Path("id") id: String,
        @Field("nama_promosi") namaPromosi: String,
        @Field("keterangan") keterangan: String,
        @Field("status") status: String,
        @Field("tanggal_promosi") tanggalPromosi: String,
    ): Response<BaseResponse<PromosiDto>>

    @POST("${URL_PROMOSI}/update/{id}?_method=PUT")
    @Multipart
    suspend fun ubahPromosi(
        @Path("id") id: String,
        @Part gambar: MultipartBody.Part,
        @Part("nama_promosi") namaPromosi: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("status") status: RequestBody,
        @Part("tanggal_promosi") tanggal_promosi: RequestBody,
    ): Response<BaseResponse<PromosiDto>>

    @GET("${URL_PROMOSI}/active")
    suspend fun getActivePromosi(): Response<BaseResponse<PromosiDto>>
}