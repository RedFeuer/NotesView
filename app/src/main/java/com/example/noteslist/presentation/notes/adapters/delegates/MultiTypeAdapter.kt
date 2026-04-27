package com.example.noteslist.presentation.notes.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes.NoteListItem

abstract class MultiTypeAdapter(
    diffUtilCallback: DiffUtil.ItemCallback<NoteListItem>,
    protected val delegates: List<AdapterDelegate>,
) : ListAdapter<NoteListItem, RecyclerView.ViewHolder>(diffUtilCallback) {
    /* находим индекс делегата, который подходит для текущего элемента списка */
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)

        val delegateIndex = delegates.indexOfFirst { delegate ->
            delegate.isForViewType(item)
        }

        require(delegateIndex != -1) { "No delegate found for item $item at position $position" }

        return delegateIndex
    }

    /* по известному viewType создаем нужный ViewHolder */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int, // это индекс делегата, который мы вернули в getItemViewType
    ): RecyclerView.ViewHolder {
        return delegates[viewType].onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        val delegateIndex = holder.itemViewType

        delegates[delegateIndex].onBindViewHolder(holder, item)
    }

    /* по известному ViewHolder и элементу списка привязываем данные нужному делегату */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads : MutableList<Any>,
    ) {
        val item = getItem(position)
        val delegateIndex = holder.itemViewType

        if (payloads.isEmpty()) {
            delegates[delegateIndex].onBindViewHolder(holder, item)
        } else {
            delegates[delegateIndex].onBindViewHolder(holder, item, payloads)
        }
    }
}