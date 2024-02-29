package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_tulis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tedc.alian.data.local.model.KaryaTulis
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.data.remote.api.karya.UploadKaryaTulisRequest
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentTambahKaryaTulisBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.BaseUiStateViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.hideKeyboard
import tedc.alian.utils.helper.onBackPressedKarya
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showDialogBack
import tedc.alian.utils.helper.showToast


@AndroidEntryPoint
class TambahKaryaTulisFragment : BaseFragment<FragmentTambahKaryaTulisBinding>() {

    private val viewModel: KaryaViewModel by viewModels()
    private val uiViewModel: BaseUiStateViewModel by viewModels()
    private var namaKategori = mutableListOf<String>()
    private var items = mutableListOf<KategoriKaryaDto>()
    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(requireContext(), R.layout.item_kategori, namaKategori)
    }
    private val loadingProgressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.get_data_progress_dialog)
    }
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.upload_karya_progress_dialog)
    }
    private var kategoriKaryaTulisId: String = ""
    private var kontenKarya: String = ""
    private val options = listOf("Simpan sebagai draft", "Unggah karya tulis")

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTambahKaryaTulisBinding {
        return FragmentTambahKaryaTulisBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        uiAction()
        observer()
    }

    private fun uiSetup() {
        (binding.include.dropdownKaryaTulis.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
        viewModel.getKategoriKaryaTulis()
        viewModel.getKaryaTulisDraft()
        val karyaTulisDropdown = binding.include.dropdownKaryaTulis.editText as AutoCompleteTextView
        karyaTulisDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                kategoriKaryaTulisId = items[position].id.toString()
            }
        if (uiViewModel.isProgressDialogVisible()) progressDialog.show()
        setupTextEditor()
    }

    private fun uiAction() {
        binding.include.editor.setOnTextChangeListener { text ->
            kontenKarya = text
        }
        binding.btnDone.setOnClickListener {
            setupOptionAlertDialog(options)
        }
        binding.toolbar.setNavigationOnClickListener {
            showDialogBack()
        }
        onBackPressed()
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.kategoriKaryaTulis.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> loadingProgressDialog.show()
                        is Resource.Error -> {
                            loadingProgressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            loadingProgressDialog.dismiss()
                            resource.data.forEach { kategoriKarya ->
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
                viewModel.uploadKaryaTulis.collect { resource ->
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
                            val action =
                                TambahKaryaTulisFragmentDirections.actionTambahKaryaTulisFragmentToDetailKaryaTulisFragment(
                                    resource.data?.id!!
                                )
                            viewModel.deleteKaryaTulisDraft()
                            findNavController().navigate(action)
                        }
                    }
                }
            }
            launch {
                viewModel.draftKaryaTulis.collect { value ->
                    kontenKarya = value.kontenKarya
                    binding.include.inputJudulKarya.editText?.setText(value.judulKarya)
                    binding.include.editor.html = value.kontenKarya
                    binding.include.sumberKaryaInput.editText?.setText(value.sumber)
                }
            }
        }
    }

    private fun setupTextEditor() {
        val editor = binding.include.editor
        editor.apply {
            setEditorHeight(200)
            setFontSize(12)
            setPadding(12, 12, 12, 12)
            setPlaceholder("Buat konten karya tulis disini...")
        }

        binding.include.apply {
            actionUndo.setOnClickListener {
                editor.undo()
            }
            actionRedo.setOnClickListener {
                editor.redo()
            }
            actionBold.setOnClickListener {
                editor.setBold()
            }
            actionItalic.setOnClickListener {
                editor.setItalic()
            }
            actionSubscript.setOnClickListener {
                editor.setSubscript()
            }
            actionSuperscript.setOnClickListener {
                editor.setSuperscript()
            }
            actionUnderline.setOnClickListener {
                editor.setUnderline()
            }
            actionStrikethrough.setOnClickListener {
                editor.setStrikeThrough()
            }
            actionHeading1.setOnClickListener {
                editor.setHeading(1)
            }
            actionHeading2.setOnClickListener {
                editor.setHeading(2)
            }
            actionHeading3.setOnClickListener {
                editor.setHeading(3)
            }
            actionHeading4.setOnClickListener {
                editor.setHeading(4)
            }
            actionHeading5.setOnClickListener {
                editor.setHeading(5)
            }
            actionHeading6.setOnClickListener {
                editor.setHeading(6)
            }
            actionAlignLeft.setOnClickListener {
                editor.setAlignLeft()
            }
            actionAlignCenter.setOnClickListener {
                editor.setAlignCenter()
            }
            actionAlignRight.setOnClickListener {
                editor.setAlignRight()
            }
            actionBlockquote.setOnClickListener {
                editor.setBlockquote()
            }
            actionInsertNumbers.setOnClickListener {
                editor.setNumbers()
            }
            actionInsertBullets.setOnClickListener {
                editor.setBullets()
            }
        }
    }

    private fun setupOptionAlertDialog(items: List<String>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih aksi")
        val arrayItems = items.toTypedArray()
        builder.setItems(arrayItems) { _, which ->
            when (which) {
                0 -> {
                    binding.include.editor.setOnTextChangeListener { text ->
                        kontenKarya = text
                    }
                    viewModel.saveKaryaTulisDraft(
                        karyaTulis = KaryaTulis(
                            kontenKarya = kontenKarya,
                            judulKarya = binding.include.inputJudulKarya.editText?.text.toString(),
                            kategoriKaryaTulisId = kategoriKaryaTulisId,
                            sumber = binding.include.sumberKaryaInput.editText?.text.toString(),
                            tipe = "tambah"
                        )
                    )
                    showToast("Karya tulis berhasil disimpan di draft")
                }

                1 -> {
                    viewModel.tambahKaryaTulis(
                        UploadKaryaTulisRequest(
                            judulKarya = binding.include.inputJudulKarya.editText?.text.toString(),
                            kontenKarya = kontenKarya,
                            kategoriKaryaTulisId = kategoriKaryaTulisId,
                            sumber = binding.include.sumberKaryaInput.editText?.text.toString()
                        )
                    )
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun onBackPressed() {
        onBackPressedKarya {
            showDialogBack()
        }
    }

    private fun showDialogBack() {
        binding.include.editor.setOnTextChangeListener { text ->
            kontenKarya = text
        }
        showDialogBack(
            condition = kontenKarya != "",
            onPositiveButton = {
                findNavController().navigateUp()
            },
            defaultBackAction = {
                findNavController().navigateUp()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        items.clear()
        namaKategori.clear()
    }
}