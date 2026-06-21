package com.mrlaughing.moyuan.ui.garden

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import com.mrlaughing.moyuan.R
import com.mrlaughing.moyuan.data.model.Season
import com.mrlaughing.moyuan.data.model.Weather
import com.mrlaughing.moyuan.databinding.FragmentGardenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GardenFragment : Fragment() {

    private var _binding: FragmentGardenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GardenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGardenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            binding.gardenView.setPlants(plants)
            val empty = plants.isEmpty()
            binding.emptyGardenContainer.visibility = if (empty) View.VISIBLE else View.GONE
            binding.waterButton.visibility = if (empty) View.GONE else View.VISIBLE
            binding.waterButtonDisabled.visibility = if (empty) View.VISIBLE else View.GONE

            // 枯萎警告条：检查是否有枯萎的植物
            val hasWithered = plants.any { it.witherStage != "NONE" }
            binding.wiltWarningBar.visibility = if (hasWithered) View.VISIBLE else View.GONE
            if (hasWithered) {
                val witheredCount = plants.count { it.witherStage != "NONE" }
                binding.wiltWarningText.text = "有 $witheredCount 株植物正在枯萎，请尽快浇灌！"
            }
        }

        binding.gardenView.setOnPlantClickListener { plant ->
            val bundle = Bundle().apply {
                putString("plantId", plant.plantId)
            }
            findNavController().navigate(
                R.id.action_gardenFragment_to_plantDetailFragment,
                bundle
            )
        }

        viewModel.meta.observe(viewLifecycleOwner) { meta ->
            if (meta != null) {
                val season = Season.fromString(meta.currentSeason)
                val weather = Weather.fromString(meta.currentWeather)

                binding.seasonToggle.text = season.displayName
                binding.weatherToggle.text = weather.displayName
                binding.statsStreakDays.text = meta.streakDays.toString()

                // 加成倍率
                val multiplier = season.multiplier * weather.multiplier
                binding.statsBonusMultiplier.text = "${multiplier}x"

                // 加成标签，如 "春·晴"
                val seasonLabel = season.displayName.substringBefore("·")
                val weatherLabel = weather.displayName
                binding.statsBonusLabel.text = "$seasonLabel·$weatherLabel"
            }
        }

        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.statsTodayMinutes.text = "$minutes 分钟"
            binding.waterButton.text = "浇灌 · 今日阅读 $minutes 分钟"
        }

        // 季节切换按钮
        binding.seasonToggle.setOnClickListener {
            viewModel.meta.value?.let { meta ->
                val seasons = Season.entries
                val currentIndex = seasons.indexOf(Season.fromString(meta.currentSeason))
                val nextSeason = seasons[(currentIndex + 1) % seasons.size]
                viewModel.updateSeason(nextSeason)
            }
        }

        // 天气切换按钮
        binding.weatherToggle.setOnClickListener {
            viewModel.meta.value?.let { meta ->
                val weathers = Weather.entries
                val currentIndex = weathers.indexOf(Weather.fromString(meta.currentWeather))
                val nextWeather = weathers[(currentIndex + 1) % weathers.size]
                viewModel.updateWeather(nextWeather)
            }
        }

        // 浇灌按钮（替代旧的同步按钮）
        binding.waterButton.setOnClickListener {
            val workInfoLiveData = viewModel.syncNow()
            if (workInfoLiveData != null) {
                binding.waterButton.text = "浇灌中..."
                workInfoLiveData.observe(viewLifecycleOwner) { info ->
                    when {
                        info == null -> {}
                        info.state == WorkInfo.State.SUCCEEDED -> {
                            viewModel.markSyncSuccess()
                            // 文字会在 totalMinutes 观察中自动更新
                        }
                        info.state == WorkInfo.State.FAILED -> {
                            viewModel.markSyncFailed()
                            binding.waterButton.text = "浇灌失败，请重试"
                        }
                    }
                }
            }
        }

        // 无数据时的浇灌按钮
        binding.waterButtonDisabled.setOnClickListener {
            val workInfoLiveData = viewModel.syncNow()
            if (workInfoLiveData != null) {
                binding.waterButtonDisabled.text = "同步中..."
                workInfoLiveData.observe(viewLifecycleOwner) { info ->
                    when {
                        info == null -> {}
                        info.state == WorkInfo.State.SUCCEEDED -> {
                            viewModel.markSyncSuccess()
                        }
                        info.state == WorkInfo.State.FAILED -> {
                            viewModel.markSyncFailed()
                            binding.waterButtonDisabled.text = "同步失败，请重试"
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
