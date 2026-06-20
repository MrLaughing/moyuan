package com.mrlaughing.moyuan.ui.study

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mrlaughing.moyuan.R
import com.mrlaughing.moyuan.data.local.db.entity.BookTrackingEntity
import com.mrlaughing.moyuan.databinding.ItemBookBinding

class BookListAdapter : ListAdapter<BookTrackingEntity, BookListAdapter.BookViewHolder>(BookDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: BookTrackingEntity) {
            binding.bookTitle.text = book.title
            binding.bookMinutes.text = "${book.readMinutes}分钟"
            if (book.cover.isNotBlank()) {
                Glide.with(binding.bookCover)
                    .load(book.cover)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(binding.bookCover)
            } else {
                binding.bookCover.setImageResource(R.drawable.ic_book_placeholder)
            }
        }
    }

    class BookDiff : DiffUtil.ItemCallback<BookTrackingEntity>() {
        override fun areItemsTheSame(oldItem: BookTrackingEntity, newItem: BookTrackingEntity) =
            oldItem.bookId == newItem.bookId
        override fun areContentsTheSame(oldItem: BookTrackingEntity, newItem: BookTrackingEntity) =
            oldItem == newItem
    }
}
