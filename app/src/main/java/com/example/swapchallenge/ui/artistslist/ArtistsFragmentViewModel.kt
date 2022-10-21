package com.example.swapchallenge.ui.artistslist

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.swapchallenge.ArtistsQuery
import com.example.swapchallenge.apollo.apolloClient
import com.example.swapchallenge.base.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ArtistsFragmentViewModel used to load the list of artists.
 * Here we have some LiveDatas as well that we observe in the fragment.
 *
 * @param artistsData - actuall data of artists
 * @param searchBarError - if the searchBar it's empty
 * @param artistListDataError - if we get an error from the request
 */

private const val TAG = "ArtistsFragmentViewMode"

class ArtistsFragmentViewModel : BaseViewModel() {
    private var cursorCounter: Int = -1
    private val _artistsData: MediatorLiveData<List<ArtistsQuery.Node>> = MediatorLiveData()
    val artistsData: LiveData<List<ArtistsQuery.Node>> = _artistsData
    private val _searchBarError: MutableLiveData<String> = MutableLiveData()
    val searchBarError: LiveData<String> = _searchBarError
    private val _artistListDataError: MutableLiveData<ApolloException?> = MutableLiveData()
    val artistListDataError: LiveData<ApolloException?> = _artistListDataError

    private var cursor: String? = null
    private var currentQuery: String? = null
    private var searchJob: Job? = null

    fun searchArtist(query: String?, context: Context) {
        currentQuery = query
        searchJob?.cancel(CancellationException("canceling old search and starting new"))
        currentQuery?.let { artistName ->
            if (artistName.isBlank()) {
                _searchBarError.value = "Invalid name"
            } else {
                searchJob = viewModelScope.launch {
                    loading.value = true
                    requestArtists(context, artistName)
                    _searchBarError.value = ""
                    loading.value = false
                }
            }
        }

    }

    fun requestNextPage(context: Context) {
        Log.d(TAG, "requestNextPage: ")
        searchArtist(currentQuery, context)
    }

    /**
     * @param cursor
     * &
     * @param cursorCounter are both used for pagination (when scrolling
     * down to load more artists)
     */

    private suspend fun requestArtists(context: Context, artistName: String) {
        val response = try {
            val result = apolloClient(context).query(
                ArtistsQuery(name = artistName, cursor = Optional.Present(cursor))
            ).execute()
            _artistListDataError.value = null
            Log.d(TAG, "requestArtists: success result is $result")
            result
        } catch (e: ApolloException) {
            Log.d(TAG, "Failure", e)
            e.printStackTrace()
            _artistListDataError.value = e
            loading.value = false
            return
        }
        Log.d(TAG, "requestArtists: cursor $cursor")
        _artistsData.value = response.data?.search?.artists?.nodes?.filterNotNull()
        cursor = response.data?.search?.artists?.edges?.getOrNull(++cursorCounter)?.cursor
    }

}
