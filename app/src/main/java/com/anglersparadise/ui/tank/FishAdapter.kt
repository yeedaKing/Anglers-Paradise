// app/src/main/java/com/anglersparadise/ui/tank/FishAdapter.kt

package com.anglersparadise.ui.tank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anglersparadise.databinding.ItemFishBinding
import com.anglersparadise.domain.model.Fish

class FishAdapter(
    private val onClick: (Fish) -> Unit
) : ListAdapter<Fish, FishAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Fish>() {
        override fun areItemsTheSame(oldItem: Fish, newItem: Fish) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Fish, newItem: Fish) = oldItem == newItem
    }

    inner class VH(private val b: ItemFishBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(f: Fish) {
            b.title.text = f.species
            b.subtitle.text = "ID ${f.id} • size ${f.size}"
            b.root.setOnClickListener { onClick(f) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        return VH(ItemFishBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
