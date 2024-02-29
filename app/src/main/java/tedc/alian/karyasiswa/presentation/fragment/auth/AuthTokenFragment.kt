package tedc.alian.karyasiswa.presentation.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.databinding.FragmentAuthTokenBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.AuthViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.BaseUiStateViewModel
import tedc.alian.utils.helper.addListener

@AndroidEntryPoint
class AuthTokenFragment : BaseFragment<FragmentAuthTokenBinding>() {

    private val viewModel: AuthViewModel by viewModels()
    private val uiViewModel: BaseUiStateViewModel by viewModels()
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAuthTokenBinding {
        return FragmentAuthTokenBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        uiAction()
        getDataFromArgs()
    }

    private fun getDataFromArgs() {
        val args = arguments
        val bundle = AuthTokenFragmentArgs.fromBundle(args!!)
        viewModel.setEmail(bundle.email)
    }

    private fun uiSetup() {
        with(binding) {
            with(inputToken.editText) {
                this?.setText(viewModel.getToken())
                this?.addListener(
                    onTextChanged = { s, _, _, _ ->
                        btnCek.isEnabled = s.toString().isNotEmpty()
                        uiViewModel.setButtonEnabled(btnCek.isEnabled)
                    },
                    afterTextChanged = { s ->
                        viewModel.setToken(s.toString())
                    }
                )
            }
            btnCek.isEnabled = uiViewModel.isButtonEnabled()
        }
    }

    private fun uiAction() {
        binding.btnCek.setOnClickListener {
            val email = viewModel.getEmail().orEmpty()
            val token = viewModel.getToken().orEmpty()
            val action =
                AuthTokenFragmentDirections.actionAuthTokenFragmentToForgotPasswordFragment(
                    email, token
                )
            findNavController().navigate(action)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}