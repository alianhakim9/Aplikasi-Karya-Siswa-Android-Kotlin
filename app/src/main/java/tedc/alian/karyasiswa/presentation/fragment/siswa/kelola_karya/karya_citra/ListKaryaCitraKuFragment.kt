package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_citra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.karyasiswa.databinding.FragmentListKaryaCitrakuBinding
import tedc.alian.karyasiswa.presentation.adapters.paging.ListKaryaCitraAdapter
import tedc.alian.karyasiswa.presentation.adapters.paging.load_state.ListKaryaCitraLoadStateAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.ListKaryaCitraViewModel
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast
import javax.inject.Inject


@AndroidEntryPoint
class ListKaryaCitraKuFragment : BaseFragment<FragmentListKaryaCitrakuBinding>(),
    ListKaryaCitraAdapter.OnItemClickListener {

    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel: ListKaryaCitraViewModel by viewModels()
    private val adapter: ListKaryaCitraAdapter by lazy {
        ListKaryaCitraAdapter(this, imageLoader)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListKaryaCitrakuBinding {
        return FragmentListKaryaCitrakuBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.getAllKaryaCitraKu()
        repeatOnViewLifecycle {
            viewModel.getAllKaryaCitraKu().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    override fun onItemClick(karyaCitra: KaryaCitraDto) {
        val action =
            ListKaryaCitraKuFragmentDirections.actionListKaryaCitraKuFragmentToDetailKaryaCitraFragment(
                karyaCitra.id
            )
        findNavController().navigate(action)
    }

    override fun onLongItemClick(karyaCitra: KaryaCitraDto): Boolean {
        val action =
            ListKaryaCitraKuFragmentDirections.actionListKaryaCitraKuFragmentToAksiKaryaCitraBottomSheet(
                karyaCitra
            )
        findNavController().navigate(action)
        return true
    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        manager.isSmoothScrollbarEnabled = true
        binding.rvListKaryaCitra.apply {
            layoutManager = manager
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            itemAnimator = DefaultItemAnimator()
            this.adapter = this@ListKaryaCitraKuFragment.adapter.withLoadStateHeaderAndFooter(
                header = ListKaryaCitraLoadStateAdapter {
                    this@ListKaryaCitraKuFragment.adapter.retry()
                },
                footer = ListKaryaCitraLoadStateAdapter {
                    this@ListKaryaCitraKuFragment.adapter.retry()
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
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun onDelete() {
        adapter.refresh()
    }

    private fun showLoading() {
        binding.include.loading.isVisible = true
        binding.rvListKaryaCitra.isVisible = false
        binding.emptyData.root.isVisible = false
    }

    private fun hideLoading() {
        binding.include.loading.isVisible = false
        binding.rvListKaryaCitra.isVisible = true
    }
}