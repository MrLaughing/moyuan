package com.mrlaughing.moyuan.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrlaughing.moyuan.databinding.FragmentStudyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudyFragment : Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyViewModel by viewModels()
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
            val empty = books.isEmpty()
            binding.booksRecyclerView.visibility = if (empty) View.GONE else View.VISIBLE
            binding.emptyBooksText.visibility = if (empty) View.VISIBLE else View.GONE
        }

        // 累计分钟 → 更新今日阅读分钟数和累计阅读时间
        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.overviewTodayMinutes.text = "$minutes 分钟"
            // 格式化为 "Xh Xm"
            val hours = minutes / 60
            val mins = minutes % 60
            binding.overviewTotalTime.text = "${hours}h ${mins}m"
        }

        // meta 数据：连续天数、已读书目数
        viewModel.meta.observe(viewLifecycleOwner) { meta ->
            if (meta != null) {
                binding.overviewStreak.text = meta.streakDays.toString()
                binding.overviewBooks.text = meta.booksRead.toString()
            }
        }

        viewModel.loadIfNeeded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
