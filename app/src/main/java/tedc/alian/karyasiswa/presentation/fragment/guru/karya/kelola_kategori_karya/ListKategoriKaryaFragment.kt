package tedc.alian.karyasiswa.presentation.fragment.guru.karya.kelola_kategori_karya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentListKategoriKaryaBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.ListKategoriKaryaCitraAdapter
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.ListKategoriKaryaTulisAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.GuruViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showDialog
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class ListKategoriKaryaFragment : BaseFragment<FragmentListKategoriKaryaBinding>(),
    ListKategoriKaryaCitraAdapter.InterfaceListener,
    ListKategoriKaryaTulisAdapter.InterfaceListener {

    private val viewModel: GuruViewModel by viewModels()
    private val adapter: ListKategoriKaryaCitraAdapter by lazy {
        ListKategoriKaryaCitraAdapter(this)
    }
    private val adapterKategoriKaryaTulis: ListKategoriKaryaTulisAdapter by lazy {
        ListKategoriKaryaTulisAdapter(this)
    }
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListKategoriKaryaBinding {
        return FragmentListKategoriKaryaBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getData()
        observer()
        uiAction()
    }

    private fun getData() {
        viewModel.listKategoriKaryaCitra()
        viewModel.listKategoriKaryaTulis()
    }

    private fun uiAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.listKategoriKaryaCitra.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Error -> {
                            showLoading()
                            showToast(resource.throwable.toString())
                        }
                        is Resource.Success -> {
                            hideLoading()
                            adapter.submitList(resource.data!!)
                            binding.emptyDataKaryaCitra.root.isVisible = adapter.itemCount == 0
                        }
                    }
                }
            }
            launch {
                viewModel.listKategoriKaryaTulis.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Error -> {
                            hideLoading()
                            showToast(resource.throwable.message.toString())
                        }
                        is Resource.Success -> {
                            hideLoading()
                            adapterKategoriKaryaTulis.submitList(resource.data!!)
                            binding.emptyDataKaryaTulis.root.isVisible = adapter.itemCount == 0
                        }
                    }
                }
            }
            launch {
                viewModel.hapusKategoriKarya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> progressDialog.show()
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                        }
                        is Resource.Success -> {
                            progressDialog.dismiss()
                            showToast("Kategori berhasil dihapus")
                            getData()
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvKategoriKaryaTulis.adapter = adapterKategoriKaryaTulis
        binding.rvKategoriKaryaCitra.adapter = adapter
        binding.rvKategoriKaryaTulis.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKategoriKaryaCitra.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onItemClick(item: KategoriKaryaDto) {
        showDialog(
            title = "Hapus data kategori",
            message = "Apakah anda yakin akan menghapus data ini?",
            negativeButtonText = "Batal",
            positiveButtonText = "Hapus"
        ) {
            viewModel.hapusKategoriKaryaCitra(item.id.toString())
        }
    }

    private fun showLoading() {
        binding.include.loading.isVisible = true
        binding.include2.loading.isVisible = true
        binding.rvKategoriKaryaTulis.isVisible = false
        binding.rvKategoriKaryaCitra.isVisible = false
        binding.emptyDataKaryaCitra.root.isVisible = false
        binding.emptyDataKaryaTulis.root.isVisible = false
    }

    private fun hideLoading() {
        binding.include.loading.isVisible = false
        binding.include2.loading.isVisible = false
        binding.rvKategoriKaryaTulis.isVisible = true
        binding.rvKategoriKaryaCitra.isVisible = true
    }

    override fun deleteKaryaCitra(item: KategoriKaryaDto) {
        showDialog(
            title = "Hapus data kategori",
            message = "Apakah anda yakin akan menghapus data ini?",
            negativeButtonText = "Batal",
            positiveButtonText = "Hapus"
        ) {
            viewModel.hapusKategoriKaryaTulis(item.id.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
}