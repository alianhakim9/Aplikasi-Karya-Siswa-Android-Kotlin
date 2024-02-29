package tedc.alian.karyasiswa.presentation.fragment.tim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentHalamanUtamaTimBinding
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.TimViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.setGreeting
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class HalamanUtamaTimFragment : BaseFragment<FragmentHalamanUtamaTimBinding>() {

    private val viewModel: TimViewModel by viewModels()
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHalamanUtamaTimBinding {
        return FragmentHalamanUtamaTimBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getProfil()
        viewModel.getActivePromosi()
        setGreeting(binding.tvGreeting2)
        binding.cvPromosi.setOnClickListener {
            findNavController().navigate(R.id.action_halamanUtamaTimFragment_to_listPromosiFragment)
        }
        dataObserver()
    }

    private fun showLoadingProfil() {
        with(binding) {
            include.loading.visibility = View.VISIBLE
            container.visibility = View.GONE
        }
    }

    private fun hideLoadingProfil() {
        with(binding) {
            include.loading.visibility = View.GONE
            container.visibility = View.VISIBLE
        }
    }

    private fun dataObserver() {
        repeatOnViewLifecycle {
            launch {
                viewModel.profil.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoadingProfil()
                        is Resource.Error -> {
                            hideLoadingProfil()
                            showToast(resource.throwable.message.toString())
                        }
                        is Resource.Success -> {
                            hideLoadingProfil()
                            with(binding) {
                                tvGreeting.text =
                                    getString(R.string.txt_greeting, resource.data?.namaLengkap)
                                binding.profilePicture.load(
                                    R.drawable.ic_avatar
                                ) {
                                    crossfade(true)
                                    transformations(CircleCropTransformation())
                                }
                            }
                        }
                    }
                }
            }
            launch {
                viewModel.activePromosi.collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.promosi.load(resource.data?.gambar) {
                                crossfade(true)
                                transformations(RoundedCornersTransformation(20f))
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}