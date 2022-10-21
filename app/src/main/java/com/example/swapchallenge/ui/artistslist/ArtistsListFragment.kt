package com.example.swapchallenge.ui.artistslist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.exception.ApolloException
import com.example.swapchallenge.ArtistsQuery
import com.example.swapchallenge.base.hideKeyboard
import com.example.swapchallenge.databinding.FragmentArtistsBinding

/**
 *  ArtistsListFragment.kt represents the screen where we display the list of artists,
 *  We have observers for the @params from the ViewModel:
 *  where we either:
 *  - display the data
 *  - display the error that comes from the API
 *  - display an emptyState if no data comes from the API
 */

class ArtistsListFragment : Fragment() {
    private lateinit var binding: FragmentArtistsBinding
    private val viewModel: ArtistsFragmentViewModel by viewModels()
    private val adapter = ArtistsListAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()

        viewModel.artistsData.observe(viewLifecycleOwner) {
            binding.searchBar.clearFocus()
            setArtists(it)
        }

        viewModel.searchBarError.observe(viewLifecycleOwner) {
            binding.searchBar.error = it
        }

        viewModel.artistListDataError.observe(viewLifecycleOwner) {
            setErrors(it)
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            setLoading(it)
        }

        binding.searchBar.setEndIconOnClickListener {
            viewModel.searchArtist(binding.searchBar.editText?.text?.toString(), requireContext())
            hideKeyboard(view, requireContext())
        }

        binding.defaultEditText.addTextChangedListener(searchTextWatcher)

        adapter.onItemClicked = { launch ->
            findNavController().navigate(
                ArtistsListFragmentDirections.actionArtistsListFragmentToArtistDetailsFragment(
                    artistId = launch.artistBasicFragment.id
                )
            )
        }
    }

    private fun setUi() {
        binding.artistsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.artistsRv.adapter = adapter

        adapter.onEndOfListReached = {
            viewModel.requestNextPage(requireContext())
        }
    }

    private fun setArtists(it: List<ArtistsQuery.Node>?) {
        it.let {
            if (it != null) {
                adapter.setUserList(it)
                binding.noDataFound.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setLoading(it: Boolean?) {
        binding.loading.visibility = if (it == true) View.VISIBLE else View.GONE
    }

    private fun setErrors(data: ApolloException?) {
        binding.error.visibility = when (data) {
            null -> View.GONE
            else -> View.VISIBLE
        }
        binding.error.text = data?.message.orEmpty()
        binding.noDataFound.visibility = View.GONE
    }

    /**
     * We use this TextWatcher so we clear the searchBar error after user types something.
     */

    private val searchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(string: Editable?) {
            binding.searchBar.error = null
        }

        override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) =
            Unit

        override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) =
            Unit
    }
}