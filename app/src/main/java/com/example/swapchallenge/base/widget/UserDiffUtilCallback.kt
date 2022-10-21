package com.example.swapchallenge.base.widget

import androidx.recyclerview.widget.DiffUtil
import com.example.swapchallenge.ArtistsQuery

/**
 * Used to load more items inside the recyclerView.
 */

class UserDiffUtilCallback(
    private val oldList: List<ArtistsQuery.Node>, private val newList: List<ArtistsQuery.Node>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].artistBasicFragment.id == newList[newItemPosition].artistBasicFragment.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].artistBasicFragment.id == newList[newItemPosition].artistBasicFragment.id -> true
            oldList[oldItemPosition].artistBasicFragment.name == newList[newItemPosition].artistBasicFragment.name -> true
            else -> false
        }
    }

}