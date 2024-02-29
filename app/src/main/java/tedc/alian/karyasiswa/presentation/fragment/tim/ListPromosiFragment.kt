package tedc.alian.karyasiswa.presentation.fragment.tim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tedc.alian.karyasiswa.databinding.FragmentListPromosiBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.ListPromosiAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.TimViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class ListPromosiFragment : BaseFragment<FragmentListPromosiBinding>() {

    private val viewModel: TimViewModel by viewModels()
    private val adapter: ListPromosiAdapter by lazy {
        ListPromosiAdapter()
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListPromosiBinding {
        return FragmentListPromosiBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setupRecyclerView()
        viewModel.listPromosi()
        observer()
    }

    private fun observer() {
        repeatOnViewLifecycle {
            viewModel.listPromosi.collect { resource ->
                when (resource) {
                    is Resource.Loading -> showLoading()
                    is Resource.Error -> {
                        showLoading()
                        showToast("Terjadi kesalahan saat mengambil data")
                    }

                    is Resource.Success -> {
                        hideLoading()
                        val data = resource.data
                        if (data != null) {
                            adapter.submitList(data)
                        }
                        binding.emptyData.root.isVisible = adapter.itemCount == 0
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvPromosi.adapter = adapter
        binding.rvPromosi.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showLoading() {
        binding.include.loading.isVisible = true
        binding.rvPromosi.isVisible = false
        binding.emptyData.root.isVisible = false
    }

    private fun hideLoading() {
        binding.include.loading.isVisible = false
        binding.rvPromosi.isVisible = true
    }

}