package tedc.alian.karyasiswa.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tedc.alian.data.local.dao.GuruDao
import tedc.alian.data.local.dao.SiswaDao
import tedc.alian.data.local.dao.TimDao
import tedc.alian.data.local.repository.MySharedPref
import tedc.alian.data.remote.dto.Login
import tedc.alian.data.repository.AuthRepository
import tedc.alian.utils.Constants
import tedc.alian.utils.Resource
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val siswaDao: SiswaDao,
    private val guruDao: GuruDao,
    private val timDao: TimDao,
    private val mySharedPref: MySharedPref,
    private val state: SavedStateHandle
) : ViewModel() {

    private val loading = Resource.loading("loading")

    private val _splashScreen = MutableLiveData(false)
    val splashScreen: LiveData<Boolean>
        get() = _splashScreen

    private val _login =
        MutableSharedFlow<Resource<Login?>>()
    val login: SharedFlow<Resource<Login?>> get() = _login

    private val _cariAkun = MutableSharedFlow<Resource<String?>>()
    val cariAkun: SharedFlow<Resource<String?>> get() = _cariAkun

    private val _resetPassword = MutableSharedFlow<Resource<String?>>()
    val resetPassword: SharedFlow<Resource<String?>> get() = _resetPassword

    private val _onLogout = MutableStateFlow(false)
    val onLogout: StateFlow<Boolean> get() = _onLogout

    private val _isRead = MutableStateFlow(false)
    val isRead: StateFlow<Boolean> get() = _isRead

    init {
        viewModelScope.launch {
            _splashScreen.value = true
            delay(2000)
            _splashScreen.value = false
        }
    }

    fun login(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _login.emit(loading)
            _login.emit(repository.login(email, password, rememberMe))
        }
    }

    fun logoutSiswa() {
        viewModelScope.launch {
            val userId = mySharedPref.getUserId().toString()
            val currentSiswa = siswaDao.getSiswa(userId)
            if (currentSiswa != null) siswaDao.delete(currentSiswa.userId)
            mySharedPref.logout()
            delay(1000)
            _onLogout.emit(true)
        }
    }

    fun logoutGuru() {
        viewModelScope.launch {
            val userId = mySharedPref.getUserId().toString()
            val currentGuru = guruDao.getGuru(userId)
            if (currentGuru != null) guruDao.delete(currentGuru.userId)
            mySharedPref.logout()
            delay(1000)
            _onLogout.emit(true)
        }
    }

    fun logoutTim() {
        viewModelScope.launch {
            val userId = mySharedPref.getUserId().toString()
            val currentTim = timDao.getTim(userId)
            if (currentTim != null) timDao.delete(currentTim.userId)
            mySharedPref.logout()
            delay(1000)
            _onLogout.emit(true)
        }
    }

    fun cariAkun(email: String) {
        viewModelScope.launch {
            _cariAkun.emit(loading)
            _cariAkun.emit(repository.cariAkun(email))
        }
    }

    fun resetPassword(token: String, email: String, password: String, konfirmasiPassword: String) {
        viewModelScope.launch {
            _resetPassword.emit(loading)
            _resetPassword.emit(
                repository.resetPassword(token, email, password, konfirmasiPassword)
            )
        }
    }

    fun getRoleId(): String = mySharedPref.getRoleId() ?: ""

    // ui state handle
    fun setEmail(email: String) {
        state[Constants.EMAIL_SAVED_KEY] = email
    }

    fun getEmail(): String? = state[Constants.EMAIL_SAVED_KEY]

    fun setPassword(password: String) {
        state[Constants.PASSWORD_SAVED_KEY] = password
    }

    fun getPassword(): String? = state[Constants.PASSWORD_SAVED_KEY]

    fun setPasswordConfirm(confirmPassword: String) {
        state[Constants.PASSWORD_CONFIRM_SAVED_KEY] = confirmPassword
    }

    fun getPasswordConfirm(): String? = state[Constants.PASSWORD_CONFIRM_SAVED_KEY]

    fun setToken(token: String) {
        state[Constants.TOKEN_SAVED_KEY] = token
    }

    fun getToken(): String? = state[Constants.TOKEN_SAVED_KEY]

    fun getIsRead() {
        _isRead.value = mySharedPref.getIsRead()
    }

    fun setIsRead(isRead: Boolean) = mySharedPref.setIsRead(isRead)
}