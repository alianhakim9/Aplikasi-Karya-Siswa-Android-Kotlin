package tedc.alian.karyasiswa.presentation.fragment.guru.karya.validasi_karya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentTolakKaryaBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.BaseUiStateViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.*


@AndroidEntryPoint
class TolakKaryaFragment : BaseFragment<FragmentTolakKaryaBinding>() {

    private val viewModel: KaryaViewModel by viewModels()
    private val uiViewModel: BaseUiStateViewModel by viewModels()
    val args: TolakKaryaFragmentArgs by navArgs()
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTolakKaryaBinding {
        return FragmentTolakKaryaBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.karyaCitraId.value = args.karyaCitraId.toString()
        uiSetup()
        uiAction()
        observer()
    }

    private fun uiSetup() {
        if (uiViewModel.isProgressDialogVisible()) progressDialog.show()
        binding.inputAlasan.editText?.setText(viewModel.getAlasan())
        binding.inputAlasan.editText?.addListener(
            onTextChanged = { s, _, _, _ ->
                binding.btnKirim.isEnabled = s.toString().isNotEmpty()
                uiViewModel.setButtonEnabled(binding.btnKirim.isEnabled)
            },
            afterTextChanged = { s ->
                viewModel.setAlasan(s.toString())
            }
        )
        binding.btnKirim.isEnabled = uiViewModel.isButtonEnabled()
    }

    private fun uiAction() {
        binding.btnKirim.setOnClickListener {
            viewModel.tolakKarya()
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            viewModel.tolakKarya.collect { resource ->
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
}