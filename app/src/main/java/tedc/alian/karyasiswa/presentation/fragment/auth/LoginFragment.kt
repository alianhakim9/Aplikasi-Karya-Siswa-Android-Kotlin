package tedc.alian.karyasiswa.presentation.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentLoginBinding
import tedc.alian.karyasiswa.presentation.activity.DashboardGuruActivity
import tedc.alian.karyasiswa.presentation.activity.DashboardSiswaActivity
import tedc.alian.karyasiswa.presentation.activity.DashboardTimActivity
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.AuthViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.BaseUiStateViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.*

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: AuthViewModel by viewModels()
    private val uiViewModel: BaseUiStateViewModel by viewModels()
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        uiSetup()
        uiAction(navController)
        observer()
    }

    private fun uiSetup() {
        with(binding) {
            with(emailTextField.editText) {
                this?.setText(viewModel.getEmail())
                this?.addListener(
                    onTextChanged = { s, _, _, _ ->
                        btnLogin.isEnabled = s.toString().trim()
                            .isNotEmpty() && passwordTextField.editText?.text.toString()
                            .isNotEmpty()
                        uiViewModel.setButtonEnabled(btnLogin.isEnabled)
                    },
                    afterTextChanged = { s ->
                        viewModel.setEmail(s.toString())
                    }
                )
            }
            with(passwordTextField.editText) {
                this?.setText(viewModel.getPassword())
                this?.addListener(
                    onTextChanged = { s, _, _, _ ->
                        btnLogin.isEnabled = s.toString().trim()
                            .isNotEmpty() && emailTextField.editText?.text.toString()
                            .isNotEmpty()
                        uiViewModel.setButtonEnabled(btnLogin.isEnabled)
                    },
                    afterTextChanged = { s ->
                        viewModel.setPassword(s.toString())
                    }
                )
            }
            btnLogin.isEnabled = uiViewModel.isButtonEnabled()
            if (uiViewModel.isProgressDialogVisible()) {
                progressDialog.show()
            }
        }
    }

    private fun uiAction(navController: NavController) {
        binding.btnLogin.setOnClickListener {
            viewModel.login(
                email = binding.emailTextField.editText?.text.toString().trim(),
                password = binding.passwordTextField.editText?.text.toString().trim(),
                binding.loginSessionCheckbox.isChecked
            )
        }
        binding.forgotPasswordTv.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_searchAccountFragment)
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        progressDialog.show()
                        uiViewModel.setProgressDialogVisibility(progressDialog.isShowing)
                    }
                    is Resource.Error -> {
                        progressDialog.dismiss()
                        uiViewModel.setProgressDialogVisibility(progressDialog.isShowing)
                        showToast(it.throwable.message.toString())
                        hideKeyboard()
                    }
                    is Resource.Success -> {
                        progressDialog.dismiss()
                        uiViewModel.setProgressDialogVisibility(progressDialog.isShowing)
                        when (it.data?.data?.user?.roleId) {
                            2 -> startActivityAndFinish<DashboardGuruActivity>()
                            3 -> startActivityAndFinish<DashboardSiswaActivity>()
                            4 -> startActivityAndFinish<DashboardTimActivity>()
                        }
                    }
                }
            }
        }
    }
}