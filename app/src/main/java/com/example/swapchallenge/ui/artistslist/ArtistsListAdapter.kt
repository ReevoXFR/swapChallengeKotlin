package com.example.swapchallenge.ui.artistslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.swapchallenge.ArtistsQuery
import com.example.swapchallenge.R
import com.example.swapchallenge.base.widget.UserDiffUtilCallback
import com.example.swapchallenge.databinding.ArtistItemBinding

/**
 * ArtistsListAdapter it's the adapter for the recyclerview within ArtistListFragment.
 */


class ArtistsListAdapter(private val artists: MutableList<ArtistsQuery.Node> = mutableListOf()) :
    RecyclerView.Adapter<ArtistsListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ArtistItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return artists.size
    }

    fun setUserList(updatedUserList: List<ArtistsQuery.Node>) {
        val diffResult = DiffUtil.calculateDiff(UserDiffUtilCallback(artists, updatedUserList))
        artists.clear()
        artists.addAll(updatedUserList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ArtistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    var onEndOfListReached: (() -> Unit)? = null
    var onItemClicked: ((ArtistsQuery.Node) -> Unit)? = null

    /**
     * artistPic.load will load from the diceBear API a generated .png via a seed (in our case,
     * it's $artistId, the .png remains the same everytime if called with the same seed,
     * so we don't have to store the img for consistency.
     */

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = artists[position]
        holder.binding.data = artist.artistBasicFragment

        val artistId = artist.artistBasicFragment.id
        holder.binding.artistPic.load("https://avatars.dicebear.com/api/male/$artistId.png") {
            placeholder(R.drawable.ic_launcher_foreground)
        }

        holder.binding.root.setOnClickListener {
            onItemClicked?.invoke(artist)
        }

        if (position == artists.size - 1) onEndOfListReached?.invoke()
    }

}