package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_citra

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.google.android.exoplayer2.upstream.FileDataSource
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okio.IOException
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.data.remote.api.karya.TambahKaryaCitraRequest
import tedc.alian.karyasiswa.BuildConfig
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentTambahKaryaCitraBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.BaseUiStateViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class TambahKaryaCitraFragment :
    BaseFragment<FragmentTambahKaryaCitraBinding>() {
    private val loadingProgressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.get_data_progress_dialog)
    }
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.upload_karya_progress_dialog)
    }
    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.item_kategori,
            namaKategori
        )
    }
    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }
    private val viewModel: KaryaViewModel by viewModels()
    private val uiViewModel: BaseUiStateViewModel by viewModels()
    private var namaKategori = mutableListOf<String>()
    private var items = mutableListOf<KategoriKaryaDto>()
    private var kategoriKaryaCitraId: String? = null
    private var karya: File? = null
    private var currentPhotoPath: String = ""

    companion object {
        private const val REQUEST_CODE = "REQUEST_CODE"
        private const val REQUEST_CODE_GALLERY = 100
        private const val REQUEST_CODE_CAMERA = 200
        private const val PERMISSION_REQUEST_CODE = 123
        private val TAG = TambahKaryaCitraFragment::class.simpleName
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTambahKaryaCitraBinding {
        return FragmentTambahKaryaCitraBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        uiAction()
        observer()
    }

    private fun uiSetup() {
        if (uiViewModel.isProgressDialogVisible()) {
            progressDialog.show()
        }
        binding.btnSelectKarya.setOnClickListener {
            findNavController().navigate(R.id.action_tambahKaryaCitraFragment_to_pilihanMediaKaryaBottomSheet)
        }
        (binding.dropdownKaryaCitra.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
        viewModel.getKategoriKaryaCitra()
        val kategoriKaryaCitra = binding.dropdownKaryaCitra.editText as AutoCompleteTextView
        kategoriKaryaCitra.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                kategoriKaryaCitraId = items[position].id.toString()
            }
    }

    private fun uiAction() {
        binding.btnUpload.setOnClickListener {
            viewModel.tambahKaryaCitra(
                TambahKaryaCitraRequest(
                    namaKarya = binding.namaKarya.editText?.text.toString(),
                    karya = karya,
                    caption = binding.caption.editText?.text.toString(),
                    kategoriKaryaCitraId = kategoriKaryaCitraId
                )
            )
        }
        binding.toolbar.setNavigationOnClickListener {
            showDialogBack()
        }
        onBackPressedKarya {
            showDialogBack()
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.kategoriKaryaCitra.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoadingForKategori()
                        is Resource.Error -> {
                            hideLoadingForKategori()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            loadingProgressDialog.dismiss()
                            resource.data.forEach { kategoriKarya ->
                                Log.d(TAG, "observer: $kategoriKarya")
                                if (kategoriKarya != null) {
                                    items.add(kategoriKarya)
                                    namaKategori.add(kategoriKarya.namaKategori)
                                }
                            }
                        }
                    }
                }
            }
            launch {
                viewModel.uploadKarya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            progressDialog.show()
                            uiViewModel.setProgressDialogVisibility(progressDialog.isShowing)
                        }

                        is Resource.Error -> {
                            progressDialog.dismiss()
                            uiViewModel.setProgressDialogVisibility(progressDialog.isShowing)
                            showToast(resource.throwable.message.toString())
                            hideKeyboard()
                        }

                        is Resource.Success -> {
                            progressDialog.dismiss()
                            uiViewModel.setProgressDialogVisibility(progressDialog.isShowing)
                            showToast("Karya berhasil diunggah")
                            val action =
                                TambahKaryaCitraFragmentDirections.actionTambahKaryaCitraFragmentToDetailKaryaCitraFragment(
                                    resource.data?.id ?: ""
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uriPathHelper = URIPathHelper()
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
                            namaKategori.clear()
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

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    result.uri?.let { uri ->
                        val filePath = uri.path
                        handleKaryaFotoAtauGambar(filePath!!, uri)
                        karya = uri.path?.let { path -> File(path) }
                    }
                }
            }
        }
    }

    private fun openGaleri() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*, video/*"
        intent.putExtra(REQUEST_CODE, REQUEST_CODE_GALLERY)
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
                    intent.putExtra(REQUEST_CODE, REQUEST_CODE_CAMERA)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, REQUEST_CODE_CAMERA)
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

    @Throws(IOException::class)
    private fun createCapturedPhoto(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("karya_citra_${timestamp}", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }


    private fun hideLoadingForKategori() {
        loadingProgressDialog.dismiss()
    }

    private fun showLoadingForKategori() {
        loadingProgressDialog.show()
    }

    private fun handleKaryaVideo(filePath: String) {
        val mediaSource =
            ProgressiveMediaSource.Factory(FileDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(Uri.parse(filePath)))
        binding.karyaFoto.isVisible = false
        binding.karyaVideo.isVisible = true
        binding.karyaVideo.player = player
        binding.karyaVideo.setShowBuffering(SHOW_BUFFERING_ALWAYS)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGaleri()
            } else {
                showToast("akses galeri ditolak")
            }
        }
    }

    fun getKaryaGaleriContract() = checkPermissionGallery()
    fun getKaryaKameraContract() = openKamera()

    private fun compressVideo(uri: Uri) {
        val uris = listOf(uri)
        VideoCompressor.start(
            context = requireContext().applicationContext,
            uris = uris,
            isStreamable = false,
            appSpecificStorageConfiguration = AppSpecificStorageConfiguration(
                videoName = "compressed_video", // => required name
            ),
            configureWith = Configuration(
                quality = VideoQuality.LOW,
                isMinBitrateCheckEnabled = false,
                videoBitrateInMbps = 1,
                disableAudio = false,
                keepOriginalResolution = true
            ),
            listener = object : CompressionListener {
                override fun onCancelled(index: Int) {
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    binding.tvKeterangan.text = "Gagal melakukan compress video"
                    Log.d(TAG, "onFailure: $failureMessage")
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
                        "* ukuran karya maksimal 10 MB\n* format yang diizinkan: .jpg, .png, .jpeg, dan .mp4\n* durasi karya video maks.1 menit"
                }
            }
        )
    }

    private fun showDialogBack(

    ) {
        showDialogBack(
            condition = karya != null,
            onPositiveButton = {
                findNavController().navigateUp()
            },
            defaultBackAction = {
                findNavController().navigateUp()
            }
        )
    }
}