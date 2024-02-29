package tedc.alian.karyasiswa.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tedc.alian.utils.Constants
import javax.inject.Inject

@HiltViewModel
class BaseUiStateViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {
    fun setProgressDialogVisibility(visible: Boolean) {
        state[Constants.ALERT_DIALOG_VISIBILITY_KEY] = visible
    }

    fun isProgressDialogVisible(): Boolean {
        return state[Constants.ALERT_DIALOG_VISIBILITY_KEY] ?: false
    }

    fun setButtonEnabled(enabled: Boolean) {
        state[Constants.BUTTON_ENABLE_KEY] = enabled
    }

    fun isButtonEnabled(): Boolean = state[Constants.BUTTON_ENABLE_KEY] ?: false
}