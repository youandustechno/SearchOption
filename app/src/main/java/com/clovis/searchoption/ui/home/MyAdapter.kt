package com.clovis.searchoption.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clovis.searchoption.databinding.ItemTextBinding

class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private var items = listOf<String>()

    fun submitList(newList: List<String>) {
        items = newList
        notifyDataSetChanged()
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
        holder.binding.tvText.text = items[position]
    }

    override fun getItemCount() = items.size
}