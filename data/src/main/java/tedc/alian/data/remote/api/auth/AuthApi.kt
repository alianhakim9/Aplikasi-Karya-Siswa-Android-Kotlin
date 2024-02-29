package tedc.alian.data.remote.api.auth

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import tedc.alian.data.remote.dto.abstraksi.BaseResponse

interface AuthApi {
    @POST("api/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginDto>

    @POST("api/password/email")
    @FormUrlEncoded
    suspend fun forgotPassword(
        @Field("email") email: String
    ): Response<BaseResponse<Nothing>>

    @POST("api/password/reset")
    @FormUrlEncoded
    suspend fun resetPassword(
        @Field("token") token: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): Response<BaseResponse<Nothing>>
}