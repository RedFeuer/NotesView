package com.example.noteslist.presentation.notes.adapters.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes.NoteListItem

abstract class MultiTypeAdapter(
    diffUtilCallback: DiffUtil.ItemCallback<NoteListItem>,
    private val delegates: List<AdapterDelegate>,
) : ListAdapter<NoteListItem, RecyclerView.ViewHolder>(diffUtilCallback) {
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
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return delegates[viewType].onCreateViewHolder(parent)
    }

    /* по известному ViewHolder и элементу списка привязываем данные нужному делегату */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        val delegateIndex = holder.itemViewType
        delegates[delegateIndex].onBindViewHolder(holder, item)
    }
}