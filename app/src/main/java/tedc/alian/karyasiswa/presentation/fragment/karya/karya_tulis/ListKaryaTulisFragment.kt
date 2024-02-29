package tedc.alian.karyasiswa.presentation.fragment.karya.karya_tulis

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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentListKaryaTulisBinding
import tedc.alian.karyasiswa.presentation.adapters.paging.ListKaryaTulisAdapter
import tedc.alian.karyasiswa.presentation.adapters.paging.load_state.ListKaryaTulisLoadStateAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.ListKaryaTulisViewModel
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast


@AndroidEntryPoint
class ListKaryaTulisFragment : BaseFragment<FragmentListKaryaTulisBinding>(),
    ListKaryaTulisAdapter.OnItemClickListener {

    private val viewModel: ListKaryaTulisViewModel by viewModels()
    private val adapter: ListKaryaTulisAdapter by lazy {
        ListKaryaTulisAdapter(this)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListKaryaTulisBinding {
        return FragmentListKaryaTulisBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        uiAction()
        getData()
    }

    private fun uiAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        val searchView = binding.toolbar.menu.findItem(R.id.search_view).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                repeatOnViewLifecycle {
                    viewModel.filterKaryaTulis(newText.orEmpty()).collectLatest { pagingData ->
                        adapter.submitData(pagingData)
                        binding.rvKaryaTulis.scrollToPosition(0)
                        binding.emptyData.root.isVisible = adapter.itemCount == 0
                        binding.rvKaryaTulis.isVisible = adapter.itemCount != 0
                    }
                }
                return true
            }
        })
    }

    private fun getData() {
        repeatOnViewLifecycle {
            launch {
                viewModel.getItems().collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                    binding.rvKaryaTulis.scrollToPosition(0)
                    binding.emptyData.root.isVisible = adapter.itemCount == 0
                    binding.rvKaryaTulis.isVisible = adapter.itemCount != 0
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvKaryaTulis.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            this.adapter = this@ListKaryaTulisFragment.adapter.withLoadStateHeaderAndFooter(
                header = ListKaryaTulisLoadStateAdapter {
                    this@ListKaryaTulisFragment.adapter.retry()
                },
                footer = ListKaryaTulisLoadStateAdapter {
                    this@ListKaryaTulisFragment.adapter.retry()
                }
            )
        }
        adapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> showLoading()
                is LoadState.Error -> {
                    showToast("Terjadi kesalahan saat mengambil data")
                    showLoading()
                }

                is LoadState.NotLoading -> {
                    hideLoading()
                    binding.emptyData.root.isVisible = adapter.itemCount == 0
                }
            }
        }
        binding.btnRetry.setOnClickListener {
            adapter.retry()
        }
    }

    private fun showLoading() {
        binding.include.loading.isVisible = true
        binding.rvKaryaTulis.isVisible = false
        binding.emptyData.root.isVisible = false
    }

    private fun hideLoading() {
        binding.include.loading.isVisible = false
        binding.rvKaryaTulis.isVisible = true
    }

    override fun onItemClick(karyaTulis: KaryaTulisDto) {
        val action =
            ListKaryaTulisFragmentDirections.actionListKaryaTulisFragmentToDetailKaryaTulisFragment(
                karyaTulis.id
            )
        findNavController().navigate(action)
    }

    override fun onLongItemClick(karyaTulis: KaryaTulisDto): Boolean {
        return false
    }
}