package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_tulis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.BottomSheetAksiKaryaBinding
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.showDialog
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class AksiKaryaTulisBottomSheet : BottomSheetDialogFragment() {
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
        val listKaryaTulisKuFragment =
            parentFragmentManager.fragments.firstOrNull { it is ListKaryaTulisKuFragment } as? ListKaryaTulisKuFragment
        val bundle = arguments
        if (bundle != null) {
            val args = AksiKaryaTulisBottomSheetArgs.fromBundle(bundle)
            viewModel.karyaTulisId.value = args.karyaTulis.id.toString()
            binding.btnHapusKarya.setOnClickListener {
                showDialog(
                    title = "Hapus karya",
                    message = "Apakah anda yakin akan menghapus karya ini?",
                    negativeButtonText = "Batal",
                    positiveButtonText = "Ya",
                    onPositiveButton = {
                        progressDialog.show()
                        viewModel.hapusKaryaTulis()
                    })
            }
            binding.btnEditKarya.setOnClickListener {
                this@AksiKaryaTulisBottomSheet.dismiss()
                val action =
                    AksiKaryaTulisBottomSheetDirections.actionAksiKaryaTulisBottomSheetToEditKaryaTulisFragment(
                        args.karyaTulis
                    )
                findNavController().navigate(action)
            }
            lifecycleScope.launchWhenStarted {
                viewModel.deleteKarya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> progressDialog.show()
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                            this@AksiKaryaTulisBottomSheet.dismiss()
                        }
                        is Resource.Success -> {
                            progressDialog.dismiss()
                            showToast(resource.data)
                            listKaryaTulisKuFragment?.onDelete()
                            this@AksiKaryaTulisBottomSheet.dismiss()
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}