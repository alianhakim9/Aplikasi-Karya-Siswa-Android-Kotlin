package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.ImageLoader
import coil.load
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemKaryaBelumDivalidasiBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView

class ListKaryaYangBelumDiValidasiAdapter(
    private val itemClick: InterfaceListener,
    private val imageLoader: ImageLoader
) : BaseRecyclerView<KaryaCitraDto, ItemKaryaBelumDivalidasiBinding>() {

    interface InterfaceListener {
        fun onItemClick(karyaCitra: KaryaCitraDto)
        fun validatedKarya(karyaCitra: KaryaCitraDto)
        fun onTolakKarya(karyaCitra: KaryaCitraDto)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemKaryaBelumDivalidasiBinding {
        return ItemKaryaBelumDivalidasiBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemKaryaBelumDivalidasiBinding, item: KaryaCitraDto) {
        if (item.format == "mp4") {
            binding.karya.load(R.drawable.img_is_video)
        } else {
            binding.karya.load(item.karya)
        }
        binding.tvNamaKarya.text = item.namaKarya
        binding.tvNamaSiswa.text = "Oleh: ${item.siswa.namaLengkap}"
        binding.root.setOnClickListener {
            itemClick.onItemClick(item)
        }
        binding.btnTerima.setOnClickListener {
            itemClick.validatedKarya(item)
        }
        binding.btnTolak.setOnClickListener {
            itemClick.onTolakKarya(item)
        }
    }

    override fun areContentsTheSame(oldItem: KaryaCitraDto, newItem: KaryaCitraDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: KaryaCitraDto, newItem: KaryaCitraDto): Boolean {
        return oldItem.id == newItem.id
    }
}