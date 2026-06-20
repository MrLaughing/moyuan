package com.mrlaughing.moyuan.ui.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.data.model.GrowthLevel
import com.mrlaughing.moyuan.data.model.PlantPath
import com.mrlaughing.moyuan.data.model.WitherStage
import com.mrlaughing.moyuan.databinding.ItemPlantBinding

class PlantCardAdapter(
    private val onClick: (PlantStateEntity) -> Unit = {}
) : RecyclerView.Adapter<PlantCardAdapter.PlantViewHolder>() {

    private var items: List<PlantStateEntity> = emptyList()

    inner class PlantViewHolder(val binding: ItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val b = ItemPlantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlantViewHolder(b)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val p = items[position]
        val path = PlantPath.fromString(p.path)
        val level = GrowthLevel.fromString(p.level)
        val stage = WitherStage.fromString(p.witherStage)
        holder.binding.plantName.text = "${path.displayName}·${p.plantId.substringAfterLast('_')}"
        holder.binding.plantLevel.text = "等级：${level.displayName} · 枯萎：${stage.displayName}"
        holder.binding.plantMinutes.text = "累计 ${p.accumulatedMinutes} 分钟"
        holder.binding.root.setOnClickListener { onClick(p) }
    }

    override fun getItemCount() = items.size

    fun submitList(new: List<PlantStateEntity>) {
        items = new
        notifyDataSetChanged()
    }
}
