package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.ImageLoader
import coil.load
import coil.size.Size
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemKaryaCitrakuBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView


class KaryaCitraKuAdapter(
    private val imageLoader: ImageLoader
) : BaseRecyclerView<KaryaCitraDto, ItemKaryaCitrakuBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemKaryaCitrakuBinding {
        return ItemKaryaCitrakuBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemKaryaCitrakuBinding, item: KaryaCitraDto) {
        if (item.format == "mp4") {
            binding.karya.load(R.drawable.img_is_video)
        } else {
            binding.karya.load(item.karya) {
                crossfade(100)
                error(ColorDrawable(Color.RED))
            }
        }
        when (item.status) {
            "Disetujui" -> {
                binding.icValidasi.load(R.drawable.ic_validated)
                binding.tvValidated.text = "Disetujui"
            }
            "Menunggu validasi" -> {
                binding.icValidasi.load(R.drawable.ic_menunggu_validasi)
                binding.tvValidated.text = "Menunggu validasi"
            }
            "Ditolak" -> {
                binding.icValidasi.load(R.drawable.ic_not_validated)
                binding.tvValidated.text = "Ditolak"
            }
        }
    }

    override fun areContentsTheSame(oldItem: KaryaCitraDto, newItem: KaryaCitraDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: KaryaCitraDto, newItem: KaryaCitraDto): Boolean {
        return oldItem.id == newItem.id
    }
}