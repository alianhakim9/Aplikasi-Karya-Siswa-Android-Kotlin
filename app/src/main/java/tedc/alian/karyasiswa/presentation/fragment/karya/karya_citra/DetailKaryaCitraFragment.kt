package tedc.alian.karyasiswa.presentation.fragment.karya.karya_citra

import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentDetailKaryaCitraBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.KomentarKaryaAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class DetailKaryaCitraFragment : BaseFragment<FragmentDetailKaryaCitraBinding>() {

    private val viewModel: KaryaViewModel by viewModels()
    private val adapter: KomentarKaryaAdapter by lazy {
        KomentarKaryaAdapter()
    }
    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }
    private var karyaCitraId = ""

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailKaryaCitraBinding {
        return FragmentDetailKaryaCitraBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        getData()
        uiAction()
    }

    private fun getData() {
        val bundle = arguments ?: return
        val args = DetailKaryaCitraFragmentArgs.fromBundle(bundle)
        viewModel.getDetailKaryaCitra(args.idKaryaCitra)
    }

    private fun uiAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupViews(karyaCitra: KaryaCitraDto) {
        val komentar = karyaCitra.komentar
        adapter.submitList(komentar ?: emptyList())
        binding.rvKomentarKaryaCitra.adapter = adapter
        binding.rvKomentarKaryaCitra.layoutManager = LinearLayoutManager(requireContext())
        binding.tvNamaSiswa.text = karyaCitra.siswa.namaLengkap
        binding.tvNisn.text = karyaCitra.siswa.nisn
        binding.tvCaption.text = karyaCitra.caption
        binding.btnLike.text = getString(R.string.txt_jumlah_like, karyaCitra.jumlahLike)
        binding.tvNamaKarya.text = karyaCitra.namaKarya
        binding.chipKategori.text = karyaCitra.namaKategori
        binding.imgAvatar.load(karyaCitra.siswa.fotoProfil) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        val format = karyaCitra.format
        if (format == "mp4") {
            binding.karyaVideo.visibility = View.VISIBLE
            binding.karya.visibility = View.GONE
            binding.karyaVideo.player = player
            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(Uri.parse(karyaCitra.karya)))
            player.setMediaSource(mediaSource)
            binding.karyaVideo.setShowBuffering(SHOW_BUFFERING_ALWAYS)
            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
        } else {
            binding.karyaVideo.visibility = View.GONE
            binding.karya.load(karyaCitra.karya) {
                crossfade(true)
                transformations(RoundedCornersTransformation(20f))
            }
        }
        when (karyaCitra.status) {
            getString(R.string.txt_ditolak) -> {
                binding.tvIsValided.text = getString(R.string.txt_karya_ini_ditolak)
                binding.imgVerified2.load(R.drawable.ic_not_validated)
            }

            getString(R.string.txt_menunggu_validasi) -> {
                binding.tvIsValided.text = getString(R.string.txt_menunggu_validasi)
                binding.imgVerified2.load(R.drawable.ic_menunggu_validasi)
            }
        }
        binding.btnKirim.setOnClickListener {
            onKirimButtonClicked(karyaCitra.id)
        }

        binding.btnLike.setOnClickListener {
            onLikeImageClicked(karyaCitra.id)
        }
        binding.tvCaption.movementMethod = LinkMovementMethod.getInstance()
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
                viewModel.detailKaryaCitra.collectLatest { resource ->
                    handleGetDetailKaryaCitra(resource)
                }
            }
        }
    }

    private fun onKirimButtonClicked(karyaId: String) {
        viewModel.tambahKomentarKaryaCitra(
            karyaId,
            binding.inputKomentar.editText?.text.toString()
        )
    }

    private fun onLikeImageClicked(karyaCitraId: String) {
        viewModel.tambahLikeKaryaCitra(karyaCitraId)
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
                viewModel.getDetailKaryaCitra(karyaCitraId)
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
                viewModel.getDetailKaryaCitra(karyaCitraId)
                binding.btnLike.isEnabled = true
            }

            is Resource.Error -> {
                binding.btnLike.isEnabled = true
                Log.e(
                    DetailKaryaCitraFragment::class.simpleName,
                    "handleLikeResource: ${resource.throwable.message}",
                )
                showToast(resource.throwable.message ?: "Gagal memberikan like")
            }
        }
    }

    private fun handleGetDetailKaryaCitra(resource: Resource<KaryaCitraDto?>) {
        when (resource) {
            is Resource.Loading -> showLoading()
            is Resource.Error -> {
                hideLoading()
                showToast(resource.throwable.message.toString())
            }

            is Resource.Success -> {
                hideLoading()
                setupViews(resource.data!!)
                karyaCitraId = resource.data?.id ?: ""
            }
        }
    }

    private fun showLoading() {
        binding.include.loading.isVisible = true
        binding.container.isVisible = false
    }

    private fun hideLoading() {
        binding.include.loading.isVisible = false
        binding.container.isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}