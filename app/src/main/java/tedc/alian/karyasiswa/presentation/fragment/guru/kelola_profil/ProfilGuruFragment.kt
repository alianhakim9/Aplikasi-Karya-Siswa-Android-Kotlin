package tedc.alian.karyasiswa.presentation.fragment.guru.kelola_profil

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
import tedc.alian.data.remote.api.guru.GuruDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentProfilGuruBinding
import tedc.alian.karyasiswa.presentation.activity.MainActivity
import tedc.alian.karyasiswa.presentation.activity.SettingsActivity
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.AuthViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.GuruViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.loadProfilePicture
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showLogoutDialog
import tedc.alian.utils.helper.startActivity
import tedc.alian.utils.helper.startActivityAndFinish
import javax.inject.Inject

@AndroidEntryPoint
class ProfilGuruFragment : BaseFragment<FragmentProfilGuruBinding>() {

    private val viewModel: GuruViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }
    private var guru: GuruDto? = null

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfilGuruBinding {
        return FragmentProfilGuruBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        uiAction()
    }

    private fun getData() {
        viewModel.getProfil()
    }

    private fun uiAction() {
        binding.btnDetail.setOnClickListener {
            findNavController().navigate(R.id.action_guruProfilFragment_to_listWaitingValidasiKaryaFragment)
        }

        binding.profilCard.btnLogout.setOnClickListener {
            showLogoutDialog {
                progressDialog.show()
                authViewModel.logoutGuru()
            }
        }

        binding.profilCard.btnEditProfile.setOnClickListener {
            if (guru != null) {
                val action =
                    ProfilGuruFragmentDirections.actionGuruProfilFragmentToEditProfilGuruFragment(
                        guru!!
                    )
                findNavController().navigate(action)
            }
        }
        binding.btnDetailKategori.setOnClickListener {
            findNavController().navigate(R.id.action_guruProfilFragment_to_listKategoriKaryaFragment)
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


    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.profil.collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                        }

                        is Resource.Success -> {
                            with(binding) {
                                resource.data?.also { data ->
                                    swipeRefresh.isRefreshing = false
                                    profilCard.tvName.text = getString(
                                        R.string.txt_nama_guru_with_param,
                                        data.namaLengkap,
                                        data.gelar
                                    )
                                    profilCard.tvNisnOrNuptk.text =
                                        getString(
                                            R.string.txt_nuptk_with_param,
                                            data.nuptk
                                        )
                                    profilCard.profilePicture.loadProfilePicture(
                                        imageUrl = data.fotoProfil,
                                        placeholderRes = R.drawable.ic_avatar,
                                        imageResource = R.drawable.ic_avatar
                                    )
                                    profilCard.agama.text = data.agama
                                    if (resource.data?.jenisKelamin == "L") {
                                        profilCard.jenisKelamin.text = "Laki-Laki"
                                    } else {
                                        profilCard.jenisKelamin.text = "Perempuan"
                                    }
                                    profilCard.ttl.text = data.alamat
                                    guru = data
                                }
                            }
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

    override fun onResume() {
        super.onResume()
        getData()
    }
}