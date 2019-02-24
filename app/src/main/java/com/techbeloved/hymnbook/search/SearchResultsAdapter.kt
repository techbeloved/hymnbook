package com.techbeloved.hymnbook.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.ItemSearchResultBinding
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel

class SearchResultsAdapter(private val clickListener: HymnItemModel.ClickListener<HymnItemModel>) : ListAdapter<HymnItemModel, SearchResultsAdapter.ViewHolder>(HymnItemModel.diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<ItemSearchResultBinding>(inflater,
                R.layout.item_search_result, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.item = item
        holder.binding.callback = clickListener
    }

    inner class ViewHolder(internal val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root)

}