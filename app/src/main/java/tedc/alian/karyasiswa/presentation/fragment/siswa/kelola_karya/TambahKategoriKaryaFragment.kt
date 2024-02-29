package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentTambahKategoriKaryaBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment

@AndroidEntryPoint
class TambahKategoriKaryaFragment : BaseFragment<FragmentTambahKategoriKaryaBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTambahKategoriKaryaBinding {
        return FragmentTambahKategoriKaryaBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiAction()
    }

    private fun uiAction() {
        binding.cvKaryaCitra.setOnClickListener {
            findNavController().navigate(R.id.action_tambahKategoriKaryaFragment_to_tambahKaryaCitraFragment)
        }
        binding.cvKaryaTulis.setOnClickListener {
            findNavController().navigate(R.id.action_tambahKategoriKaryaFragment_to_tambahKaryaTulisFragment)
        }
    }
}