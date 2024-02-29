package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemKaryaTulisBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView
import tedc.alian.utils.helper.loadProfilePicture

class ListKaryaTulisSiswaAdapter(
    private val listener: InterfaceListener
) : BaseRecyclerView<KaryaTulisDto, ItemKaryaTulisBinding>() {

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemKaryaTulisBinding {
        return ItemKaryaTulisBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemKaryaTulisBinding, item: KaryaTulisDto) {
        binding.tvJudulKarya.text = item.judulKarya
        binding.tvOlehSiswa.text = "Oleh: ${item.siswa.namaLengkap}"
        binding.tvJumlahLike.text = "${item.jumlahLike} Suka"
        binding.tvExcerpt.text = item.excerpt
        binding.imgAvatar.loadProfilePicture(
            imageUrl = item.siswa.fotoProfil,
            imageResource = R.drawable.ic_avatar,
            placeholderRes = R.drawable.img_placeholder
        )
        binding.root.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    override fun areContentsTheSame(oldItem: KaryaTulisDto, newItem: KaryaTulisDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: KaryaTulisDto, newItem: KaryaTulisDto): Boolean {
        return oldItem.id == newItem.id
    }

    interface InterfaceListener {
        fun onItemClick(item: KaryaTulisDto)
    }
}