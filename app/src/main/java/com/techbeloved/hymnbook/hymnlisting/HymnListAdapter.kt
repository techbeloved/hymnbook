package com.techbeloved.hymnbook.hymnlisting

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.HymnListItemBinding

class HymnListAdapter(private val clickListener: HymnItemModel.ClickListener<HymnItemModel>)
    : ListAdapter<HymnItemModel, HymnListAdapter.ViewHolder>(HymnItemModel.HymnItemDiffCallback), Filterable {

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                if (hymnList.isEmpty()) return null
                val filteredHymns: List<HymnItemModel>?
                if (constraint.isNullOrBlank()) {
                    filteredHymns = hymnList
                } else {
                    filteredHymns = hymnList.filter {
                        it.title.contains(constraint, true)
                                ||  it.id.toString().contains(constraint, true)
                    }
                }

                return FilterResults().apply { values = filteredHymns }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null) {
                    submitList(results.values as MutableList<HymnItemModel>?)
                }
            }
        }
    }

    private val hymnList: MutableList<HymnItemModel> = ArrayList()

    /**
     * Use to submit data to the adapter from fragment. This enables us to save the original list so we can run filtering on it
     */
    fun submitData(list: List<HymnItemModel>?) {
        hymnList.clear()
        list?.let { hymnList.addAll(it) }
        synchronized(this) { submitList(list) }
    }

    inner class ViewHolder(internal val binding: HymnListItemBinding) : RecyclerView.ViewHolder(binding.root)

}
