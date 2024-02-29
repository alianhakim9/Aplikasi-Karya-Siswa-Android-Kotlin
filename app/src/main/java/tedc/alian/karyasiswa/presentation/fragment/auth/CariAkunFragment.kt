package tedc.alian.karyasiswa.presentation.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentCariAkunBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.AuthViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.BaseUiStateViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.*

@AndroidEntryPoint
class CariAkunFragment : BaseFragment<FragmentCariAkunBinding>() {

    private val viewModel: AuthViewModel by viewModels()
    private val uiViewModel: BaseUiStateViewModel by viewModels()

    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCariAkunBinding {
        return FragmentCariAkunBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        uiAction()
        observer()
    }

    private fun uiSetup() {
        with(binding) {
            inputEmail.editText?.setText(viewModel.getEmail())
            inputEmail.editText?.addListener(
                onTextChanged = { s, _, _, _ ->
                    btnCari.isEnabled = s.toString().isNotEmpty()
                    uiViewModel.setButtonEnabled(btnCari.isEnabled)
                },
                afterTextChanged = { s ->
                    viewModel.setEmail(s.toString())
                }
            )
            btnCari.isEnabled = uiViewModel.isButtonEnabled()
        }
        if (uiViewModel.isProgressDialogVisible()) {
            progressDialog.show()
        }
    }

    private fun uiAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnCari.setOnClickListener {
            viewModel.cariAkun(email = binding.inputEmail.editText?.text.toString())
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            viewModel.cariAkun.collect { resource ->
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
                            CariAkunFragmentDirections.actionSearchAccountFragmentToAuthTokenFragment(
                                binding.inputEmail.editText?.text.toString()
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }
}