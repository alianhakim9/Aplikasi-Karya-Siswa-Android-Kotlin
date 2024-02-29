package tedc.alian.karyasiswa.presentation.fragment.guru.karya.validasi_karya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.FragmentListKaryaBelumDivalidasiBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.ListKaryaYangBelumDiValidasiAdapter
import tedc.alian.karyasiswa.presentation.fragment.BaseFragment
import tedc.alian.karyasiswa.presentation.viewmodel.KaryaViewModel
import tedc.alian.utils.Resource
import tedc.alian.utils.helper.createProgressDialog
import tedc.alian.utils.helper.repeatOnViewLifecycle
import tedc.alian.utils.helper.showDialog
import tedc.alian.utils.helper.showDialogWithNegativeAction
import tedc.alian.utils.helper.showToast
import javax.inject.Inject

@AndroidEntryPoint
class ListKaryaBelumDiValidasiFragment : BaseFragment<FragmentListKaryaBelumDivalidasiBinding>(),
    ListKaryaYangBelumDiValidasiAdapter.InterfaceListener {

    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel: KaryaViewModel by viewModels()
    private val adapter: ListKaryaYangBelumDiValidasiAdapter by lazy {
        ListKaryaYangBelumDiValidasiAdapter(this, imageLoader)
    }
    private val progressDialog: AlertDialog by lazy {
        createProgressDialog(R.layout.reusable_progress_dialog_loading)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListKaryaBelumDivalidasiBinding {
        return FragmentListKaryaBelumDivalidasiBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getData()
        observer()
        uiAction()
    }

    private fun getData() {
        viewModel.getKaryaYangBelumDivalidasi()
    }

    private fun uiAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        binding.rvValidasi.adapter = adapter
        binding.rvValidasi.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observer() {
        repeatOnViewLifecycle {
            launch {
                viewModel.waitingValidateKarya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Error -> {
                            showLoading()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            hideLoading()
                            adapter.submitList(resource.data)
                            binding.emptyData.root.isVisible = adapter.itemCount == 0
                        }
                    }
                }
            }
            launch {
                viewModel.validasiKarya.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> progressDialog.show()
                        is Resource.Error -> {
                            progressDialog.dismiss()
                            showToast(resource.throwable.message.toString())
                        }

                        is Resource.Success -> {
                            progressDialog.dismiss()
                            showToast(resource.data)
                            viewModel.getKaryaYangBelumDivalidasi()
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(karyaCitra: KaryaCitraDto) {
        val action =
            ListKaryaBelumDiValidasiFragmentDirections.actionListWaitingValidasiKaryaFragmentToDetailWaitingKaryaBottomSheet(
                karyaCitra
            )
        findNavController().navigate(action)
    }

    override fun validatedKarya(karyaCitra: KaryaCitraDto) {
        showDialog(
            title = "Validasi karya",
            message = "Acc karya ${karyaCitra.namaKarya}",
            negativeButtonText = "Batal",
            positiveButtonText = "Ya"
        ) {
            viewModel.karyaCitraId.value = karyaCitra.id
            showDialogWithNegativeAction(
                title = "Watermark Logo SMK",
                message = "Apakah anda akan menambahkan watermark pada karya ini?",
                negativeButtonText = "Tidak",
                positiveButtonText = "Ya",
                onPositiveButton = {
                    viewModel.terimaKarya(true)
                },
                onNegativeButton = {
                    viewModel.terimaKarya(false)
                }
            )
        }
    }

    override fun onTolakKarya(karyaCitra: KaryaCitraDto) {
        val action =
            ListKaryaBelumDiValidasiFragmentDirections.actionListWaitingValidasiKaryaFragmentToTolakKaryaFragment(
                karyaCitra.id.toInt()
            )
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getKaryaYangBelumDivalidasi()
    }

    private fun showLoading() {
        binding.include.loading.isVisible = true
        binding.rvValidasi.isVisible = false
        binding.emptyData.root.isVisible = false
    }

    private fun hideLoading() {
        binding.include.loading.isVisible = false
        binding.rvValidasi.isVisible = true
    }
}
