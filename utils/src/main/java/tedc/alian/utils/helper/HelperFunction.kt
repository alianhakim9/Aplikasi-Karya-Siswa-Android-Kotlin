package tedc.alian.utils.helper

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tedc.alian.utils.Resource
import java.io.File
import java.util.Calendar

fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectivityManager.activeNetwork ?: return false
    } else {
        TODO("VERSION.SDK_INT < M")
    }
    val activeNetwork =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

inline fun <T> apiCall(
    context: Context,
    onSuccess: () -> Resource<T>
): Resource<T> {
    return try {
        if (!context.isNetworkConnected()) {
            Resource.error(Throwable("Tidak ada koneksi internet"))
        } else {
            onSuccess()
        }
    } catch (e: Exception) {
        Resource.error(Throwable(e.message))
    }
}

fun Fragment.createProgressDialog(@LayoutRes layoutResId: Int): AlertDialog {
    val progressDialogView = layoutInflater.inflate(layoutResId, null)
    return MaterialAlertDialogBuilder(requireContext())
        .setView(progressDialogView)
        .setCancelable(false)
        .create()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message, duration).show()
}

fun Fragment.repeatOnViewLifecycle(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(lifecycleState) {
            launch { block() }
        }
    }
}

inline fun <reified T : Activity> Fragment.startActivityAndFinish() {
    Intent(requireActivity(), T::class.java).also { intent ->
        startActivity(intent)
        requireActivity().finish()
    }
}

inline fun <reified T : Activity> Activity.startNewActivity() {
    Intent(this, T::class.java).also { intent ->
        startActivity(intent)
        this.finish()
    }
}

inline fun <reified T : Activity> Fragment.startActivity() {
    Intent(requireContext(), T::class.java).apply {
        startActivity(this)
    }
}

const val SIANG = "Selamat siang"
const val SORE = "Selamat sore"
const val MALAM = "Selamat malam"
const val PAGI = "Selamat pagi"
const val SIANG_START = 12
const val SORE_START = 15
const val MALAM_START = 19
const val MALAM_END = 23

fun setGreeting(textView: TextView) {
    val cal: Calendar = Calendar.getInstance()
    cal.timeInMillis = System.currentTimeMillis()

    when (cal.get(Calendar.HOUR_OF_DAY)) {
        in SIANG_START until SORE_START -> {
            textView.text = SIANG
        }

        in SORE_START until MALAM_START -> {
            textView.text = SORE
        }

        in MALAM_START..MALAM_END -> {
            textView.text = MALAM
        }

        else -> {
            textView.text = PAGI
        }
    }
}

fun Fragment.showDialog(
    title: String,
    message: String,
    negativeButtonText: String,
    positiveButtonText: String,
    onPositiveButton: () -> Unit,
) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.cancel()
        }
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveButton()
            dialog.dismiss()
        }
        .show()
}

fun Fragment.showDialogWithNegativeAction(
    title: String,
    message: String,
    negativeButtonText: String,
    positiveButtonText: String,
    onPositiveButton: () -> Unit,
    onNegativeButton: () -> Unit
) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.cancel()
            onNegativeButton()
        }
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveButton()
            dialog.dismiss()
        }
        .show()
}

fun Fragment.showLogoutDialog(
    onPositiveAction: () -> Unit
) {
    showDialog(
        title = "Logout",
        message = "Apakah anda yakin akan logout?",
        negativeButtonText = "Batal",
        positiveButtonText = "Logout"
    ) {
        onPositiveAction()
    }
}

fun Fragment.hideKeyboard() {
    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
    view?.clearFocus()
}

fun EditText.addListener(
    beforeTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null,
    onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null,
    afterTextChanged: ((Editable?) -> Unit)? = null
) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged?.invoke(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChanged?.invoke(s)
        }
    })
}

fun getFileSize(file: File): Long {
    return if (file.exists()) {
        file.length()
    } else {
        0L
    }
}

fun getFileSizeInMb(file: File): Long {
    val fileSizeInKb = getFileSize(file) / 1024
    return fileSizeInKb / 1024
}

fun ImageView.loadProfilePicture(
    imageUrl: String?,
    @DrawableRes imageResource: Int?,
    @DrawableRes placeholderRes: Int
) {
    this.load(imageUrl ?: imageResource) {
        placeholder(placeholderRes)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.loadPromosi(source: String) {
    this.load(source) {
        crossfade(true)
        transformations(RoundedCornersTransformation(15f))
    }
}

fun Fragment.startCropActivity(uri: Uri) {
    CropImage.activity(uri)
        .setGuidelines(CropImageView.Guidelines.ON)
        .setAspectRatio(1080, 1080)
        .setCropShape(CropImageView.CropShape.RECTANGLE)
        .start(requireContext(), this)
}

fun Fragment.startCropKarya(uri: Uri) {
    CropImage.activity(uri)
        .setGuidelines(CropImageView.Guidelines.ON)
        .start(requireContext(), this)
}

fun Fragment.createNotificationChannel(channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notification Title"
        val descriptionText = "Notification Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun Fragment.showNotification(
    title: String,
    description: String,
    channelId: String,
    notificationId: Int,
    @DrawableRes smallIcon: Int
) {
    val builder = NotificationCompat.Builder(requireContext(), channelId)
        .setSmallIcon(smallIcon)
        .setContentTitle(title)
        .setContentText(description)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(requireContext())) {
        notify(notificationId, builder.build())
    }
}


fun Fragment.showToastNotification(
    @LayoutRes layoutId: Int
) {
    val inflater = layoutInflater
    val layout = inflater.inflate(layoutId, null)
    val toast = Toast(requireContext())
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
    val params = toast.view?.layoutParams as? ViewGroup.MarginLayoutParams
    params?.width = ViewGroup.LayoutParams.MATCH_PARENT
    toast.duration = Toast.LENGTH_LONG
    toast.view = layout
    toast.show()
}

fun Fragment.onBackPressedKarya(
    showDialogBack: () -> Unit
) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialogBack()
            }
        })
}

fun Fragment.showDialogBack(
    condition: Boolean,
    onPositiveButton: () -> Unit,
    defaultBackAction: () -> Unit
) {
    if (condition) {
        showDialog(
            "Kembali",
            "Apakah anda yakin akan kembali?",
            "Batal",
            "Ya",
            onPositiveButton = {
                onPositiveButton()
            }
        )
    } else {
        defaultBackAction()
    }
}