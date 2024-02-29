package tedc.alian.karyasiswa.presentation.fragment.karya.karya_citra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
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
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentListKaryaCitraBinding
import tedc.alian.karyasiswa.presentation.adapters.paging.ListKaryaCitraAdapter
import tedc.alian.karyasiswa.presentation.adapters.paging.load_state.ListKaryaCitraLoadStateAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.ListKaryaCitraViewModel
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast
import javax.inject.Inject


@AndroidEntryPoint
class ListKaryaCitraFragment : BaseFragment<FragmentListKaryaCitraBinding>(),
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
    ): FragmentListKaryaCitraBinding {
        return FragmentListKaryaCitraBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiSetup()
        viewModel.getItems()
        uiAction()
        observer()
    }

    private fun uiSetup() {
        setupRecyclerView()
    }

    private fun uiAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnRetry.setOnClickListener {
            adapter.retry()
        }
        val searchView = binding.toolbar.menu.findItem(R.id.search_view).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                repeatOnViewLifecycle {
                    viewModel.filterKaryaCitra(newText.orEmpty()).collect { pagingData ->
                        adapter.submitData(pagingData)
                        binding.rvListKaryaCitra.scrollToPosition(0)
                        binding.emptyData.root.isVisible = adapter.itemCount == 0
                        binding.rvListKaryaCitra.isVisible = adapter.itemCount != 0
                    }
                }
                return true
            }
        })
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.getItems().collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                    binding.rvListKaryaCitra.scrollToPosition(0)
                    binding.emptyData.root.isVisible = adapter.itemCount == 0
                    binding.rvListKaryaCitra.isVisible = adapter.itemCount != 0
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        manager.isSmoothScrollbarEnabled = true
        binding.rvListKaryaCitra.apply {
            layoutManager = manager
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(
                DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
            )
            this.adapter = this@ListKaryaCitraFragment.adapter.withLoadStateHeaderAndFooter(
                header = ListKaryaCitraLoadStateAdapter {
                    this@ListKaryaCitraFragment.adapter.retry()
                },
                footer = ListKaryaCitraLoadStateAdapter {
                    this@ListKaryaCitraFragment.adapter.retry()
                }
            )
        }
        adapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    binding.include.loading.isVisible = true
                    binding.rvListKaryaCitra.isVisible = false
                }

                is LoadState.NotLoading -> {
                    binding.include.loading.isVisible = false
                    binding.rvListKaryaCitra.isVisible = true
                }

                is LoadState.Error -> {
                    binding.include.loading.isVisible = true
                    binding.rvListKaryaCitra.isVisible = false
                    showToast("terjadi kesalahan saat mengambil data")
                }
            }
        }
    }

    override fun onItemClick(karyaCitra: KaryaCitraDto) {
        val action =
            ListKaryaCitraFragmentDirections.actionListKaryaCitraFragmentToDetailKaryaCitraFragment(
                karyaCitra.id
            )
        findNavController().navigate(action)
    }

    override fun onLongItemClick(karyaCitra: KaryaCitraDto): Boolean {
        return false
    }
}