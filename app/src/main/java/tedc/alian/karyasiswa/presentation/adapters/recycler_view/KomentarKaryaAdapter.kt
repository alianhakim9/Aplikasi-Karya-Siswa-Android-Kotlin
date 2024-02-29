package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import tedc.alian.data.remote.api.karya.KomentarDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemKomentarBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView
import tedc.alian.utils.helper.loadProfilePicture

class KomentarKaryaAdapter : BaseRecyclerView<KomentarDto, ItemKomentarBinding>() {

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemKomentarBinding {
        return ItemKomentarBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemKomentarBinding, item: KomentarDto) {
        setKomentarByStatus(item, binding)
    }

    override fun areContentsTheSame(oldItem: KomentarDto, newItem: KomentarDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: KomentarDto, newItem: KomentarDto): Boolean {
        return oldItem.id == newItem.id
    }

    private fun setDataToView(
        imageUrl: String? = null,
        @LayoutRes imageResource: Int? = null,
        userName: String,
        binding: ItemKomentarBinding
    ) {
        binding.fotoProfil.loadProfilePicture(
            imageUrl,
            imageResource,
            R.drawable.ic_avatar
        )
        binding.namaUser.text = userName
    }

    private fun setKomentarByStatus(item: KomentarDto, binding: ItemKomentarBinding) {
        if (item.guru != null) {
            val guru = item.guru!!
            setDataToView(
                imageUrl = guru.fotoProfil,
                userName = guru.namaLengkap,
                binding = binding,
            )
        } else if (item.siswa != null) {
            val siswa = item.siswa!!
            setDataToView(
                imageUrl = siswa.fotoProfil,
                userName = siswa.namaLengkap,
                binding = binding
            )
        } else {
            setDataToView(
                imageResource = R.drawable.ic_avatar,
                userName = item.user.email,
                binding = binding
            )
        }
        binding.uploaded.text = item.createdAt
        binding.komentar.text = item.komentar
    }
}