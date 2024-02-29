package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_citra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.databinding.BottomSheetPilihanMediaBinding

@AndroidEntryPoint
class PilihanMediaKaryaBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPilihanMediaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPilihanMediaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiAction()
    }

    private fun uiAction() {
        val tambahKaryaCitraFragment =
            parentFragmentManager.fragments.firstOrNull { it is TambahKaryaCitraFragment } as? TambahKaryaCitraFragment
        val ubahKaryaCitraFragment =
            parentFragmentManager.fragments.firstOrNull { it is UbahKaryaCitraFragment } as? UbahKaryaCitraFragment
        val bundle = arguments ?: return
        val args = PilihanMediaKaryaBottomSheetArgs.fromBundle(bundle)
        val isEdit = args.isEdit
        binding.btnGaleri.setOnClickListener {
            if (isEdit) {
                ubahKaryaCitraFragment?.getKaryaGaleriContract()
                this.dismiss()
            }
            tambahKaryaCitraFragment?.getKaryaGaleriContract()
            this.dismiss()
        }
        binding.btnKamera.setOnClickListener {
            if (isEdit) {
                ubahKaryaCitraFragment?.getKaryaKameraContract()
                this.dismiss()
            }
            tambahKaryaCitraFragment?.getKaryaKameraContract()
            this.dismiss()
        }
        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}