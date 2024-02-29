package tedc.alian.karyasiswa.presentation.activity

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ActivityDashboardSiswaBinding
import java.lang.reflect.Method


@AndroidEntryPoint
class DashboardSiswaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardSiswaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.siswa_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        setupBottomNav(navController)
        binding.extendedFab.setOnClickListener {
            navController.navigate(R.id.action_homeSiswaFragment_to_tambahKategoriKaryaFragment)
        }
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m: Method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupBottomNav(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.extendedFab.isVisible =
                destination.id == R.id.homeSiswaFragment
            binding.bottomNavigation.isVisible =
                destination.id == R.id.listKaryaKuFragment ||
                        destination.id == R.id.listKaryaSiswaFragment ||
                        destination.id == R.id.profilSiswaFragment ||
                        destination.id == R.id.homeSiswaFragment
        }
    }
}