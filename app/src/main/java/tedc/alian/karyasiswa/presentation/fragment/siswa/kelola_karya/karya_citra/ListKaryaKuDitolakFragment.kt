package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_citra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.KaryaCitraDitolakDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentListKaryaKuDitolakBinding
import tedc.alian.karyasiswa.presentation.adapters.paging.ListKaryaCitraDitolakAdapter
import tedc.alian.karyasiswa.presentation.adapters.paging.load_state.ListKaryaCitraLoadStateAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.ListKaryaCitraViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showDialog
import tedc.alian.utils.helper.showToast
import javax.inject.Inject

@AndroidEntryPoint
class ListKaryaKuDitolakFragment : BaseFragment<FragmentListKaryaKuDitolakBinding>(),
    ListKaryaCitraDitolakAdapter.OnItemClickListener {

    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel: ListKaryaCitraViewModel by viewModels()
    private val karyaViewModel: KaryaViewModel by viewModels()
    private val adapter: ListKaryaCitraDitolakAdapter by lazy {
        ListKaryaCitraDitolakAdapter(this, imageLoader)
    }
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListKaryaKuDitolakBinding {
        return FragmentListKaryaKuDitolakBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllKaryaDitolak()
        uiSetup()
        observer()
        uiAction()
    }

    private fun uiSetup() {
        setupRecyclerView()
    }

    private fun uiAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.getAllKaryaDitolak().collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                    binding.emptyData.root.isVisible = adapter.itemCount == 0
                    binding.rvKaryaDitolak.isVisible = adapter.itemCount != 0
                }
            }
            launch {
                karyaViewModel.deleteKarya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> progressDialog.show()
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                        }
                        is Resource.Success -> {
                            progressDialog.dismiss()
                            showToast("Karya berhasil dihapus")
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvKaryaDitolak.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            this.adapter = this@ListKaryaKuDitolakFragment.adapter.withLoadStateHeaderAndFooter(
                header = ListKaryaCitraLoadStateAdapter {
                    this@ListKaryaKuDitolakFragment.adapter.retry()
                },
                footer = ListKaryaCitraLoadStateAdapter {
                    this@ListKaryaKuDitolakFragment.adapter.retry()
                }
            )
        }
        adapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> showLoading()
                is LoadState.Error -> {
                    showLoading()
                    showToast("Terjadi kesalahan saat mengambil data")
                }
                is LoadState.NotLoading -> {
                    hideLoading()
                    binding.emptyData.root.isVisible = adapter.itemCount == 0
                }
            }
        }
    }

    override fun onHapus(karyaCitra: KaryaCitraDitolakDto) {
        showDialog(
            title = "Hapus karya",
            message = "Apakah anda yakin akan menghapus karya ini?",
            negativeButtonText = "Batal",
            positiveButtonText = "Hapus"
        ) {
            karyaViewModel.karyaCitraId.value = karyaCitra.id.toString()
            karyaViewModel.hapusKaryaCitra()
            viewModel.getAllKaryaDitolak()
        }
    }

    override fun onLihat(karyaCitra: KaryaCitraDitolakDto) {
        val action = ListKaryaKuDitolakFragmentDirections
            .actionListKaryaKuDitolakFragmentToDetailKaryaDitolakFragment(karyaCitra)
        findNavController().navigate(action)
    }

    private fun showLoading() {
        binding.include.loading.isVisible = true
        binding.rvKaryaDitolak.isVisible = false
        binding.emptyData.root.isVisible = false
    }

    private fun hideLoading() {
        binding.include.loading.isVisible = false
        binding.rvKaryaDitolak.isVisible = true
    }
}