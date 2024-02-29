package tedc.alian.karyasiswa.presentation.fragment.tim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tedc.alian.data.remote.dto.PromosiDto
import tedc.alian.data.remote.dto.TimDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentProfilTimBinding
import tedc.alian.karyasiswa.presentation.activity.MainActivity
import tedc.alian.karyasiswa.presentation.activity.SettingsActivity
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.ListPromosiByTimAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.AuthViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.TimViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showDialog
import tedc.alian.utils.helper.showLogoutDialog
import tedc.alian.utils.helper.showToast
import tedc.alian.utils.helper.startActivity
import tedc.alian.utils.helper.startActivityAndFinish

@AndroidEntryPoint
class ProfilTimFragment : BaseFragment<FragmentProfilTimBinding>(),
    ListPromosiByTimAdapter.InterfaceListener {

    private val viewModel: TimViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var tim: TimDto? = null
    private val adapter: ListPromosiByTimAdapter by lazy {
        ListPromosiByTimAdapter(this)
    }
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfilTimBinding {
        return FragmentProfilTimBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        getData()
        uiAction()
        observer()
    }

    private fun uiSetup() {
        setupRecyclerView()
    }

    private fun getData() {
        viewModel.getProfil()
        viewModel.getListPromosiByTim()
    }

    private fun uiAction() {
        binding.profilCard.btnLogout.setOnClickListener {
            showKonfirmasiDialog()
        }
        binding.profilCard.btnEditProfile.setOnClickListener {
            val action =
                ProfilTimFragmentDirections.actionProfilTimFragmentToUbahProfilTimFragment(tim!!)
            findNavController().navigate(action)
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
            authViewModel.logoutTim()
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.profil.collect {
                    when (it) {
                        is Resource.Success -> {
                            with(binding) {
                                profilCard.tvName.text = it.data?.namaLengkap
                                profilCard.tvNisnOrNuptk.text = it.data?.jabatan
                                profilCard.profilePicture.load(R.drawable.ic_avatar) {
                                    transformations(CircleCropTransformation())
                                }
                            }
                            tim = it.data
                        }

                        else -> {}
                    }
                }

            }
            launch {
                authViewModel.onLogout.collectLatest {
                    if (it) {
                        progressDialog.dismiss()
                        startActivityAndFinish<MainActivity>()
                    }
                }
            }
            launch {
                viewModel.listPromosi.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> showShimmerLoading()
                        is Resource.Error -> {
                            hideShimmerLoading()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            hideShimmerLoading()
                            adapter.submitList(resource.data!!)
                        }
                    }
                }
            }
            launch {
                viewModel.hapusPromosi.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> progressDialog.show()
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            progressDialog.dismiss()
                            showToast(resource.data.toString())
                            viewModel.getListPromosiByTim()
                        }
                    }
                }
            }
        }
    }

    override fun onDelete(promosiDto: PromosiDto) {
        showDialog(
            title = "Hapus data promosi",
            message = "Apakah anda yakin akan menghapus data ini?",
            negativeButtonText = "Batal",
            positiveButtonText = "Hapus"
        ) {
            viewModel.hapusDataPromosi(promosiDto.id.toString())
        }
    }

    override fun onEdit(promosiDto: PromosiDto) {
        val action =
            ProfilTimFragmentDirections.actionProfilTimFragmentToUbahPromosiFragment(promosiDto)
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        binding.listPromosi.adapter = adapter
        binding.listPromosi.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showShimmerLoading() {
        binding.include.loading.isVisible = true
        binding.listPromosi.isVisible = false
    }

    private fun hideShimmerLoading() {
        binding.include.loading.isVisible = false
        binding.listPromosi.isVisible = true
    }
}