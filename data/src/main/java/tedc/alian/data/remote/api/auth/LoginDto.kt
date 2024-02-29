package tedc.alian.data.remote.api.auth

import tedc.alian.data.remote.dto.DataLogin
import tedc.alian.data.remote.dto.Login


data class LoginDto(
    val message: String,
    val data: DataLoginDto,
) {
    fun toLogin(): Login {
        return Login(message, data.toDataLogin())
    }
}

data class DataLoginDto(
    val token: String,
    val user: UserDto,
) {
    fun toDataLogin(): DataLogin {
        return DataLogin(token, user)
    }
}