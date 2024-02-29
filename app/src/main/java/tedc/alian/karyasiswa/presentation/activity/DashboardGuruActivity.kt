package tedc.alian.karyasiswa.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ActivityDashboardGuruBinding

@AndroidEntryPoint
class DashboardGuruActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardGuruBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.guru_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.extendedFab.isVisible = destination.id == R.id.guruHomeFragment
            binding.bottomNavigation.isVisible =
                destination.id == R.id.guruHomeFragment || destination.id == R.id.guruProfilFragment
        }
        binding.extendedFab.setOnClickListener {
            navController.navigate(R.id.action_guruHomeFragment_to_tambahKategoriFragment)
        }
    }
}