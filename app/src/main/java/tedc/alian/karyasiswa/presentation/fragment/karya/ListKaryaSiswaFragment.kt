package tedc.alian.karyasiswa.presentation.fragment.karya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentListKaryaSiswaBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.ListKaryaCitraSiswaAdapter
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.ListKaryaTulisSiswaAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast
import javax.inject.Inject

@AndroidEntryPoint
class ListKaryaSiswaFragment : BaseFragment<FragmentListKaryaSiswaBinding>(),
    ListKaryaCitraSiswaAdapter.InterfaceListener,
    ListKaryaTulisSiswaAdapter.InterfaceListener {

    @Inject
    lateinit var imageLoader: ImageLoader

    private val karyaCitraSiswaAdapter: ListKaryaCitraSiswaAdapter by lazy {
        ListKaryaCitraSiswaAdapter(this)
    }
    private val karyaTulisSiswaAdapter: ListKaryaTulisSiswaAdapter by lazy {
        ListKaryaTulisSiswaAdapter(this)
    }
    private val viewModel: KaryaViewModel by viewModels()

    private var karyaCitraError = false
    private var karyaTulisError = false

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListKaryaSiswaBinding {
        return FragmentListKaryaSiswaBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLihatSemuaKaryaCitra.setOnClickListener {
            findNavController().navigate(R.id.action_listKaryaSiswaFragment_to_listKaryaCitraFragment)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setupRecyclerView()
        viewModel.getAllKaryaCitra()
        viewModel.getAllKaryaTulis()
        karyaObserver()
    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        manager.isSmoothScrollbarEnabled = true
        val manager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        manager2.isSmoothScrollbarEnabled = true
        binding.rvKaryaCitra.adapter = karyaCitraSiswaAdapter
        binding.rvKaryaCitra.layoutManager = manager
        binding.rvKaryaCitra.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        binding.rvKaryaTulis.adapter = karyaTulisSiswaAdapter
        binding.rvKaryaTulis.layoutManager = manager2
        binding.rvKaryaTulis.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
    }

    private fun karyaObserver() {
        repeatOnViewLifecycle {
            launch {
                viewModel.karya.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            hideLoading()
                            karyaCitraSiswaAdapter.submitList(resource.data)
                            binding.rvKaryaCitra.isVisible = karyaCitraSiswaAdapter.itemCount != 0
                        }

                        is Resource.Error -> {
                            hideLoading()
                            showToast(resource.throwable.message.toString())
                            karyaCitraError = true
                            showMultipleError()
                        }

                        is Resource.Loading -> showLoading()
                    }
                }
            }
            launch {
                viewModel.karyaTulis.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            hideLoading()
                            karyaTulisSiswaAdapter.submitList(resource.data)
                            binding.rvKaryaTulis.isVisible = karyaTulisSiswaAdapter.itemCount != 0
                        }

                        is Resource.Error -> {
                            hideLoading()
                            showToast(resource.throwable.message.toString())
                            karyaTulisError = true
                            showMultipleError()
                        }

                        is Resource.Loading -> showLoading()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            include.loading.isVisible = true
            setVisibility(false)
        }
    }

    private fun hideLoading() {
        with(binding) {
            include.loading.isVisible = false
            setVisibility(true)
        }
    }

    override fun onItemClick(item: KaryaCitraDto) {
        val action =
            ListKaryaSiswaFragmentDirections.actionListKaryaSiswaFragmentToDetailKaryaCitraFragment(
                item.id
            )
        findNavController().navigate(action)
    }

    override fun onItemClick(item: KaryaTulisDto) {
        val action =
            ListKaryaSiswaFragmentDirections.actionListKaryaSiswaFragmentToDetailKaryaTulisFragmentSiswa(
                item.id
            )
        findNavController().navigate(action)
    }

    private fun setVisibility(value: Boolean) {
        with(binding) {
            tvKaryaTulis.isVisible = value
            tvKaryaVisual.isVisible = value
            tvLihatSemuaKaryaCitra.isVisible = value
            rvKaryaCitra.isVisible = value
            rvKaryaTulis.isVisible = value
        }
    }

    private fun showMultipleError() {
        if (karyaCitraError && karyaTulisError) {
            showLoading()
        }
    }
}