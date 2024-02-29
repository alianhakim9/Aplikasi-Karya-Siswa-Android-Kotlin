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
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.data.remote.api.karya.UpdateKaryaTulisRequest
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentUbahKaryaTulisBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.hideKeyboard
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class UbahKaryaTulisFragment : BaseFragment<FragmentUbahKaryaTulisBinding>() {

    private val viewModel: KaryaViewModel by viewModels()
    private var namaKategori = mutableListOf<String>()
    private var items = mutableListOf<KategoriKaryaDto>()
    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(requireContext(), R.layout.item_kategori, namaKategori)
    }
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.save_progress_dialog)
    }
    private val loadingProgressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.get_data_progress_dialog)
    }

    private var kategoriKaryaTulisId: String? = null
    private var kontenKarya: String? = null
    private val options = listOf("Simpan sebagai draft", "Update karya tulis")

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUbahKaryaTulisBinding {
        return FragmentUbahKaryaTulisBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: return
        val args = UbahKaryaTulisFragmentArgs.fromBundle(bundle)
        val karyaTulis = args.karyaTulis
        viewModel.getKategoriKaryaTulis()
        uiSetup(karyaTulis)
        uiAction(karyaTulis)
        observer()
    }

    private fun uiSetup(karyaTulis: KaryaTulisDto) {
        (binding.include.dropdownKaryaTulis.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
        val karyaTulisDropdown = binding.include.dropdownKaryaTulis.editText as AutoCompleteTextView
        karyaTulisDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                kategoriKaryaTulisId = items[position].id.toString()
            }
        viewModel.karyaTulisId.value = karyaTulis.id
        binding.include.inputJudulKarya.editText?.setText(karyaTulis.judulKarya)
        binding.include.editor.html = karyaTulis.kontenKarya
        binding.include.sumberKaryaInput.editText?.setText("Sumber")
        val editor = binding.include.editor
        editor.apply {
            setEditorHeight(200)
            setFontSize(12)
            setPadding(12, 12, 12, 12)
            setPlaceholder("Ubah konten karya tulis disini...")
        }
    }

    private fun uiAction(karyaTulis: KaryaTulisDto) {
        binding.include.editor.setOnTextChangeListener { text ->
            kontenKarya = text
        }
        binding.btnDone.setOnClickListener {
            viewModel.ubahKaryaTulis(
                UpdateKaryaTulisRequest(
                    judulKarya = binding.include.inputJudulKarya.editText?.text.toString(),
                    kontenKarya = kontenKarya ?: karyaTulis.kontenKarya,
                    kategoriKaryaTulisId = kategoriKaryaTulisId
                        ?: karyaTulis.kategoriKaryaTulisId,
                    sumber = binding.include.sumberKaryaInput.editText?.text.toString()
                )
            )
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
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

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.kategoriKaryaTulis.collectLatest { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            loadingProgressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            loadingProgressDialog.dismiss()
                            resource.data.forEach { kategoriKarya ->
                                if (kategoriKarya != null) {
                                    namaKategori.add(kategoriKarya.namaKategori)
                                    items.add(kategoriKarya)
                                }
                            }
                        }

                        is Resource.Loading -> loadingProgressDialog.show()
                    }
                }
            }
            launch {
                viewModel.updateKaryaTulis.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> progressDialog.show()
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                            hideKeyboard()
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
}