package tedc.alian.karyasiswa.presentation.fragment.tim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.data.remote.dto.TimDto
import tedc.alian.karyasiswa.databinding.FragmentUbahProfilTimBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.TimViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.hideKeyboard
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast


@AndroidEntryPoint
class UbahProfilTimFragment : BaseFragment<FragmentUbahProfilTimBinding>() {

    private val viewModel: TimViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUbahProfilTimBinding {
        return FragmentUbahProfilTimBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: return
        val args = UbahProfilTimFragmentArgs.fromBundle(bundle)
        val tim = args.tim
        uiSetup(tim)
        uiAction()

        observer()
    }

    private fun uiSetup(tim: TimDto) {
        binding.inputJabatan.editText?.setText(tim.jabatan)
        binding.inputNamaLengkap.editText?.setText(tim.namaLengkap)
    }

    private fun uiAction() {
        binding.btnSimpan.setOnClickListener {
            viewModel.editProfil(
                namaLengkap = binding.inputNamaLengkap.editText?.text.toString(),
                jabatan = binding.inputJabatan.editText?.text.toString()
            )
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
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