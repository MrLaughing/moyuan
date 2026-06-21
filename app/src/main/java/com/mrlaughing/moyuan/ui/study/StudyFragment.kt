package com.mrlaughing.moyuan.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrlaughing.moyuan.databinding.FragmentStudyBinding
import com.mrlaughing.moyuan.ui.garden.GardenViewModel
import com.mrlaughing.moyuan.ui.garden.SyncOnlyViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudyFragment : Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyViewModel by viewModels()
    private val syncViewModel: SyncOnlyViewModel by viewModels()
    private val adapter = BookListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.booksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.booksRecyclerView.adapter = adapter

        // 书架数据
        viewModel.books.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books)
            binding.booksCountText.text = "${books.size} 本"
            val empty = books.isEmpty()
            binding.booksRecyclerView.visibility = if (empty) View.GONE else View.VISIBLE
            binding.emptyBooksContainer.visibility = if (empty) View.VISIBLE else View.GONE
        }

        // 累计分钟
        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.overviewHours.text = (minutes / 60).toString()
        }

        // meta 数据：书本数、连续天数、夜读天数
        viewModel.meta.observe(viewLifecycleOwner) { meta ->
            if (meta != null) {
                binding.overviewBooks.text = meta.booksRead.toString()
                binding.overviewStreak.text = meta.streakDays.toString()
                binding.overviewNightDays.text = "${meta.nightReadDays} 天"
            }
        }

        // 今日阅读：取 totalMinutes 显示为分钟（后续可改进为真实今日数据）
        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.overviewTodayMinutes.text = "${minutes % 60} 分钟"
        }

        // 空状态的同步按钮
        binding.emptySyncBtn.setOnClickListener {
            syncViewModel.syncNow()
        }

        viewModel.loadIfNeeded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
