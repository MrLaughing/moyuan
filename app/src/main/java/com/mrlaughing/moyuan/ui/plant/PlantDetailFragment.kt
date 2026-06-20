package com.mrlaughing.moyuan.ui.plant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mrlaughing.moyuan.data.model.GrowthLevel
import com.mrlaughing.moyuan.data.model.PlantPath
import com.mrlaughing.moyuan.data.model.WitherStage
import com.mrlaughing.moyuan.R
import com.mrlaughing.moyuan.databinding.FragmentPlantDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantDetailFragment : Fragment() {

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observePlant().observe(viewLifecycleOwner) { plant ->
            if (plant == null) {
                binding.titlePlantDetail.text = "植物详情"
                binding.plantIdText.text = "暂无植物数据，请先同步阅读"
                binding.plantIdText.visibility = View.VISIBLE
                binding.plantImage.visibility = View.GONE
                binding.plantStatsText.visibility = View.GONE
                binding.growthProgressBar.progress = 0
                binding.witherProgressBar.progress = 0
                return@observe
            }

            binding.plantImage.visibility = View.VISIBLE

            val path = PlantPath.fromString(plant.path)
            binding.titlePlantDetail.text = "${path.displayName}·${plant.plantId.substringAfterLast('_')}"
            binding.plantIdText.text = "${path.displayName}·${plant.plantId.substringAfterLast('_')}"

            val stage = WitherStage.fromString(plant.witherStage)
            val level = GrowthLevel.fromString(plant.level)

            // 根据路径设置不同图标
            binding.plantImage.setImageResource(getIconForPath(path))
            // 废墟植物降低透明度
            binding.plantImage.alpha = if (stage == WitherStage.RUIN) 0.3f else 1.0f

            binding.plantStatsText.text = "等级 ${level.displayName} · 枯萎 ${stage.displayName} · 累计 ${plant.accumulatedMinutes} 分钟"
            binding.plantStatsText.visibility = View.VISIBLE

            // 生长进度
            val (progress, remaining) = calcGrowthProgress(plant.accumulatedMinutes, level)
            binding.growthDetailText.text = "当前：${level.displayName} · 距下一级还需 $remaining 分钟"
            binding.growthProgressBar.progress = progress

            // 枯萎进度
            val witherProgress = calcWitherProgress(stage)
            binding.witherDetailText.text = if (stage == WitherStage.NONE) {
                "状态良好，继续阅读保持健康"
            } else {
                "当前状态：${stage.displayName} · 不阅读将逐渐枯萎"
            }
            binding.witherProgressBar.progress = witherProgress

            // 详细信息
            binding.unlockDateText.text = "解锁日期：${plant.unlockedDate}"
            binding.lastWateredText.text = "上次浇水：${plant.lastWateredDate ?: "—"}"
            binding.pathProgressText.text = "路径：${path.displayName} · 墨滴 ${plant.pathProgress}"

            // 废墟状态
            val isRuined = viewModel.isRuined(plant)
            if (isRuined) {
                binding.ruinsInfoText.visibility = View.VISIBLE
                binding.ruinsInfoText.text = "此植物已成废墟 · 重建次数 ${plant.ruinsRebuildCount}\n点击下方按钮可重建植物，从种子重新开始生长"
                binding.rebuildButton.visibility = View.VISIBLE
            } else {
                binding.ruinsInfoText.visibility = View.GONE
                binding.rebuildButton.visibility = View.GONE
            }
        }

        viewModel.rebuildMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.consumeRebuildMessage()
            }
        }

        binding.rebuildButton.setOnClickListener {
            viewModel.rebuildRuins()
        }
    }

    private fun getIconForPath(path: PlantPath): Int {
        return when (path) {
            PlantPath.JIMO -> R.drawable.ic_path_jimo
            PlantPath.BINGZHU -> R.drawable.ic_path_bingzhu
            PlantPath.SUIHAN -> R.drawable.ic_path_suihan
            PlantPath.XUNFANG -> R.drawable.ic_path_xunfang
            PlantPath.HIDDEN -> R.drawable.ic_path_hidden
        }
    }

    private fun calcGrowthProgress(minutes: Int, currentLevel: GrowthLevel): Pair<Int, Int> {
        val sorted = GrowthLevel.entries.sortedBy { it.thresholdMinutes }
        val currentIdx = sorted.indexOf(currentLevel)
        val next = sorted.getOrNull(currentIdx + 1)
        return if (next != null) {
            val range = next.thresholdMinutes - currentLevel.thresholdMinutes
            val progress = minutes - currentLevel.thresholdMinutes
            val pct = (progress.toFloat() / range * 100).toInt().coerceIn(0, 100)
            val remaining = next.thresholdMinutes - minutes
            pct to remaining
        } else {
            100 to 0
        }
    }

    private fun calcWitherProgress(stage: WitherStage): Int {
        return when (stage) {
            WitherStage.NONE -> 0
            WitherStage.FADE -> 20
            WitherStage.WITHER -> 40
            WitherStage.SEVERE -> 65
            WitherStage.DEAD -> 85
            WitherStage.RUIN -> 100
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
