package tedc.alian.karyasiswa.presentation.fragment.tim

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.data.remote.api.tim.TambahPromosiRequest
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentTambahPromosiBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.BaseUiStateViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.TimViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.hideKeyboard
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast
import java.io.File
import java.text.DateFormat
import java.util.*

@AndroidEntryPoint
class TambahPromosiFragment : BaseFragment<FragmentTambahPromosiBinding>() {
    private var itemsStatus = mutableListOf("Aktif", "Tidak Aktif")
    private var status = ""
    private val viewModel: TimViewModel by viewModels()
    private val uiViewModel: BaseUiStateViewModel by viewModels()
    private var gambarPromosi: File? = null
    private var tglPromosi = ""
    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.item_kategori,
            itemsStatus
        )
    }
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.upload_promosi_progress_dialog)
    }

    companion object {
        private const val REQUEST_CODE_GALLERY = 100
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTambahPromosiBinding {
        return FragmentTambahPromosiBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        uiAction()
        observer()
    }

    private fun uiSetup() {
        if (uiViewModel.isProgressDialogVisible()) progressDialog.show()
    }

    private fun uiAction() {
        (binding.statusPromosi.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
        val statusPromosi = binding.statusPromosi.editText as AutoCompleteTextView
        statusPromosi.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                status = itemsStatus[position]
            }
        val edtDatePicker = binding.inputTanggalPromosi.editText
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
                tglPromosi = formattedDate
            }
            datePicker.show(childFragmentManager, "tag")
        }
        binding.btnPilihGambar.setOnClickListener {
            openGaleri()
        }
        binding.btnUpload.setOnClickListener {
            viewModel.tambahPromosi(
                TambahPromosiRequest(
                    namaPromosi = binding.namaPromosi.editText?.text.toString(),
                    gambar = gambarPromosi,
                    keterangan = binding.keterangan.editText?.text.toString(),
                    status = status,
                    tanggalPromosi = tglPromosi
                )
            )
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            viewModel.tambahPromosi.collect { resource ->
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
                        showToast(resource.data)
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uriPathHelper = tedc.alian.utils.helper.URIPathHelper()
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    val uri = data?.data
                    if (data?.data != null) {
                        val filePath = uriPathHelper.getPath(requireContext(), data.data!!)
                        previewGambar(uri)
                        gambarPromosi = filePath?.let { path -> File(path) }
                    }
                }
            }
        }
    }


    private fun openGaleri() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_GALLERY
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        }
    }

    private fun <T> previewGambar(karya: T) {
        binding.karyaFoto.load(karya) {
            crossfade(true)
            transformations(RoundedCornersTransformation(20f))
        }
    }
}