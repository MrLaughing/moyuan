package com.mrlaughing.moyuan.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mrlaughing.moyuan.databinding.FragmentStatsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            if (stats == null) {
                binding.statsScrollView.visibility = View.GONE
                return@observe
            }
            binding.statsScrollView.visibility = View.VISIBLE
            binding.totalMinutes.text = "累计阅读: ${stats.totalMinutes} 分钟"
            binding.todayMinutes.text = "今日: ${stats.todayMinutes} 分钟"
            binding.thisWeekMinutes.text = "本周: ${stats.thisWeekMinutes} 分钟"
            binding.thisMonthMinutes.text = "本月: ${stats.thisMonthMinutes} 分钟"
            binding.thisYearMinutes.text = "本年: ${stats.thisYearMinutes} 分钟"
            binding.streakDays.text = "连续阅读: ${stats.streakDays} 天"
            binding.maxStreakDays.text = "最长连续: ${stats.maxStreakDays} 天"
            binding.readDays.text = "阅读天数: ${stats.readDays} 天"
            binding.nightReadDays.text = "夜读天数: ${stats.nightReadDays} 天"
            binding.finishBooksCount.text = "读完书籍: ${stats.finishBooksCount} 本"
            binding.noteCount.text = "笔记数: ${stats.noteCount}"
            binding.highlightCount.text = "划线数: ${stats.highlightCount}"
            binding.preferCategory.text = "偏好类别: ${stats.preferCategory}"
            binding.preferAuthor.text = "偏好作者: ${stats.preferAuthor}"
            binding.depthLevel.text = "阅读深度: Lv.${stats.depthLevel}"
            binding.depthDescription.text = stats.depthDescription
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.statsScrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.loadStats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
