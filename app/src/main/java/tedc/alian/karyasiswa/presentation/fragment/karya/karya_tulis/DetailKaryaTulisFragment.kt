package tedc.alian.karyasiswa.presentation.fragment.karya.karya_tulis

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentDetailKaryaTulisBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.KomentarKaryaAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.loadProfilePicture
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class DetailKaryaTulisFragment : BaseFragment<FragmentDetailKaryaTulisBinding>() {

    private val viewModel: KaryaViewModel by viewModels()
    private val adapter: KomentarKaryaAdapter by lazy {
        KomentarKaryaAdapter()
    }
    private var karyaTulisId = ""

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailKaryaTulisBinding {
        return FragmentDetailKaryaTulisBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: return
        val args = DetailKaryaTulisFragmentArgs.fromBundle(bundle)
        viewModel.getDetailKaryaTulis(args.idKaryaTulis)
        observer()
    }

    private fun uiSetup(karyaTulis: KaryaTulisDto) {
        val komentar = karyaTulis.komentar
        adapter.submitList(komentar)
        binding.rvKomentarKaryaTulis.adapter = adapter
        binding.rvKomentarKaryaTulis.layoutManager = LinearLayoutManager(requireContext())
        binding.tvNamaSiswa.text = karyaTulis.siswa.namaLengkap
        binding.tvNisn.text = karyaTulis.siswa.nisn
        binding.chipKategori.text = karyaTulis.namaKategori
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvCaption.text =
                Html.fromHtml(karyaTulis.kontenKarya, Html.FROM_HTML_MODE_COMPACT)
        } else {
            binding.tvCaption.text = Html.fromHtml(karyaTulis.kontenKarya)
        }
        binding.btnLike.text =
            getString(R.string.txt_jumlah_like, karyaTulis.jumlahLike)
        binding.tvNamaKarya.text = karyaTulis.judulKarya
        binding.imgAvatar.loadProfilePicture(
            imageUrl = karyaTulis.siswa.fotoProfil,
            placeholderRes = R.drawable.ic_avatar,
            imageResource = R.drawable.ic_avatar
        )
    }

    private fun uiAction(karyaTulis: KaryaTulisDto) {
        binding.btnLike.setOnClickListener {
            viewModel.tambahLikeKaryaTulis(karyaTulis.id)
        }
        binding.btnKirim.setOnClickListener {
            onKirimButtonClicked(karyaTulis.id)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.tambahKomentar.collectLatest { resource ->
                    handleKomentarResource(resource)
                }
            }
            launch {
                viewModel.like.collectLatest { resource ->
                    handleLikeResource(resource)
                }
            }
            launch {
                viewModel.detailKaryaTulis.collectLatest { resource ->
                    handleDetailKaryaTulis(resource)
                }
            }
        }
    }

    private fun handleKomentarResource(resource: Resource<String?>) {
        when (resource) {
            is Resource.Loading -> {
                binding.btnKirim.isEnabled = false
                binding.inputKomentar.isEnabled = false
            }

            is Resource.Success -> {
                binding.btnKirim.isEnabled = true
                binding.inputKomentar.isEnabled = true
                binding.inputKomentar.editText?.setText("")
                viewModel.getDetailKaryaTulis(karyaTulisId)
            }

            is Resource.Error -> {
                showToast("Gagal menambahkan komentar")
            }
        }
    }

    private fun handleLikeResource(resource: Resource<String>) {
        when (resource) {
            is Resource.Loading -> binding.btnLike.isEnabled = false
            is Resource.Success -> {
                viewModel.getDetailKaryaTulis(karyaTulisId)
                binding.btnLike.isEnabled = true
            }

            is Resource.Error -> {
                binding.btnLike.isEnabled = true
                showToast(resource.throwable.message ?: "Gagal memberikan like")
            }
        }
    }

    private fun handleDetailKaryaTulis(resource: Resource<KaryaTulisDto?>) {
        when (resource) {
            is Resource.Loading -> showLoading()
            is Resource.Error -> {
                hideLoading()
                showToast(resource.throwable.message.toString())
            }

            is Resource.Success -> {
                hideLoading()
                val data = resource.data
                uiSetup(data!!)
                uiAction(data)
                karyaTulisId = resource.data?.id ?: ""
            }
        }
    }

    private fun onKirimButtonClicked(karyaId: String) {
        viewModel.tambahKomentarKaryaTulis(
            karyaId,
            binding.inputKomentar.editText?.text.toString()
        )
    }

    private fun showLoading() {
        with(binding) {
            include.loading.isVisible = true
            container.isVisible = false
        }
    }

    private fun hideLoading() {
        with(binding) {
            include.loading.isVisible = false
            container.isVisible = true
        }
    }
}