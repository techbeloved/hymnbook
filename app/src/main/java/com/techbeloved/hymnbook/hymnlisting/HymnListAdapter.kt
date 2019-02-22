package com.techbeloved.hymnbook.hymnlisting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.HymnListItemBinding

class HymnListAdapter(private val clickListener: HymnItemModel.ClickListener<HymnItemModel>) : ListAdapter<HymnItemModel, HymnListAdapter.ViewHolder>(HymnItemModel.diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<HymnListItemBinding>(inflater,
                R.layout.hymn_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.item = item
        holder.binding.callback = clickListener
    }

    inner class ViewHolder(internal val binding: HymnListItemBinding) : RecyclerView.ViewHolder(binding.root)

}
