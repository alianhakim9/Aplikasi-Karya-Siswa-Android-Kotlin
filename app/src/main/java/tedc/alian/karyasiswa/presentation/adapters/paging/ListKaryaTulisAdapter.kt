package tedc.alian.karyasiswa.presentation.adapters.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tedc.alian.data.remote.api.karya.KaryaTulisDto
import tedc.alian.karyasiswa.R
import tedc.alian.karyasiswa.databinding.ItemKaryaTulisBinding
import tedc.alian.utils.helper.loadProfilePicture

class ListKaryaTulisAdapter(
    private val itemClick: OnItemClickListener
) :
    PagingDataAdapter<KaryaTulisDto, ListKaryaTulisAdapter.KaryaViewHolder>(KaryaTulisDiffCallBack()) {

    inner class KaryaViewHolder(val binding: ItemKaryaTulisBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: KaryaTulisDto) {
            binding.root.setOnClickListener {
                itemClick.onItemClick(item)
            }
            binding.root.setOnLongClickListener {
                itemClick.onLongItemClick(item)
            }
            with(binding) {
                tvJudulKarya.text = item.judulKarya
                tvExcerpt.text = item.excerpt
                tvOlehSiswa.text = "Oleh: ${item.siswa.namaLengkap}"
                tvJumlahLike.text = "${item.jumlahLike} Suka"
                binding.imgAvatar.loadProfilePicture(
                    imageUrl = item.siswa.fotoProfil,
                    placeholderRes = R.drawable.ic_avatar,
                    imageResource = R.drawable.ic_avatar
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KaryaViewHolder {
        val binding =
            ItemKaryaTulisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KaryaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KaryaViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class KaryaTulisDiffCallBack : DiffUtil.ItemCallback<KaryaTulisDto>() {
        override fun areItemsTheSame(oldItem: KaryaTulisDto, newItem: KaryaTulisDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: KaryaTulisDto, newItem: KaryaTulisDto): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClick(karyaTulis: KaryaTulisDto)

        fun onLongItemClick(karyaTulis: KaryaTulisDto): Boolean
    }
}