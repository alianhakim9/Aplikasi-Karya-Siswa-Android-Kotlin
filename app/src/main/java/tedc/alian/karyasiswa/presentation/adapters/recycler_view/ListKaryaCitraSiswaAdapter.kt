package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import coil.transform.RoundedCornersTransformation
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemKaryaSiswaCitraBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView

class ListKaryaCitraSiswaAdapter(
    private val listener: InterfaceListener
) : BaseRecyclerView<KaryaCitraDto, ItemKaryaSiswaCitraBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemKaryaSiswaCitraBinding {
        return ItemKaryaSiswaCitraBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemKaryaSiswaCitraBinding, item: KaryaCitraDto) {
        if (item.format == "mp4") {
            binding.karya.load(R.drawable.img_is_video) {
                transformations(RoundedCornersTransformation(16f))
            }
        } else {
            binding.karya.load(item.karya) {
                crossfade(100)
                error(ColorDrawable(Color.RED))
                transformations(RoundedCornersTransformation(16f))
            }
        }
        binding.titleKarya.text = item.namaKarya
        binding.excerpt.text = item.excerpt
        binding.uploaded.text = item.createdAt
        binding.jumlahKomentar.text = "${item.komentar?.size} Komentar"
        binding.jumlahLike.text = item.jumlahLike
        binding.root.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    override fun areContentsTheSame(oldItem: KaryaCitraDto, newItem: KaryaCitraDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: KaryaCitraDto, newItem: KaryaCitraDto): Boolean {
        return oldItem.id == newItem.id
    }

    interface InterfaceListener {
        fun onItemClick(item: KaryaCitraDto)
    }
}