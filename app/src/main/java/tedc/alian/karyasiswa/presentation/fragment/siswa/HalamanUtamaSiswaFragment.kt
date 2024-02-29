package tedc.alian.karyasiswa.presentation.fragment.siswa

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
import tedc.alian.karyasiswa.databinding.FragmentHalamanUtamaSiswaBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.KaryaCitraHalamanUtamaAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.SiswaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createNotificationChannel
import tedc.alian.utils.helper.loadProfilePicture
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.setGreeting
import tedc.alian.utils.helper.showNotification
import tedc.alian.utils.helper.showToast

@ExperimentalBadgeUtils
@AndroidEntryPoint
class HalamanUtamaSiswaFragment : BaseFragment<FragmentHalamanUtamaSiswaBinding>(),
    KaryaCitraHalamanUtamaAdapter.InterfaceListener {

    private val viewModel: SiswaViewModel by viewModels()
    private val karyaViewModel: KaryaViewModel by viewModels()
    private val adapter: KaryaCitraHalamanUtamaAdapter by lazy {
        KaryaCitraHalamanUtamaAdapter(this)
    }
    private val badgeDrawable: BadgeDrawable by lazy {
        BadgeDrawable.create(requireContext())
    }

    companion object {
        const val CHANNEL_ID = "channel_id_siswa"
        const val NOTIFICATION_ID = 101
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHalamanUtamaSiswaBinding {
        return FragmentHalamanUtamaSiswaBinding.inflate(layoutInflater)
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
        karyaViewModel.getNotifikasiSiswa()
    }

    private fun uiSetup() {
        binding.rvKaryaTerbaru.adapter = adapter
        binding.rvKaryaTerbaru.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        setGreeting(binding.tvGreeting2)
        createNotificationChannel(CHANNEL_ID)
    }

    private fun uiAction() {
        binding.cvKaryaCitra.setOnClickListener {
            findNavController().navigate(R.id.action_homeSiswaFragment_to_listKaryaCitraFragment)
        }
        binding.cvKaryaTulis.setOnClickListener {
            findNavController().navigate(R.id.action_homeSiswaFragment_to_listKaryaTulisFragment)
        }
        binding.toolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.notification -> {
                        findNavController().navigate(R.id.action_homeSiswaFragment_to_listNotifikasiSiswaFragment)
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
                        }

                        is Resource.Error -> {
                            hideLoader()
                            binding.tvErrorMsg.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            hideLoader()
                            adapter.submitList(resource.data)
                            binding.tvErrorMsg.isVisible = resource.data.isEmpty()
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
                                tvGreeting.text =
                                    getString(R.string.txt_greeting, data?.namaLengkap)
                                profilePicture.loadProfilePicture(
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
                                        "Karya divalidasi",
                                        "Selamat karya berhasil divalidasi",
                                        CHANNEL_ID,
                                        NOTIFICATION_ID,
                                        R.drawable.img_logo_128
                                    )
                                    //showToastNotification(R.layout.notif_toast)
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
            include.loading.visibility = View.VISIBLE
            container.visibility = View.GONE
        }
    }

    private fun hideLoadingProfil() {
        with(binding) {
            include.loading.visibility = View.GONE
            container.visibility = View.VISIBLE
        }
    }

    private fun showLoader() {
        with(binding.loader) {
            visibility = View.VISIBLE
        }
    }

    private fun hideLoader() {
        with(binding.loader) {
            visibility = View.GONE
        }
    }

    override fun onItemClick(karyaCitraDto: KaryaCitraDto) {
        val action =
            HalamanUtamaSiswaFragmentDirections.actionHomeSiswaFragmentToDetailKaryaCitraFragment(
                karyaCitraDto.id
            )
        findNavController().navigate(action)
    }
}