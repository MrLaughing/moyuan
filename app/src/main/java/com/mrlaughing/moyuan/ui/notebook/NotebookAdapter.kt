package com.mrlaughing.moyuan.ui.notebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrlaughing.moyuan.data.remote.dto.NotebookDto
import com.mrlaughing.moyuan.databinding.ItemNotebookBinding

class NotebookAdapter(private val onClick: (NotebookDto) -> Unit = {}) :
    RecyclerView.Adapter<NotebookAdapter.NotebookViewHolder>() {

    private var items: List<NotebookDto> = emptyList()

    inner class NotebookViewHolder(val binding: ItemNotebookBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookViewHolder {
        val b = ItemNotebookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotebookViewHolder(b)
    }

    override fun onBindViewHolder(holder: NotebookViewHolder, position: Int) {
        val notebook = items[position]
        holder.binding.bookTitle.text = notebook.bookTitle
        holder.binding.author.text = notebook.author
        holder.binding.highlightCount.text = "${notebook.highlights.size} 条划线"
        holder.binding.thoughtCount.text = "${notebook.thoughts.size} 条笔记"
        holder.binding.root.setOnClickListener { onClick(notebook) }
    }

    override fun getItemCount() = items.size

    fun submitList(new: List<NotebookDto>) {
        items = new
        notifyDataSetChanged()
    }
}
