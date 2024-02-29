package tedc.alian.karyasiswa.presentation.adapters.paging

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.size.Size
import tedc.alian.data.remote.api.karya.KaryaCitraDitolakDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemKaryaCitraDitolakBinding

class ListKaryaCitraDitolakAdapter(
    private val itemClick: OnItemClickListener,
    private val imageLoader: ImageLoader
) :
    PagingDataAdapter<KaryaCitraDitolakDto, ListKaryaCitraDitolakAdapter.KaryaViewHolder>(
        KaryaCitraDiffCallBack()
    ) {

    inner class KaryaViewHolder(val binding: ItemKaryaCitraDitolakBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KaryaCitraDitolakDto) {
            if (item.format == "mp4") {
                binding.karya.load(R.drawable.img_is_video)
            } else {
                binding.karya.load(item.karya) {
                    crossfade(100)
                    error(ColorDrawable(Color.RED))
                }
            }
            binding.tvNamaKarya.text = item.namaKarya
            binding.tvCaption.text = item.caption
            binding.btnHapus.setOnClickListener {
                itemClick.onHapus(item)
            }
            binding.btnLihat.setOnClickListener {
                itemClick.onLihat(item)
            }
        }
    }

    override fun onBindViewHolder(holder: KaryaViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KaryaViewHolder {
        val binding =
            ItemKaryaCitraDitolakBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KaryaViewHolder(binding)
    }

    class KaryaCitraDiffCallBack : DiffUtil.ItemCallback<KaryaCitraDitolakDto>() {
        override fun areItemsTheSame(
            oldItem: KaryaCitraDitolakDto,
            newItem: KaryaCitraDitolakDto
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: KaryaCitraDitolakDto,
            newItem: KaryaCitraDitolakDto
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onHapus(karyaCitra: KaryaCitraDitolakDto)
        fun onLihat(karyaCitra: KaryaCitraDitolakDto)
    }
}
