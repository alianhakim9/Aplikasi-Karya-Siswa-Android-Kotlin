package tedc.alian.karyasiswa.presentation.adapters.paging

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.ImageLoader
import coil.load
import coil.memory.MemoryCache
import tedc.alian.data.remote.api.karya.KaryaCitraDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemKaryaCitraBinding
import tedc.alian.utils.helper.loadProfilePicture

class ListKaryaCitraAdapter(
    private val itemClick: OnItemClickListener,
    private val imageLoader: ImageLoader
) :
    PagingDataAdapter<KaryaCitraDto, ListKaryaCitraAdapter.KaryaViewHolder>(KaryaCitraDiffCallBack()) {

    inner class KaryaViewHolder(val binding: ItemKaryaCitraBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KaryaCitraDto) {
            var placeholder: MemoryCache.Key? = null
            if (item.format == "mp4") {
                binding.karya.load(R.drawable.img_is_video) {
                    error(ColorDrawable(Color.RED))
                }
            } else {
                binding.karya.load(item.karya) {
                    crossfade(100)
                    error(ColorDrawable(Color.RED))
                }
            }
            binding.namaSiswa.text = item.siswa.namaLengkap
            binding.titleKarya.text = item.namaKarya
            binding.excerpt.text = item.excerpt
            binding.uploaded.text = item.createdAt
            binding.fotoProfil.loadProfilePicture(
                item.siswa.fotoProfil,
                imageResource = R.drawable.ic_avatar,
                placeholderRes = R.drawable.ic_avatar
            )
            binding.jumlahKomentar.text = "${item.komentar?.size} Komentar"
            binding.jumlahLike.text = item.jumlahLike
            binding.uploaded.text = item.createdAt
            binding.root.setOnClickListener {
                itemClick.onItemClick(item)
            }
            binding.root.setOnLongClickListener {
                itemClick.onLongItemClick(item)
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
            ItemKaryaCitraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KaryaViewHolder(binding)
    }

    class KaryaCitraDiffCallBack : DiffUtil.ItemCallback<KaryaCitraDto>() {
        override fun areItemsTheSame(oldItem: KaryaCitraDto, newItem: KaryaCitraDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: KaryaCitraDto, newItem: KaryaCitraDto): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClick(karyaCitra: KaryaCitraDto)

        fun onLongItemClick(karyaCitra: KaryaCitraDto): Boolean
    }
}
