package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_profil

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.siswa.SiswaDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentProfilSiswaBinding
import tedc.alian.karyasiswa.presentation.activity.MainActivity
import tedc.alian.karyasiswa.presentation.activity.SettingsActivity
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.AuthViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.SiswaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfilSiswaFragment : BaseFragment<FragmentProfilSiswaBinding>() {

    private val viewModel: SiswaViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }
    private var siswa: SiswaDto? = null

    @Inject
    lateinit var imageLoader: ImageLoader
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfilSiswaBinding {
        return FragmentProfilSiswaBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        uiAction()
    }

    private fun getData() {
        viewModel.getProfil()
        profilObserver()
    }

    private fun uiAction() {
        binding.profilCard.btnLogout.setOnClickListener {
            showKonfirmasiDialog()
        }
        binding.profilCard.btnEditProfile.setOnClickListener {
            val action =
                ProfilSiswaFragmentDirections
                    .actionProfilSiswaFragmentToEditProfilSiswaFragment(siswa!!)
            findNavController().navigate(action)
        }
        binding.cvKaryaCitra.setOnClickListener {
            findNavController().navigate(R.id.action_profilSiswaFragment_to_listKaryaCitraKuFragment)
        }
        binding.cvKaryaTulis.setOnClickListener {
            findNavController().navigate(R.id.action_profilSiswaFragment_to_listKaryaTulisKuFragment)
        }
        binding.cvStatusKarya.setOnClickListener {
            findNavController().navigate(R.id.action_profilSiswaFragment_to_listKaryaKuDitolakFragment)
        }
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            getData()
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.pengaturan -> {
                    startActivity<SettingsActivity>()
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getProfil()
    }

    private fun showKonfirmasiDialog() {
        showLogoutDialog {
            progressDialog.show()
            authViewModel.logoutSiswa()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun profilObserver() {
        repeatOnViewLifecycle {
            launch {
                viewModel.profil.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            with(binding) {
                                resource.data?.also { data ->
                                    swipeRefresh.isRefreshing = false
                                    profilCard.tvName.text = data.namaLengkap
                                    profilCard.tvNisnOrNuptk.text = "NISN: ${data.nisn}"
                                    profilCard.profilePicture.loadProfilePicture(
                                        imageUrl = data.fotoProfil,
                                        placeholderRes = R.drawable.ic_avatar,
                                        imageResource = R.drawable.ic_avatar
                                    )
                                    profilCard.agama.text = data.agama
                                    if (data.jenisKelamin == "L") {
                                        profilCard.jenisKelamin.text = "Laki-Laki"
                                    } else {
                                        profilCard.jenisKelamin.text = "Perempuan"
                                    }
                                    profilCard.ttl.text = data.alamat
                                    siswa = data
                                }
                            }
                        }

                        is Resource.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            showToast(resource.throwable.message.toString())
                        }

                        else -> {}
                    }
                }
            }
            launch {
                authViewModel.onLogout.collectLatest {
                    if (it) {
                        startActivityAndFinish<MainActivity>()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }
}
