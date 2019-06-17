package com.techbeloved.hymnbook.playlists

import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.ItemPlaylistBinding
import com.techbeloved.hymnbook.databinding.ItemSimpleTitleBinding
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel
import com.techbeloved.hymnbook.utils.layoutInflater

class PlaylistsAdapter(private val clickListener: HymnItemModel.ClickListener<HymnItemModel>)
    : ListAdapter<HymnItemModel, PlaylistsAdapter.ViewHolder>(HymnItemModel.HymnItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPlaylistBinding = DataBindingUtil.inflate(
                parent.context.layoutInflater(),
                R.layout.item_playlist,
                parent,
                false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = getItem(position)
        holder.binding.callback = clickListener
    }


    inner class ViewHolder constructor(val binding: ItemPlaylistBinding)
        : RecyclerView.ViewHolder(binding.root)
}

class SimplePlaylistsAdapter(private val clickListener: HymnItemModel.ClickListener<HymnItemModel>)
    : ListAdapter<HymnItemModel, SimplePlaylistsAdapter.ViewHolder>(HymnItemModel.HymnItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSimpleTitleBinding = DataBindingUtil.inflate(
                parent.context.layoutInflater(),
                R.layout.item_simple_title,
                parent,
                false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = getItem(position)
        holder.binding.callback = clickListener
    }


    inner class ViewHolder constructor(val binding: ItemSimpleTitleBinding)
        : RecyclerView.ViewHolder(binding.root)
}