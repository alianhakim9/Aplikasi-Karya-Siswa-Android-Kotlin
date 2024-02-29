package tedc.alian.data.remote.dto

import tedc.alian.data.remote.api.auth.UserDto

data class Login(
    val message: String,
    val data: DataLogin
)

data class DataLogin(
    val token: String,
    val user: UserDto,
)