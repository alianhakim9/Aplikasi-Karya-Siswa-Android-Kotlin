package tedc.alian.data.repository

import android.content.Context
import tedc.alian.data.local.repository.MySharedPref
import tedc.alian.data.remote.api.auth.AuthApi
import tedc.alian.data.remote.dto.Login
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.apiCall
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val context: Context,
    private val preferences: MySharedPref
) {
    suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean
    ): Resource<Login?> {
        return if (email.isNotEmpty() || password.isNotEmpty()) {
            apiCall(context) {
                val response = authApi.login(email, password)
                if (response.isSuccessful) {
                    val userId = response.body()?.toLogin()?.data?.user?.id.toString()
                    val token = response.body()?.toLogin()?.data?.token.toString()
                    val roleId = response.body()?.data?.user?.roleId.toString()
                    if (rememberMe) {
                        preferences.setUserId(userId)
                        preferences.setRoleId(roleId)
                        preferences.setToken(token)
                        preferences.setRememberMe(true)
                    }
                    Resource.success(data = response.body()?.toLogin())
                } else {
                    when (response.code()) {
                        401, 422 -> {
                            tampilError("Login gagal, cek kembali email atau password")
                        }
                        else -> {
                            tampilError("Terjadi kesalahan server")
                        }
                    }
                }
            }
        } else {
            tampilError("Harap lengkapi email atau password")
        }
    }

    suspend fun cariAkun(email: String): Resource<String?> {
        return if (email.isEmpty()) {
            tampilError("Email tidak boleh kosong")
        } else {
            apiCall(context) {
                val response = authApi.forgotPassword(email)
                if (response.isSuccessful) Resource.success(email)
                else tampilError("Alamat email tidak ditemukan")
            }
        }
    }

    suspend fun resetPassword(
        token: String,
        email: String,
        password: String,
        konfirmasiPassword: String
    ): Resource<String?> {
        if (password.isEmpty() || konfirmasiPassword.isEmpty()) {
            return tampilError("Harap lengkapi data")
        }
        if (password.length < 8) {
            return tampilError("Password minimal 8 karakter")
        }
        if (password != konfirmasiPassword) {
            return tampilError("Password tidak sama")
        }
        return apiCall(context) {
            val response = authApi.resetPassword(token, email, password, konfirmasiPassword)
            if (response.isSuccessful) Resource.success("Password berhasil diubah")
            else tampilError("Gagal melakukan reset password")
        }
    }

    private fun tampilError(error: String): Resource<Nothing> {
        return Resource.error(throwable = Throwable(error))
    }
}