package tedc.alian.karyasiswa.presentation.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentAuthDashboardBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.AuthViewModel
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showDialog

@AndroidEntryPoint
class AuthDashboardFragment : BaseFragment<FragmentAuthDashboardBinding>() {

    private val viewModel by viewModels<AuthViewModel>()
    private var isRead = false

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAuthDashboardBinding {
        return FragmentAuthDashboardBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        uiAction()
    }

    private fun uiSetup() {
        viewModel.getIsRead()
        repeatOnViewLifecycle {
            viewModel.isRead.collectLatest { value ->
                binding.isReading.isChecked = value
                isRead = value
            }
        }
        binding.isReading.setOnCheckedChangeListener { _, isChecked ->
            binding.btnLanjutkan.isEnabled = isChecked
        }
    }

    private fun uiAction() {
        val termAndCondition = SyaratDanKetentuanBottomSheet()
        binding.btnLanjutkan.setOnClickListener {
            if (isRead) {
                navigateToLogin()
            } else {
                showDialog(
                    "Apakah kamu yakin sudah membaca syarat dan ketentuan?",
                    "Dengan membaca syarat dan ketentuan, pengguna sudah memahami dam mengetahui aspek apa saja perlu diperhatikan dalam penggunaan aplikasi ini",
                    "Belum",
                    "Sudah",
                    onPositiveButton = {
                        navigateToLogin()
                        viewModel.setIsRead(true)
                    }
                )
            }
        }
        binding.tvSyaratKetentuan.setOnClickListener {
            termAndCondition.show(childFragmentManager, "SyaratDanKetentuanBottomSheet")
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_authDashboardFragment_to_loginFragment)
    }
}