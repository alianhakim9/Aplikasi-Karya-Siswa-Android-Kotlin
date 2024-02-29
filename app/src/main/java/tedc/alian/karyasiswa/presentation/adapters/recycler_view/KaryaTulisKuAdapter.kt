package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemKaryaTuliskuBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView
import tedc.alian.utils.helper.loadProfilePicture

class KaryaTulisKuAdapter : BaseRecyclerView<KaryaTulisDto, ItemKaryaTuliskuBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemKaryaTuliskuBinding {
        return ItemKaryaTuliskuBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemKaryaTuliskuBinding, item: KaryaTulisDto) {
        binding.tvJudulKarya.text = item.judulKarya
        binding.tvOlehSiswa.text = "Oleh ${item.siswa.namaLengkap}"
        binding.tvJumlahLike.text = "${item.jumlahLike} suka"
        binding.imgAvatar.loadProfilePicture(
            imageUrl = item.siswa.fotoProfil,
            imageResource = R.drawable.ic_avatar,
            placeholderRes = R.drawable.ic_avatar
        )
    }

    override fun areContentsTheSame(oldItem: KaryaTulisDto, newItem: KaryaTulisDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: KaryaTulisDto, newItem: KaryaTulisDto): Boolean {
        return oldItem.id == newItem.id
    }

}