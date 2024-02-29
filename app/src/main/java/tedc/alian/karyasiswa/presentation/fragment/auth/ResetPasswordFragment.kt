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
import tedc.alian.karyasiswa.databinding.FragmentResetPasswordBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.AuthViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.BaseUiStateViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.*

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>() {

    private val viewModel: AuthViewModel by viewModels()
    private val uiViewModel: BaseUiStateViewModel by viewModels()
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentResetPasswordBinding {
        return FragmentResetPasswordBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        uiAction()
        observer()
    }

    private fun uiSetup() {
        with(binding) {
            with(textInputPassword.editText) {
                this?.setText(viewModel.getPassword())
                this?.addListener(
                    onTextChanged = { s, _, _, _ ->
                        btnReset.isEnabled = s.toString()
                            .isNotEmpty() && textInputKonfirmasiPassword.editText?.text.toString()
                            .isNotEmpty()
                        uiViewModel.setButtonEnabled(btnReset.isEnabled)
                    },
                    afterTextChanged = { s ->
                        viewModel.setPassword(s.toString())
                    }
                )
            }
            with(textInputKonfirmasiPassword.editText) {
                this?.setText(viewModel.getPasswordConfirm())
                this?.addListener(
                    onTextChanged = { s, _, _, _ ->
                        btnReset.isEnabled = s.toString()
                            .isNotEmpty() && textInputPassword.editText?.text.toString()
                            .isNotEmpty()
                        uiViewModel.setButtonEnabled(btnReset.isEnabled)
                    },
                    afterTextChanged = { s ->
                        viewModel.setPasswordConfirm(s.toString())
                    }
                )
            }
            btnReset.isEnabled = uiViewModel.isButtonEnabled()
            if (uiViewModel.isProgressDialogVisible()) {
                progressDialog.show()
            }
        }
    }

    private fun uiAction() {
        val args = arguments ?: return
        val bundle = ResetPasswordFragmentArgs.fromBundle(args)
        val email = bundle.email
        val token = bundle.token
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnReset.setOnClickListener {
            val password = viewModel.getPassword() ?: ""
            val konfirmasiPassword = viewModel.getPasswordConfirm()
            if (password != konfirmasiPassword) showToast(
                "Password tidak sama"
            )
            else viewModel.resetPassword(
                token = token,
                email = email,
                password = viewModel.getPassword() ?: "",
                konfirmasiPassword = viewModel.getPasswordConfirm() ?: ""
            )
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            viewModel.resetPassword.collect { resource ->
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
                        showToast(resource.data ?: "Password telah diperbarui")
                        findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                    }
                }
            }
        }
    }
}