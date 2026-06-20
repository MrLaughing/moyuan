package com.mrlaughing.moyuan.ui.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrlaughing.moyuan.databinding.FragmentRecommendBinding
import com.mrlaughing.moyuan.ui.search.SearchedBookAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendFragment : Fragment() {

    private var _binding: FragmentRecommendBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecommendViewModel by viewModels()
    private val adapter = SearchedBookAdapter { book ->
        Toast.makeText(requireContext(), "${book.title} · ${book.author}", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.booksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.booksRecyclerView.adapter = adapter

        viewModel.books.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.reason.observe(viewLifecycleOwner) { binding.reasonTextView.text = it }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.booksRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.loadRecommendations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
