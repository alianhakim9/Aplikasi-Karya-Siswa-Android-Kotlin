package tedc.alian.karyasiswa.presentation.fragment.guru.karya.kelola_kategori_karya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentTambahKategoriBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.BaseUiStateViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.GuruViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.*

@AndroidEntryPoint
class TambahKategoriFragment : BaseFragment<FragmentTambahKategoriBinding>() {
    private val viewModel: GuruViewModel by viewModels()
    private val uiViewModel: BaseUiStateViewModel by viewModels()
    private var selectedItemKategori: String = ""
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.upload_kategori_progress_dialog)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTambahKategoriBinding {
        return FragmentTambahKategoriBinding.inflate(layoutInflater)
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
        binding.inputNamaKategori.editText?.setText(viewModel.getKategori())
        binding.inputNamaKategori.editText?.addListener(
            onTextChanged = { s, _, _, _ ->
                binding.btnTambah.isEnabled = s.toString().trim()
                    .isNotEmpty()
                uiViewModel.setButtonEnabled(binding.btnTambah.isEnabled)
            },
            afterTextChanged = { s ->
                viewModel.setKategori(s.toString())
            }
        )
        binding.btnTambah.isEnabled = uiViewModel.isButtonEnabled()
    }

    private fun uiAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        val items = listOf("Karya Visual", "Karya Tulis")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_kategori, items)
        (binding.dropdownKategoriKarya.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.dropdownKategoriKarya.editText as? AutoCompleteTextView)?.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            selectedItemKategori = selectedItem
        }
        binding.btnTambah.setOnClickListener {
            if (selectedItemKategori == "Karya Visual") {
                viewModel.tambahKategoriKaryaCitra(
                    namaKategori = viewModel.getKategori()
                )
            }
            if (selectedItemKategori == "Karya Tulis") {
                viewModel.tambahKategoriKaryaTulis(
                    namaKategori = viewModel.getKategori()
                )
            }
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            viewModel.tambahKategorikarya.collect { resource ->
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
                        showToast(resource.data.toString())
                        binding.inputNamaKategori.editText?.setText("")
                    }
                }
            }
        }
    }
}