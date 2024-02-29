package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import tedc.alian.data.remote.dto.PromosiDto
import tedc.alian.karyasiswa.databinding.ItemPromosiByTimBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView
import tedc.alian.utils.helper.loadPromosi

class ListPromosiByTimAdapter(
    private val listener: InterfaceListener
) : BaseRecyclerView<PromosiDto, ItemPromosiByTimBinding>() {

    interface InterfaceListener {

        fun onEdit(item: PromosiDto)

        fun onDelete(item: PromosiDto)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemPromosiByTimBinding {
        return ItemPromosiByTimBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemPromosiByTimBinding, item: PromosiDto) {
        binding.promosi.loadPromosi(item.gambar)
        binding.tvNamaPromosi.text = item.namaPromosi
        binding.tvNamaTim.text = "Oleh : ${item.timPPDB.namaLengkap}"
        binding.btnEdit.setOnClickListener {
            listener.onEdit(item)
        }
        binding.btnDelete.setOnClickListener {
            listener.onDelete(item)
        }
    }

    override fun areContentsTheSame(oldItem: PromosiDto, newItem: PromosiDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: PromosiDto, newItem: PromosiDto): Boolean {
        return oldItem.id == newItem.id
    }
}