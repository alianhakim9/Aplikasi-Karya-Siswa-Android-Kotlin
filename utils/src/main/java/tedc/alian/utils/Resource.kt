package tedc.alian.utils

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable, val message: String? = null) : Resource<Nothing>()
    data class Loading(val msg: String? = null) : Resource<Nothing>()

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Success(data)
        }

        fun error(throwable: Throwable, message: String? = null): Resource<Nothing> {
            return Error(throwable, message)
        }

        fun loading(msg: String): Resource<Nothing> {
            return Loading(msg)
        }
    }
}
