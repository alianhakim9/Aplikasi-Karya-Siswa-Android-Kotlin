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
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.NotifikasiDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentListNotifikasiSiswaBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.ListNotifikasiSiswaAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showDialog
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class ListNotifikasiSiswaFragment : BaseFragment<FragmentListNotifikasiSiswaBinding>(),
    ListNotifikasiSiswaAdapter.InterfaceListener {

    private val viewModel: KaryaViewModel by viewModels()

    private val adapter: ListNotifikasiSiswaAdapter by lazy {
        ListNotifikasiSiswaAdapter(requireContext(), this)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListNotifikasiSiswaBinding {
        return FragmentListNotifikasiSiswaBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setupRecyclerView()
        viewModel.getNotifikasiSiswa()
        observer()
        binding.hapusSemuaNotifikasi.setOnClickListener {
            showDialog(
                title = "Hapus semua notifikasi",
                message = "Apakah anda yakin akan menghapus semua notifikasi?",
                negativeButtonText = "Batal",
                positiveButtonText = "Hapus",
                onPositiveButton = {
                    viewModel.hapusNotifikasiSiswa()
                }
            )
        }
    }

    private fun setupRecyclerView() {
        binding.rvNotifikasi.adapter = adapter
        binding.rvNotifikasi.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showLoading() {
        binding.include.loading.isVisible = true
        binding.rvNotifikasi.isVisible = false
        binding.hapusSemuaNotifikasi.isVisible = false
    }

    private fun hideLoading() {
        binding.include.loading.isVisible = false
        binding.rvNotifikasi.isVisible = true
        binding.hapusSemuaNotifikasi.isVisible = true
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
                            if (data != null) {
                                adapter.submitList(data)
                            }
                            binding.emptyNotifikasi.root.isVisible = adapter.itemCount == 0
                            binding.hapusSemuaNotifikasi.isVisible = adapter.itemCount != 0
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
                            viewModel.getNotifikasiSiswa()
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(notifikasiDto: NotifikasiDto) {
        viewModel.updateNotifikasi(notifikasiDto.karyaCitra.id, notifikasiDto.id.toString())
        val bundle = Bundle()
        bundle.putString("id_karya_citra", notifikasiDto.idKaryaCitra.toString())
        findNavController().navigate(
            R.id.action_listNotifikasiSiswaFragment_to_detailKaryaCitraFragment,
            bundle
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNotifikasiSiswa()
    }
}