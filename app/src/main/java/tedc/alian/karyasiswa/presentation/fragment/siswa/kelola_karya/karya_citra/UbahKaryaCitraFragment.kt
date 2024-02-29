package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_citra

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okio.IOException
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.data.remote.api.karya.UbahKaryaCitraRequestRequest
import tedc.alian.karyasiswa.BuildConfig
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentUbahKaryaCitraBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.hideKeyboard
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class UbahKaryaCitraFragment : BaseFragment<FragmentUbahKaryaCitraBinding>() {

    private val viewModel: KaryaViewModel by viewModels()
    private val loadingProgressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.get_data_progress_dialog)
    }
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.save_progress_dialog)
    }
    private var currentPhotoPath: String = ""
    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(requireContext(), R.layout.item_kategori, namaKategori)
    }
    private var itemsKategori = mutableListOf<KategoriKaryaDto>()
    private var namaKategori = mutableListOf<String>()
    private var kategoriKaryaCitraId: String? = null
    private var karya: File? = null

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }

    companion object {
        private const val REQUEST_CODE = "REQUEST_CODE"
        private const val REQUEST_CODE_GALLERY = 100
        private const val REQUEST_CODE_CAMERA = 200
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUbahKaryaCitraBinding {
        return FragmentUbahKaryaCitraBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: return
        val args = UbahKaryaCitraFragmentArgs.fromBundle(bundle)
        val karyaCitra = args.karyaCitra
        getData(karyaCitra)
        uiSetup()
        uiAction(karyaCitra)
        observer()
    }

    private fun getData(karyaCitra: KaryaCitraDto) {
        viewModel.getKategoriKaryaCitra()
        if (karyaCitra.format != "mp4") {
            binding.karyaFoto.visibility = View.VISIBLE
            binding.karyaVideo.visibility = View.GONE
            binding.karyaFoto.load(karyaCitra.karya) {
                crossfade(true)
                transformations(RoundedCornersTransformation(20f))
            }
        } else {
            binding.karyaVideo.visibility = View.VISIBLE
            binding.karyaFoto.visibility = View.GONE
            binding.karyaVideo.player = player
            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(Uri.parse(karyaCitra.karya)))
            player.setMediaSource(mediaSource)
            binding.karyaVideo.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
        }
        binding.namaKarya.editText?.setText(karyaCitra.namaKarya)
        binding.caption.editText?.setText(karyaCitra.caption)
        viewModel.karyaCitraId.value = karyaCitra.id
        if (karyaCitra.status.lowercase() == "disetujui") {
            binding.tvKeterangan.text = "Karya sudah tervalidasi"
            binding.btnSelectKarya.isEnabled = false
        } else if (karyaCitra.status.lowercase() == "ditolak") {
            binding.tvKeterangan.text = "Karya ditolak, silahkan unggah kembali karya terbaru"
            binding.btnSelectKarya.isEnabled = false
            binding.btnUpload.isEnabled = false
        }
        (binding.kategoriKaryaCitra.editText as AutoCompleteTextView).setText(
            karyaCitra.namaKategori,
            false
        )
    }

    private fun uiSetup() {
        (binding.kategoriKaryaCitra.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
        val karyaTulisDropdown = binding.kategoriKaryaCitra.editText as AutoCompleteTextView
        karyaTulisDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                kategoriKaryaCitraId = itemsKategori[position].id.toString()
            }
    }

    private fun uiAction(karyaCitra: KaryaCitraDto) {
        binding.btnSelectKarya.setOnClickListener {
            val action =
                UbahKaryaCitraFragmentDirections.actionUbahKaryaCitraFragmentToPilihanMediaFragment(
                    true
                )
            findNavController().navigate(action)
        }
        binding.btnUpload.setOnClickListener {
            progressDialog.show()
            if (karya == null) {
                viewModel.ubahKaryaCitraTanpaKarya(
                    UbahKaryaCitraRequestRequest(
                        namaKarya = binding.namaKarya.editText?.text.toString(),
                        caption = binding.caption.editText?.text.toString(),
                        kategoriKaryaCitraId = kategoriKaryaCitraId
                            ?: karyaCitra.kategoriKaryaCitraId
                    )
                )
            } else {
                viewModel.ubahKaryaCitra(
                    UbahKaryaCitraRequestRequest(
                        karya = karya,
                        namaKarya = binding.namaKarya.editText?.text.toString(),
                        caption = binding.caption.editText?.text.toString(),
                        kategoriKaryaCitraId = kategoriKaryaCitraId
                            ?: karyaCitra.kategoriKaryaCitraId
                    )
                )
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uriPathHelper = tedc.alian.utils.helper.URIPathHelper()
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    val uri = data?.data
                    uri?.let { mediaUri ->
                        val filePath = uriPathHelper.getPath(requireContext(), data.data!!)
                        if (mediaUri.toString().contains("jpg") || mediaUri.toString()
                                .contains("jpeg") || mediaUri.toString()
                                .contains("png") || filePath.toString()
                                .contains("jpeg") || filePath.toString()
                                .contains("png") || filePath.toString().contains("jpg")
                        ) {
                            handleKaryaFotoAtauGambar(filePath!!, mediaUri)
                        } else if (mediaUri.toString().contains("mp4") || filePath.toString()
                                .contains("mp4")
                        ) {
                            val duration = getVideoDuration(filePath!!)
                            if (duration / 1000 > 60) {
                                showToast("Durasi karya video maksimal 1 menit")
                            } else {
                                compressVideo(mediaUri)
                            }
                        } else {
                            showToast("Format karya tidak di dukung :(")
                        }
                    }
                }

                REQUEST_CODE_CAMERA -> {
                    val uri = Uri.parse(currentPhotoPath)
                    previewKarya(uri)
                    karya = File(currentPhotoPath)
                }
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun openGaleri() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*, video/*"
        intent.putExtra(
            REQUEST_CODE,
            REQUEST_CODE_GALLERY
        )
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    private fun openKamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createCapturedPhoto()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        Objects.requireNonNull(requireContext()),
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )
                    intent.putExtra(
                        REQUEST_CODE,
                        REQUEST_CODE_CAMERA
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, REQUEST_CODE_CAMERA)
                }
            }
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.kategoriKaryaCitra.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> loadingProgressDialog.show()
                        is Resource.Error -> {
                            loadingProgressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                            hideKeyboard()
                        }

                        is Resource.Success -> {
                            loadingProgressDialog.dismiss()
                            resource.data.forEach { kategoriKarya ->
                                if (kategoriKarya != null) {
                                    namaKategori.add(kategoriKarya.namaKategori)
                                    itemsKategori.add(kategoriKarya)
                                }
                            }
                        }
                    }
                }
            }
            launch {
                viewModel.ubahKaryaCitra.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> progressDialog.show()
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            progressDialog.dismiss()
                            showToast(resource.data)
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun <T> previewKarya(karya: T) {
        binding.karyaVideo.isVisible = false
        binding.karyaFoto.isVisible = true
        binding.karyaFoto.load(karya) {
            crossfade(true)
            transformations(RoundedCornersTransformation(20f))
        }
    }

    fun getKaryaGaleriContract() = checkPermissionGallery()
    fun getKaryaKameraContract() = cekIzinKamera()

    private fun cekIzinKamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_CAMERA
            )
        } else {
            openKamera()
        }
    }

    @Throws(IOException::class)
    private fun createCapturedPhoto(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("karya_citra_${timestamp}", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun handleKaryaVideo(filePath: String) {
        val mediaSource =
            ProgressiveMediaSource.Factory(FileDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(Uri.parse(filePath)))
        binding.karyaFoto.isVisible = false
        binding.karyaVideo.isVisible = true
        binding.karyaVideo.player = player
        binding.karyaVideo.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        player.setMediaSource(mediaSource)
        player.playWhenReady = true
        player.prepare()
        player.play()
        karya = File(filePath)
    }

    private fun handleKaryaFotoAtauGambar(filePath: String, mediaUri: Uri) {
        previewKarya(mediaUri)
        filePath.let { path -> karya = File(path) }
    }

    private fun getVideoDuration(filePath: String): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)
        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val duration = durationString?.toLongOrNull() ?: 0L
        retriever.release()
        return duration
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
    }

    private fun checkPermissionGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGaleri()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGaleri()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun compressVideo(uri: Uri) {
        val uris = listOf(uri)
        VideoCompressor.start(
            context = requireContext().applicationContext,
            uris = uris,
            isStreamable = false,
            appSpecificStorageConfiguration = AppSpecificStorageConfiguration(
                videoName = "compressed_video",
            ),
            configureWith = Configuration(
                quality = VideoQuality.LOW,
                isMinBitrateCheckEnabled = true,
                videoBitrateInMbps = 1,
                disableAudio = false,
                keepOriginalResolution = true
            ),
            listener = object : CompressionListener {
                override fun onCancelled(index: Int) {
                }

                override fun onFailure(index: Int, failureMessage: String) {

                }

                override fun onProgress(index: Int, percent: Float) {
                    requireActivity().runOnUiThread {
                        binding.tvKeterangan.text =
                            "Harap tunggu Sedang meng-compress video : ${percent.toInt()} %"
                        binding.karyaFoto.load(R.drawable.ic_timer)
                    }
                }

                override fun onStart(index: Int) {
                    binding.karyaFoto.load(R.drawable.ic_timer)
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    handleKaryaVideo(path!!)
                    binding.tvKeterangan.text =
                        "* ukuran karya maksimal 10 MB\n* format yang diizinkan: .jpg, .png, .jpeg, dan .mp4\\n* durasi karya video maks.1 menit"
                }
            }
        )
    }
}