package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_citra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.BottomSheetAksiKaryaBinding
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showDialog
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class AksiKaryaCitraBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAksiKaryaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: KaryaViewModel by viewModels()
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAksiKaryaBinding.inflate(layoutInflater)
        uiSetup()
        return binding.root
    }

    private fun uiSetup() {
        val listKaryaCitraKuFragment =
            parentFragmentManager.fragments.firstOrNull { it is ListKaryaCitraKuFragment } as? ListKaryaCitraKuFragment
        val bundle = arguments
        if (bundle != null) {
            val args = AksiKaryaCitraBottomSheetArgs.fromBundle(bundle)
            viewModel.karyaCitraId.value = args.karyaCitra.id.toString()
            binding.btnHapusKarya.setOnClickListener {
                showDialog(
                    title = "Hapus karya",
                    message = "Apakah anda yakin akan menghapus karya ini?",
                    negativeButtonText = "Batal",
                    positiveButtonText = "Ya",
                    onPositiveButton = {
                        viewModel.hapusKaryaCitra()
                    }
                )
            }
            binding.btnEditKarya.setOnClickListener {
                this@AksiKaryaCitraBottomSheet.dismiss()
                val action =
                    AksiKaryaCitraBottomSheetDirections.actionAksiKaryaCitraBottomSheetToEditKaryaCitraFragment(
                        args.karyaCitra
                    )
                findNavController().navigate(action)
            }
            repeatOnViewLifecycle {
                viewModel.deleteKarya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> progressDialog.show()
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                            this@AksiKaryaCitraBottomSheet.dismiss()
                        }
                        is Resource.Success -> {
                            progressDialog.dismiss()
                            showToast("Karya berhasil dihapus")
                            listKaryaCitraKuFragment?.onDelete()
                            this@AksiKaryaCitraBottomSheet.dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}