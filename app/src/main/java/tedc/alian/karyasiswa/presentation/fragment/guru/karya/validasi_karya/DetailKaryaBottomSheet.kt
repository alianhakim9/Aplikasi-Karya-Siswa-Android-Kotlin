package tedc.alian.karyasiswa.presentation.fragment.guru.karya.validasi_karya

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.BottomSheetDetailKaryaYangBelumDivalidasiBinding
import tedc.alian.utils.helper.showToast
import javax.inject.Inject

class DetailKaryaBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDetailKaryaYangBelumDivalidasiBinding? = null
    private val binding get() = _binding!!

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDetailKaryaYangBelumDivalidasiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
    }

    private fun uiSetup() {
        val bundle = arguments ?: return
        val args = DetailKaryaBottomSheetArgs.fromBundle(bundle)
        val karya = args.karyaCitra
        binding.namaKarya.text = karya.namaKarya
        binding.caption.text = karya.caption
        binding.kategori.text = karya.namaKategori
        binding.createdAt.text = karya.createdAt
        binding.caption.movementMethod = LinkMovementMethod.getInstance()
        if (karya.format != "mp4") {
            binding.karya.visibility = View.VISIBLE
            binding.karyaVideo.visibility = View.GONE
            binding.karya.load(args.karyaCitra.karya)
        } else {
            binding.karya.visibility = View.GONE
            binding.karyaVideo.visibility = View.VISIBLE
            binding.karyaVideo.player = player
            binding.karyaVideo.setShowBuffering(SHOW_BUFFERING_ALWAYS)
            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(Uri.parse(karya.karya)))
            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}