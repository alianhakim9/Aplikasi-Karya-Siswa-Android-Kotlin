package tedc.alian.karyasiswa.presentation.fragment.notifikasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tedc.alian.data.remote.api.karya.NotifikasiDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentListNotifikasiGuruBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.ListNotifikasiGuruAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class ListNotifikasiGuruFragment : BaseFragment<FragmentListNotifikasiGuruBinding>(),
    ListNotifikasiGuruAdapter.InterfaceListener {

    private val viewModel: KaryaViewModel by viewModels()
    private val adapter: ListNotifikasiGuruAdapter by lazy {
        ListNotifikasiGuruAdapter(requireContext(), this)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListNotifikasiGuruBinding {
        return FragmentListNotifikasiGuruBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setupRecyclerView()
        viewModel.getNotifikasi()
        observer()

    }

    private fun setupRecyclerView() {
        binding.rvNotifikasi.adapter = adapter
        binding.rvNotifikasi.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showLoading() {
        binding.include.loading.isVisible = true
        binding.rvNotifikasi.isVisible = false
    }

    private fun hideLoading() {
        binding.include.loading.isVisible = false
        binding.rvNotifikasi.isVisible = true
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.notifikasi.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Error -> {
                            showLoading()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            hideLoading()
                            val data = resource.data?.data
                            adapter.submitList(data ?: emptyList())
                            binding.emptyNotifikasi.root.isVisible = adapter.itemCount == 0
                        }
                    }
                }
            }
            launch {
                viewModel.hapusNotifikasi.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Error -> {
                            hideLoading()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            hideLoading()
                            viewModel.getNotifikasi()
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(notifikasiDto: NotifikasiDto) {
        viewModel.updateNotifikasi(notifikasiDto.karyaCitra.id, notifikasiDto.id.toString())
        runBlocking {
            delay(500)
        }
        findNavController().navigate(R.id.action_listNotifikasiFragment_to_listWaitingValidasiKaryaFragment)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNotifikasi()
    }
}