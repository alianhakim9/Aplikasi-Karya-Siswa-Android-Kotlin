package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya.karya_tulis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.karyasiswa.databinding.FragmentListKaryaTuliskuBinding
import tedc.alian.karyasiswa.presentation.adapters.paging.ListKaryaTulisAdapter
import tedc.alian.karyasiswa.presentation.adapters.paging.load_state.ListKaryaTulisLoadStateAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.ListKaryaTulisViewModel
import tedc.alian.utils.helper.showToast

@AndroidEntryPoint
class ListKaryaTulisKuFragment : BaseFragment<FragmentListKaryaTuliskuBinding>(),
    ListKaryaTulisAdapter.OnItemClickListener {

    private val viewModel: ListKaryaTulisViewModel by viewModels()
    private val adapter: ListKaryaTulisAdapter by lazy {
        ListKaryaTulisAdapter(this)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListKaryaTuliskuBinding {
        return FragmentListKaryaTuliskuBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.getAllKaryaTulisKu()
        lifecycleScope.launchWhenStarted {
            viewModel.getAllKaryaTulisKu().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        binding.rvKaryaTulis.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            this.adapter = this@ListKaryaTulisKuFragment.adapter.withLoadStateHeaderAndFooter(
                header = ListKaryaTulisLoadStateAdapter {
                    this@ListKaryaTulisKuFragment.adapter.retry()
                },
                footer = ListKaryaTulisLoadStateAdapter {
                    this@ListKaryaTulisKuFragment.adapter.retry()
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
        binding.btnRetry.setOnClickListener {
            adapter.retry()
        }
    }

    override fun onItemClick(karyaTulis: KaryaTulisDto) {
        val action =
            ListKaryaTulisKuFragmentDirections.actionListKaryaTulisKuFragmentToDetailKaryaTulisFragment(
                karyaTulis.id
            )
        findNavController().navigate(action)
    }

    override fun onLongItemClick(karyaTulis: KaryaTulisDto): Boolean {
        val action =
            ListKaryaTulisKuFragmentDirections.actionListKaryaTulisKuFragmentToAksiKaryaTulisBottomSheet(
                karyaTulis
            )
        findNavController().navigate(action)
        return true
    }

    fun onDelete() {
        adapter.refresh()
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
}