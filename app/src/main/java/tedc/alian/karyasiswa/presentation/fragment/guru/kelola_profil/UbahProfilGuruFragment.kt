package tedc.alian.karyasiswa.presentation.fragment.guru.kelola_profil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.datepicker.MaterialDatePicker
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.guru.EditProfilGuruRequest
import tedc.alian.data.remote.api.guru.GuruDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentUbahProfilGuruBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.GuruViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.hideKeyboard
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast
import tedc.alian.utils.helper.startCropActivity
import java.io.File
import java.text.DateFormat
import java.util.Calendar

@AndroidEntryPoint
class UbahProfilGuruFragment : BaseFragment<FragmentUbahProfilGuruBinding>() {

    private val viewModel: GuruViewModel by viewModels()
    private var fotoProfilBaru: File? = null
    private var tglLahir = ""
    private var jenisKelamin = ""
    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.item_kategori,
            itemsStatus
        )
    }
    private var itemsStatus = mutableListOf("P", "L")

    companion object {
        private const val GALLERY_REQUEST_CODE = 1234
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUbahProfilGuruBinding {
        return FragmentUbahProfilGuruBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: return
        val args = UbahProfilGuruFragmentArgs.fromBundle(bundle)
        val guru = args.guru
        getData(guru)
        uiSetup()
        uiAction(guru)
        observer()
    }

    private fun getData(guru: GuruDto) {
        binding.fotoProfil.load(guru.fotoProfil) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        binding.inputJabatan.editText?.setText(guru.jabatan)
        binding.inputGelar.editText?.setText(guru.gelar)
        binding.inputNamaLengkap.editText?.setText(guru.namaLengkap)
        binding.inputNuptk.editText?.setText(guru.nuptk)
        binding.inputTempatLahir.editText?.setText(guru.alamat)
        binding.inputTanggalLahir.editText?.setText(guru.ttl)
        binding.inputAgama.editText?.setText(guru.agama)
        (binding.inputJenisKelamin.editText as AutoCompleteTextView).setText(
            guru.jenisKelamin,
            false
        )
        binding.fotoProfil.load(guru.fotoProfil) {
            placeholder(R.drawable.ic_avatar)
            transformations(CircleCropTransformation())
        }
    }

    private fun uiSetup() {
        (binding.inputJenisKelamin.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
        val gender = binding.inputJenisKelamin.editText as AutoCompleteTextView
        gender.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                jenisKelamin = itemsStatus[position]
            }
    }

    private fun uiAction(guru: GuruDto) {
        binding.btnSimpan.setOnClickListener {
            if (fotoProfilBaru == null) {
                viewModel.editProfilTanpaFoto(
                    ubahProfilGuruRequest(guru)
                )
            } else {
                viewModel.editProfil(
                    ubahProfilGuruRequest(guru, fotoProfilBaru)
                )
            }
        }
        binding.tvPilihFotoProfil.setOnClickListener {
            pickFromGallery()
        }
        binding.fotoProfil.setOnClickListener {
            pickFromGallery()
        }

        val edtDatePicker = binding.inputTanggalLahir.editText
        edtDatePicker?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it
                val date = DateFormat.getDateInstance().format(calendar.time)
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val formattedDate =
                    "$year-${month.toString().padStart(2, '0')}-${
                        day.toString().padStart(2, '0')
                    }"
                edtDatePicker.setText(date)
                tglLahir = formattedDate
            }
            datePicker.show(childFragmentManager, "tag")
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.editProfil.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressCircular.isVisible = true
                            binding.btnSimpan.isEnabled = false
                        }

                        is Resource.Error -> {
                            binding.progressCircular.isVisible = false
                            binding.btnSimpan.isEnabled = true
                            showToast(resource.throwable.message.toString())
                            hideKeyboard()
                        }

                        is Resource.Success -> {
                            binding.progressCircular.isVisible = false
                            binding.btnSimpan.isEnabled = true
                            showToast("Profil berhasil disimpan")
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        startCropActivity(uri)
                    }
                } else {
                    showToast("Gagal mengambil foto profil")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    result.uri?.let {
                        setImage(it)
                        fotoProfilBaru = it.path?.let { it1 -> File(it1) }
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    showToast("Gagal mengambil foto profil")
                }
            }
        }
    }

    private fun ubahProfilGuruRequest(
        guru: GuruDto,
        fotoProfil: File? = null
    ): EditProfilGuruRequest {
        return EditProfilGuruRequest(
            id = guru.id,
            namaLengkap = binding.inputNamaLengkap.editText?.text.toString(),
            nuptk = binding.inputNuptk.editText?.text.toString(),
            jenisKelamin = jenisKelamin.ifEmpty { guru.jenisKelamin },
            alamat = binding.inputTempatLahir.editText?.text.toString(),
            ttl = tglLahir.ifEmpty { binding.inputTanggalLahir.editText?.text.toString() },
            agama = binding.inputAgama.editText?.text.toString(),
            jabatan = binding.inputJabatan.editText?.text.toString(),
            gelar = binding.inputGelar.editText?.text.toString(),
            fotoProfil = fotoProfil
        )
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpg", "image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun setImage(uri: Uri?) {
        binding.fotoProfil.load(uri) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }
}