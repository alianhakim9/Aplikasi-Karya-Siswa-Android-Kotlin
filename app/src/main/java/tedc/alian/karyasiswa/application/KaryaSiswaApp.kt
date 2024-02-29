package tedc.alian.karyasiswa.application

import android.app.Application
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KaryaSiswaApp : Application(), OnSharedPreferenceChangeListener, DefaultLifecycleObserver {
    override fun onCreate() {
        super<Application>.onCreate()
        getSettings()
    }

    private fun getSettings() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "theme") {
            val prefs = sharedPreferences?.getString(key, "bawaan sistem")
            when (prefs?.lowercase()) {
                "mode terang" -> {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                }

                "mode gelap" -> {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                }

                "bawaan sistem" -> {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}