package tedc.alian.data.local.repository

import android.content.Context
import javax.inject.Inject

class MySharedPref @Inject constructor(
    context: Context
) {
    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    var editor = sharedPreferences.edit()

    companion object {
        const val TOKEN_KEY = "token_key"
        const val USER_ID_KEY = "user_id_key"
        const val SISWA_ID_KEY = "siswa_id_key"
        const val GURU_ID_KEY = "guru_id_key"
        const val TIM_ID_KEY = "tim_id_key"
        const val REMEMBER_ME_KEY = "remember_me_key"
        const val ROLE_ID_KEY = "role_id_key"
        const val PREFERENCES_NAME = "karya_siswa_pref"
        const val IS_READ_KEY = "is_read_key"
    }

    fun getToken(): String? = sharedPreferences.getString(TOKEN_KEY, "")
    fun setToken(token: String) {
        editor.putString(TOKEN_KEY, token).apply()
    }

    fun getUserId(): String? = sharedPreferences.getString(USER_ID_KEY, "")
    fun setUserId(userId: String) {
        editor.putString(USER_ID_KEY, userId).apply()
    }

    fun getSiswaId(): String? = sharedPreferences.getString(SISWA_ID_KEY, "")
    fun setSiswaId(siswaId: String) {
        editor.putString(SISWA_ID_KEY, siswaId).apply()
    }

    fun getGuruId(): String? = sharedPreferences.getString(GURU_ID_KEY, "")
    fun setGuruId(guruId: String) {
        editor.putString(GURU_ID_KEY, guruId).apply()
    }

    fun getTimId(): String? = sharedPreferences.getString(TIM_ID_KEY, "")
    fun setTimId(timId: String) {
        editor.putString(TIM_ID_KEY, timId).apply()
    }

    fun isRememberMe(): Boolean = sharedPreferences.getBoolean(REMEMBER_ME_KEY, false)
    fun setRememberMe(rememberMe: Boolean) {
        editor.putBoolean(REMEMBER_ME_KEY, rememberMe).apply()
    }

    fun getRoleId(): String? = sharedPreferences.getString(ROLE_ID_KEY, "")
    fun setRoleId(roleId: String) {
        editor.putString(ROLE_ID_KEY, roleId).apply()
    }

    fun getIsRead(): Boolean = sharedPreferences.getBoolean(IS_READ_KEY, false)
    fun setIsRead(isRead: Boolean) {
        editor.putBoolean(IS_READ_KEY, isRead).apply()
    }

    fun logout() {
        editor.apply {
            remove(TOKEN_KEY)
            remove(USER_ID_KEY)
            remove(SISWA_ID_KEY)
            remove(GURU_ID_KEY)
            remove(TIM_ID_KEY)
            remove(ROLE_ID_KEY)
            remove(REMEMBER_ME_KEY)
            apply()
        }
    }
}