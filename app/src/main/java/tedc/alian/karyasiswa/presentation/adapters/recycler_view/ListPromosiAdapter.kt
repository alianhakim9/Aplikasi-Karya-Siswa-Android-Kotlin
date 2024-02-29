package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import tedc.alian.data.remote.dto.PromosiDto
import tedc.alian.karyasiswa.databinding.ItemPromosiBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView
import tedc.alian.utils.helper.loadPromosi

class ListPromosiAdapter : BaseRecyclerView<PromosiDto, ItemPromosiBinding>() {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemPromosiBinding {
        return ItemPromosiBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemPromosiBinding, item: PromosiDto) {
        binding.promosi.loadPromosi(item.gambar)
        binding.tvNamaPromosi.text = item.namaPromosi
        binding.tvNamaTim.text = "Oleh : ${item.timPPDB.namaLengkap}"
    }

    override fun areContentsTheSame(oldItem: PromosiDto, newItem: PromosiDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: PromosiDto, newItem: PromosiDto): Boolean {
        return oldItem.id == newItem.id
    }
}