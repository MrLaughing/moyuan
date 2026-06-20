package com.mrlaughing.moyuan.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrlaughing.moyuan.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private val adapter = SearchedBookAdapter { book ->
        Toast.makeText(requireContext(), "${book.title} · ${book.author} · 评分 ${book.rating}", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.booksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.booksRecyclerView.adapter = adapter

        viewModel.books.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            binding.emptyTextView.visibility = if (errorMsg != null) View.VISIBLE else View.GONE
            binding.emptyTextView.text = errorMsg ?: "输入书名、作者搜索书籍"
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.booksRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        binding.searchButton.setOnClickListener {
            performSearch()
        }

        binding.keywordEditText.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
    }

    private fun performSearch() {
        val keyword = binding.keywordEditText.text?.toString().orEmpty()
        viewModel.search(keyword)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
