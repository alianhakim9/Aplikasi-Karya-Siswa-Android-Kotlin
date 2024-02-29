package tedc.alian.karyasiswa.presentation.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.databinding.ActivityMainBinding
import tedc.alian.karyasiswa.presentation.viewmodel.AuthViewModel
import tedc.alian.utils.helper.startNewActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.splashScreen.value == true
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        login()
    }

    private fun login() {
        when (viewModel.getRoleId()) {
            "2" -> startNewActivity<DashboardGuruActivity>()
            "3" -> startNewActivity<DashboardSiswaActivity>()
            "4" -> startNewActivity<DashboardTimActivity>()
        }
    }
}
