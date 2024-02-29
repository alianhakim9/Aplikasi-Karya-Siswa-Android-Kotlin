package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_citra

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.databinding.FragmentDetailKaryaDitolakBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment

@AndroidEntryPoint
class DetailKaryaDitolakFragment : BaseFragment<FragmentDetailKaryaDitolakBinding>() {

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailKaryaDitolakBinding {
        return FragmentDetailKaryaDitolakBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        uiAction()
    }

    private fun uiSetup() {
        val bundle = arguments ?: return
        val args = DetailKaryaDitolakFragmentArgs.fromBundle(bundle)
        val karyaCitra = args.karyaCitra
        if (karyaCitra.format == "mp4" || karyaCitra.format == "avi") {
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
            binding.karya.visibility = View.VISIBLE
            binding.karya.load(karyaCitra.karya) {
                crossfade(50)
            }
        }
        binding.tvNamaKarya.text = karyaCitra.namaKarya
        binding.tvCaption.text = karyaCitra.caption
        binding.tvAlasanDitolak.text = karyaCitra.statusKarya.keterangan
        binding.namaKategori.text = karyaCitra.namaKategori
    }

    private fun uiAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}