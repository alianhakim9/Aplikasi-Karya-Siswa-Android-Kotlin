package tedc.alian.data.remote.dto.abstraksi

data class BaseResponse<T>(val data: T, val message: String)
