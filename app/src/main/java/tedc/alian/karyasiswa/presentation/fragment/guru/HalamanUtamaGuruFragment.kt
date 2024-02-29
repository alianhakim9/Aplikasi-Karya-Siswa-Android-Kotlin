package tedc.alian.karyasiswa.presentation.fragment.guru

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentHalamanUtamaGuruBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.KaryaCitraHalamanUtamaAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.GuruViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createNotificationChannel
import tedc.alian.utils.helper.loadProfilePicture
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.setGreeting
import tedc.alian.utils.helper.showNotification
import tedc.alian.utils.helper.showToast

@ExperimentalBadgeUtils
@AndroidEntryPoint
class HalamanUtamaGuruFragment :
    BaseFragment<FragmentHalamanUtamaGuruBinding>(),
    KaryaCitraHalamanUtamaAdapter.InterfaceListener {

    private val viewModel: GuruViewModel by viewModels()
    private val karyaViewModel: KaryaViewModel by viewModels()
    private val adapter: KaryaCitraHalamanUtamaAdapter by lazy {
        KaryaCitraHalamanUtamaAdapter(this)
    }
    private lateinit var badgeDrawable: BadgeDrawable

    companion object {
        const val CHANNEL_ID = "channel_id_guru"
        const val NOTIFICATION_ID = 101
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHalamanUtamaGuruBinding {
        return FragmentHalamanUtamaGuruBinding.inflate(
            layoutInflater
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        uiSetup()
        uiAction()
        observer()
    }

    private fun getData() {
        viewModel.getProfil()
        karyaViewModel.getAllKaryaCitra()
        karyaViewModel.getNotifikasi()
    }

    private fun uiSetup() {
        binding.rvKaryaTerbaru.adapter = adapter
        binding.rvKaryaTerbaru.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        badgeDrawable = BadgeDrawable.create(requireContext())
        setGreeting(binding.tvSapaan)
        createNotificationChannel(CHANNEL_ID)
    }

    private fun uiAction() {
        binding.cvKaryaCitra.setOnClickListener {
            findNavController().navigate(R.id.action_guruHomeFragment_to_listKaryaCitraFragmentGuru)
        }
        binding.cvKaryaTulis.setOnClickListener {
            findNavController().navigate(R.id.action_guruHomeFragment_to_listKaryaTulisFragmentGuru)
        }
        binding.btnDetail.setOnClickListener {
            findNavController().navigate(R.id.action_guruHomeFragment_to_listWaitingValidasiKaryaFragment)
        }
        binding.toolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.notification -> {
                        findNavController().navigate(R.id.action_guruHomeFragment_to_listNotifikasiFragment)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                karyaViewModel.karya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoader()
                            binding.tvErrorMsg.visibility = View.GONE
                            binding.rvKaryaTerbaru.visibility = View.GONE
                        }

                        is Resource.Error -> {
                            hideLoader()
                            binding.tvErrorMsg.visibility = View.VISIBLE
                            binding.rvKaryaTerbaru.visibility = View.GONE
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            hideLoader()
                            binding.tvErrorMsg.isVisible = resource.data.isEmpty()
                            adapter.submitList(resource.data)
                        }
                    }
                }
            }
            launch {
                viewModel.profil.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoadingProfil()
                        is Resource.Error -> {
                            hideLoadingProfil()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            val data = resource.data
                            hideLoadingProfil()
                            with(binding) {
                                tvNama.text = getString(
                                    R.string.txt_nama_guru,
                                    data?.namaLengkap,
                                    data?.gelar
                                )
                                fotoProfil.loadProfilePicture(
                                    imageUrl = data?.fotoProfil,
                                    placeholderRes = R.drawable.ic_avatar,
                                    imageResource = R.drawable.ic_avatar
                                )
                            }
                        }
                    }
                }
            }
            launch {
                karyaViewModel.notifikasi.collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val data = resource.data
                            if (data != null) {
                                if (data.count != 0) {
                                    showNotification(
                                        "Validasi karya",
                                        "Karya ini sedang menunggu di validasi",
                                        CHANNEL_ID,
                                        NOTIFICATION_ID,
                                        R.drawable.img_logo_128
                                    )
//                                    showToastNotification(R.layout.notif_toast)
                                }
                                badgeDrawable.apply {
                                    isVisible = data.count != 0
                                    number = data.count
                                    backgroundColor =
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.secondary_400
                                        )
                                    horizontalOffset = 16
                                }
                                BadgeUtils.attachBadgeDrawable(
                                    badgeDrawable,
                                    binding.toolbar,
                                    R.id.notification
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }


    private fun showLoadingProfil() {
        with(binding) {
            include.loading.isVisible = true
            container.isVisible = false
        }
    }

    private fun hideLoadingProfil() {
        with(binding) {
            include.loading.isVisible = false
            container.isVisible = true
        }
    }

    private fun showLoader() {
        binding.includeKarya.loading.isVisible = true
        binding.rvKaryaTerbaru.isVisible = false
    }

    private fun hideLoader() {
        binding.includeKarya.loading.isVisible = false
        binding.rvKaryaTerbaru.isVisible = true
    }

    override fun onItemClick(karyaCitraDto: KaryaCitraDto) {
        val action =
            HalamanUtamaGuruFragmentDirections.actionGuruHomeFragmentToDetailKaryaCitraFragment2(
                karyaCitraDto.id
            )
        findNavController().navigate(action)
    }
}