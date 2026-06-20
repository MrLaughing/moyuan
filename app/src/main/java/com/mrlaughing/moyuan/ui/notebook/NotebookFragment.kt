package com.mrlaughing.moyuan.ui.notebook

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrlaughing.moyuan.databinding.FragmentNotebookBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotebookFragment : Fragment() {

    private var _binding: FragmentNotebookBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotebookViewModel by viewModels()
    private val adapter = NotebookAdapter { notebook ->
        Toast.makeText(requireContext(), "${notebook.bookTitle} · ${notebook.highlights.size} 条划线 · ${notebook.thoughts.size} 条笔记", Toast.LENGTH_SHORT).show()
    }

    private var allNotebooks = emptyList<com.mrlaughing.moyuan.data.remote.dto.NotebookDto>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotebookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notebooksRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.notebooksRecyclerView.adapter = adapter

        viewModel.notebooks.observe(viewLifecycleOwner) { notebooks ->
            allNotebooks = notebooks
            applyFilter(binding.searchEditText.text.toString())
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.notebooksRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.errorView.visibility = if (error != null) View.VISIBLE else View.GONE
            binding.errorView.text = error
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                applyFilter(s?.toString().orEmpty())

            override fun afterTextChanged(s: Editable?) = Unit
        })

        viewModel.loadNotebooks()
    }

    private fun applyFilter(keyword: String) {
        val filtered = if (keyword.isBlank()) allNotebooks
        else allNotebooks.filter {
            it.bookTitle.contains(keyword, ignoreCase = true) ||
                it.author.contains(keyword, ignoreCase = true)
        }
        adapter.submitList(filtered)
        // 修复：基于 filtered 判断空状态，且 loading != true 时才显示空提示
        binding.emptyTextView.visibility =
            if (filtered.isEmpty() && viewModel.loading.value != true) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
