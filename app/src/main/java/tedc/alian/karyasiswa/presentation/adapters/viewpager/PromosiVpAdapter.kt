package tedc.alian.karyasiswa.presentation.adapters.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import tedc.alian.data.remote.dto.PromosiDto
import tedc.alian.karyasiswa.databinding.ItemPromosiHalamanUtamaBinding

class PromosiVpAdapter(
    val context: Context
) :
    RecyclerView.Adapter<PromosiVpAdapter.PromosiVpViewHolder>() {

    private var data = emptyList<PromosiDto>()

    inner class PromosiVpViewHolder(private val binding: ItemPromosiHalamanUtamaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PromosiDto) {
            binding.ivPromosi.load(item.gambar) {
                crossfade(true)
                transformations(RoundedCornersTransformation(20f))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromosiVpViewHolder {
        val binding =
            ItemPromosiHalamanUtamaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PromosiVpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromosiVpViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    fun setData(newData: List<PromosiDto>) {
        val diffResult = DiffUtil.calculateDiff(ExampleDataDiffCallback(data, newData))
        data = newData
        diffResult.dispatchUpdatesTo(this)
    }

    class ExampleDataDiffCallback(
        private val oldList: List<PromosiDto>,
        private val newList: List<PromosiDto>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}

