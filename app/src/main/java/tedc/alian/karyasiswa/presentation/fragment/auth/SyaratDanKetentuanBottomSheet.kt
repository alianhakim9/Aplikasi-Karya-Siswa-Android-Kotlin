package tedc.alian.karyasiswa.presentation.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.BottomSheetSyaratDanKetentuanBinding

class SyaratDanKetentuanBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSyaratDanKetentuanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSyaratDanKetentuanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClose.setOnClickListener {
            this.dismiss()
        }
        val inputStream = resources.openRawResource(R.raw.syarat_ketentuan)
        val inputString = inputStream.bufferedReader().use { it.readText() }
        binding.termAndConditionBody.text = inputString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}