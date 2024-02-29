package tedc.alian.karyasiswa.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ActivityDashboardTimBinding

@AndroidEntryPoint
class DashboardTimActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardTimBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardTimBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.tim_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        setupBottomNav(navController)
        binding.extendedFab.setOnClickListener {
            navController.navigate(R.id.action_halamanUtamaTimFragment_to_tambahPromosiFragment)
        }
    }

    private fun setupBottomNav(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.extendedFab.isVisible =
                destination.id == R.id.halamanUtamaTimFragment
            binding.bottomNavigation.isVisible =
                destination.id == R.id.profilTimFragment ||
                        destination.id == R.id.halamanUtamaTimFragment
        }
    }
}