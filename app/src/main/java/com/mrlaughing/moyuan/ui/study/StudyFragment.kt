package com.mrlaughing.moyuan.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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

        // 阅读笔记入口
        binding.notebookEntry.setOnClickListener {
            findNavController().navigate(
                com.mrlaughing.moyuan.R.id.notebookFragment
            )
        }

        // 书籍搜索入口
        binding.searchEntry.setOnClickListener {
            findNavController().navigate(
                com.mrlaughing.moyuan.R.id.searchFragment
            )
        }

        // 推荐好书入口
        binding.recommendEntry.setOnClickListener {
            findNavController().navigate(
                com.mrlaughing.moyuan.R.id.recommendFragment
            )
        }

        // 阅读统计入口
        binding.statsEntry.setOnClickListener {
            findNavController().navigate(
                com.mrlaughing.moyuan.R.id.statsFragment
            )
        }

        viewModel.books.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books)
            binding.emptyTextView.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.statsSummary.text = "累计阅读：${minutes} 分钟"
        }

        viewModel.loadIfNeeded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
