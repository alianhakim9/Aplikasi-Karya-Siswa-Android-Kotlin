package tedc.alian.karyasiswa.presentation.adapters.recycler_view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import coil.load
import tedc.alian.data.remote.api.karya.NotifikasiDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemNotifikasiGuruBinding
import tedc.alian.karyasiswa.presentation.adapters.recycler_view.abstraksi.BaseRecyclerView

class ListNotifikasiGuruAdapter(
    val context: Context,
    private val listener: InterfaceListener
) : BaseRecyclerView<NotifikasiDto, ItemNotifikasiGuruBinding>() {


    interface InterfaceListener {
        fun onItemClick(notifikasiDto: NotifikasiDto)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemNotifikasiGuruBinding {
        return ItemNotifikasiGuruBinding.inflate(inflater, parent, false)
    }

    override fun bindItem(binding: ItemNotifikasiGuruBinding, item: NotifikasiDto) {
        if (item.karyaCitra.format == "mp4") {
            binding.thumbnail.load(R.drawable.img_is_video)
        } else {
            binding.thumbnail.load(item.karyaCitra.karya)
        }
        binding.notificationTitle.text = item.karyaCitra.namaKarya
        binding.tvUser.text = item.siswa.namaLengkap
        binding.tvTime.text = item.createdAt
        binding.notificationText.text = item.desc
        if (item.notifikasi == "1") {
            binding.root.setBackgroundColor(
                ContextCompat.getColor(context, R.color.secondary_100)
            )
        }
        binding.root.setOnClickListener {
            listener.onItemClick(item)
        }

    }

    override fun areContentsTheSame(oldItem: NotifikasiDto, newItem: NotifikasiDto): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: NotifikasiDto, newItem: NotifikasiDto): Boolean {
        return oldItem.id == newItem.id
    }
}