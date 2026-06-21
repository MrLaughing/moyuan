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
            binding.emptyGardenContainer.visibility =
                if (plants.isEmpty()) View.VISIBLE else View.GONE
            binding.plantCountText.text = "${plants.size} 株"
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
                val seasonDisp = when (Season.fromString(meta.currentSeason)) {
                    Season.SPRING -> "春·萌芽"
                    Season.SUMMER -> "夏·盛放"
                    Season.AUTUMN -> "秋·收获"
                    Season.WINTER -> "冬·沉淀"
                }
                val weatherDisp = when (Weather.fromString(meta.currentWeather)) {
                    Weather.CLEAR -> "晴"
                    Weather.SPRING_RAIN -> "春雨"
                    Weather.FIRST_SNOW -> "初雪"
                    Weather.MOONLIT -> "月夜"
                }
                binding.seasonText.text = seasonDisp
                binding.weatherText.text = weatherDisp
                binding.statsBookCount.text = meta.booksRead.toString()
                binding.statsStreakDays.text = meta.streakDays.toString()
                binding.statsNightDays.text = "${meta.nightReadDays} 夜读"
            }
        }

        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.statsTotalHours.text = (minutes / 60).toString()
            binding.statsTotalMinutes.text = "$minutes 分钟"
        }

        viewModel.lastSync.observe(viewLifecycleOwner) { date ->
            if (!date.isNullOrEmpty()) {
                binding.syncStatusText.text = "上次同步：$date"
                binding.syncStatusText.visibility = View.VISIBLE
            }
        }

        binding.syncButton.setOnClickListener {
            val workInfoLiveData = viewModel.syncNow()
            if (workInfoLiveData != null) {
                binding.syncStatusText.text = "同步中..."
                binding.syncStatusText.visibility = View.VISIBLE
                workInfoLiveData.observe(viewLifecycleOwner) { info ->
                    when {
                        info == null -> {}
                        info.state == WorkInfo.State.SUCCEEDED -> {
                            binding.syncStatusText.text = "同步成功"
                            binding.syncStatusText.visibility = View.VISIBLE
                            viewModel.markSyncSuccess()
                        }
                        info.state == WorkInfo.State.FAILED -> {
                            binding.syncStatusText.text = "同步失败，请检查网络或 API Key"
                            binding.syncStatusText.visibility = View.VISIBLE
                            viewModel.markSyncFailed()
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
