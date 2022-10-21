package com.example.swapchallenge.ui.artistdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.apollographql.apollo3.exception.ApolloException
import com.example.swapchallenge.R
import com.example.swapchallenge.databinding.ArtistDetailsFragmentBinding
import com.example.swapchallenge.fragment.ArtistDetailsFragment

/**
 *  ArtistDetailsFragment.kt represents the screen where we display the details of an artist,
 *  when clicked on an item from the recyclerview (within ArtistListFragment).
 */

class ArtistDetailsFragment : Fragment() {

    private lateinit var binding: ArtistDetailsFragmentBinding
    private val args: ArtistDetailsFragmentArgs by navArgs()
    private val viewModel: ArtistDetailsFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ArtistDetailsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            viewModel.requestArtistDetails(requireContext(), args.artistId)
        }

        viewModel.artistDetailsData.observe(viewLifecycleOwner) {
            setUi(it)
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            setLoading(it)
        }

        viewModel.artistDetailsDataError.observe(viewLifecycleOwner) {
            setErrors(it)
        }
    }

    private fun setLoading(it: Boolean?) {
        binding.loading.visibility = if (it == true) View.VISIBLE else View.GONE
    }

    /**
     * artistPic.load will load from the diceBear API a generated .png via a seed (in our case,
     * it's $artistId, the .png remains the same everytime if called with the same seed,
     * so we don't have to store the img.
     */

    private fun setUi(data: ArtistDetailsFragment?) {
        binding.artistName.text = data?.name ?: getString(R.string.hint_no_name)
        binding.location.text = data?.disambiguation.orEmpty()

        val artistId = data?.id
        binding.artistPic.load("https://avatars.dicebear.com/api/male/$artistId.png") {
            placeholder(R.drawable.ic_launcher_foreground)
        }
    }

    private fun setErrors(data: ApolloException?) {
        binding.error.visibility = when (data) {
            null -> View.GONE
            else -> View.VISIBLE
        }
    }
}
