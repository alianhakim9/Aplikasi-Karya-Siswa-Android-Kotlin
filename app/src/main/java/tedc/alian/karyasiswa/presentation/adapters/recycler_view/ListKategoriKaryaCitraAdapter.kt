package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import tedc.alian.data.remote.api.karya.KategoriKaryaDto
import tedc.alian.karyasiswa.databinding.ItemKategoriKaryaBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView

class ListKategoriKaryaCitraAdapter(
    private val listener: InterfaceListener
) : BaseRecyclerView<KategoriKaryaDto, ItemKategoriKaryaBinding>() {


    interface InterfaceListener {
        fun onItemClick(item: KategoriKaryaDto)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemKategoriKaryaBinding {
        return ItemKategoriKaryaBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemKategoriKaryaBinding, item: KategoriKaryaDto) {
        binding.tvNamaKategori.text = item.namaKategori
        binding.btnDelete.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    override fun areContentsTheSame(oldItem: KategoriKaryaDto, newItem: KategoriKaryaDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: KategoriKaryaDto, newItem: KategoriKaryaDto): Boolean {
        return oldItem.id == newItem.id
    }
}

