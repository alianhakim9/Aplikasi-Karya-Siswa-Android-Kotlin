package tedc.alian.karyasiswa.presentation.fragment.siswa.kelola_karya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentListKaryaKuBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.KaryaCitraKuAdapter
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.KaryaTulisKuAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.karyasiswa.presentation.viewmodel.SiswaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showToast
import javax.inject.Inject

@AndroidEntryPoint
class ListKaryaKuFragment : BaseFragment<FragmentListKaryaKuBinding>() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel: KaryaViewModel by viewModels()
    private val siswaViewModel: SiswaViewModel by viewModels()
    private val adapter: KaryaTulisKuAdapter by lazy {
        KaryaTulisKuAdapter()
    }
    private val adapterKaryaVisual: KaryaCitraKuAdapter by lazy {
        KaryaCitraKuAdapter(imageLoader)
    }
    private var karyaTulisError = false
    private var karyaCitraError = false
    private var countKaryaError = false

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListKaryaKuBinding {
        return FragmentListKaryaKuBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        siswaViewModel.getProfil()
        viewModel.getKaryaTulisKu()
        viewModel.getKaryaCitraKu()
        viewModel.getCountKarya()
        dataObserver()
        binding.titleKaryaCitra.title.text = "Karya Visual"
        binding.titleKaryaTulis.title.text = "Karya Tulis"
        binding.titleKaryaCitra.action.setOnClickListener {
            findNavController().navigate(R.id.action_listKaryaKuFragment_to_listKaryaCitraKuFragment)
        }
        binding.titleKaryaTulis.action.setOnClickListener {
            findNavController().navigate(R.id.action_listKaryaKuFragment_to_listKaryaTulisKuFragment)
        }
    }

    private fun setupRecyclerView() {
        binding.karyaTulisRecyclerView.adapter = adapter
        binding.karyaTulisRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.karyaCitraRecyclerView.adapter = adapterKaryaVisual
        binding.karyaCitraRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun dataObserver() {
        repeatOnViewLifecycle {
            launch {
                lifecycleScope.launchWhenStarted {
                    siswaViewModel.profil.collect {
                        when (it) {
                            is Resource.Success -> {
                                with(binding) {
                                    tvNama.text = it.data?.namaLengkap
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
            launch {
                viewModel.karya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Error -> {
                            hideLoading()
                            showToast(resource.throwable.message.toString())
                            karyaCitraError = true
                            showMultipleError()
                        }
                        is Resource.Success -> {
                            hideLoading()
                            adapterKaryaVisual.submitList(resource.data)
                            if (adapterKaryaVisual.itemCount == 0) {
                                binding.emptyKaryaCitra.root.visibility = View.VISIBLE
                                binding.karyaCitraRecyclerView.visibility = View.GONE
                            } else {
                                binding.emptyKaryaCitra.root.visibility = View.GONE
                                binding.karyaCitraRecyclerView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
            launch {
                viewModel.karyaTulisDtoKu.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Error -> {
                            hideLoading()
                            showToast(resource.throwable.message.toString())
                            karyaTulisError = true
                            showMultipleError()
                        }
                        is Resource.Success -> {
                            hideLoading()
                            adapter.submitList(resource.data)
                            if (adapter.itemCount == 0) {
                                binding.emptyKaryaTulis.root.visibility = View.VISIBLE
                                binding.karyaTulisRecyclerView.visibility = View.GONE
                            } else {
                                binding.emptyKaryaTulis.root.visibility = View.GONE
                                binding.karyaTulisRecyclerView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
            launch {
                viewModel.countKarya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Error -> {
                            hideLoading()
                            countKaryaError = true
                            showToast(resource.throwable.message.toString())
                        }
                        is Resource.Success -> {
                            hideLoading()
                            binding.countKaryaCitra.text = resource.data.countKaryaCitra.toString()
                            binding.countKaryaTulis.text = resource.data.countKaryaTulis.toString()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.include.loading.visibility = View.VISIBLE
        binding.container.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.include.loading.visibility = View.GONE
        binding.container.visibility = View.VISIBLE
    }

    private fun showMultipleError() {
        if (countKaryaError && karyaTulisError && karyaCitraError) {
            showLoading()
        }
    }
}