package com.mrlaughing.moyuan.ui.search

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mrlaughing.moyuan.R
import com.mrlaughing.moyuan.data.remote.dto.SearchedBookDto
import com.mrlaughing.moyuan.databinding.ItemSearchedBookBinding

class SearchedBookAdapter(
    private val onClick: (SearchedBookDto) -> Unit = {}
) : ListAdapter<SearchedBookDto, SearchedBookAdapter.ViewHolder>(BookDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchedBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemSearchedBookBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onClick(getItem(pos))
            }
        }

        fun bind(book: SearchedBookDto) {
            binding.title.text = book.title
            binding.author.text = book.author
            binding.rating.text = if (book.rating > 0) "★ ${book.rating}" else null
            binding.category.text = book.category.takeIf { it.isNotBlank() }
            binding.wordCount.text = if (book.wordCount > 0) "${book.wordCount / 10000}万字" else null
            binding.introduction.text = book.introduction.takeIf { it.isNotBlank() }

            if (book.cover.isNotBlank()) {
                Glide.with(binding.coverImage)
                    .load(book.cover)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(binding.coverImage)
            } else {
                try {
                    binding.coverImage.setBackgroundColor(Color.parseColor(book.coverColor))
                } catch (e: Exception) {
                    binding.coverImage.setBackgroundColor(Color.parseColor("#F0F0F0"))
                }
            }
        }
    }

    class BookDiff : DiffUtil.ItemCallback<SearchedBookDto>() {
        override fun areItemsTheSame(oldItem: SearchedBookDto, newItem: SearchedBookDto) = oldItem.bookId == newItem.bookId
        override fun areContentsTheSame(oldItem: SearchedBookDto, newItem: SearchedBookDto) = oldItem == newItem
    }
}
