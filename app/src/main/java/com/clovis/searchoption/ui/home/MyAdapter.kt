package com.clovis.searchoption.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.clovis.searchoption.databinding.ItemTextBinding
import com.clovis.searchoption.ui.home.DividerWithoutLast
class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private var items = listOf<String>()
    private var shouldShowArrow: Boolean = false

    fun submitList(newList: List<String>, shouldShowArrow: Boolean = false) {
        items = newList
        this.shouldShowArrow = shouldShowArrow
        notifyItemRangeChanged(0, items.size)
    }

    inner class ViewHolder(val binding: ItemTextBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTextBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            binding.tvText.text = items[position]
            binding.arrow.visibility = if (shouldShowArrow) View.VISIBLE else View.GONE
            binding.arrow.rotation = if (shouldShowArrow) 270f else 0f
            binding.arrow.setOnClickListener {
                Toast.makeText(holder.itemView.context,
                    "Arrow clicked",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun getItemCount() = items.size
}