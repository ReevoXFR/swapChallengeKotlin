package com.example.swapchallenge.ui.artistdetails

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.exception.ApolloException
import com.example.swapchallenge.ArtistQuery
import com.example.swapchallenge.apollo.apolloClient
import com.example.swapchallenge.base.BaseViewModel
import com.example.swapchallenge.fragment.ArtistDetailsFragment

/**
 * ArtistDetailsFragmentViewModel used to load details about the artist, using the artistId.
 */

class ArtistDetailsFragmentViewModel : BaseViewModel() {

    private val _artistDetailsData: MutableLiveData<ArtistDetailsFragment?> = MutableLiveData()
    val artistDetailsData: LiveData<ArtistDetailsFragment?> = _artistDetailsData
    private val _artistDetailsDataError: MutableLiveData<ApolloException?> = MutableLiveData()
    val artistDetailsDataError: LiveData<ApolloException?> = _artistDetailsDataError

    suspend fun requestArtistDetails(context: Context, artistId: String) {
        loading.value = true

        val response = try {
            apolloClient(context).query(ArtistQuery(nodeId = artistId)).execute()
        } catch (e: ApolloException) {
            _artistDetailsDataError.value = e
            loading.value = false
            return
        }

        val artistDetails = response.data?.node?.artistDetailsFragment
        if (artistDetails != null || !response.hasErrors()) {
            _artistDetailsData.value = artistDetails
            loading.value = false
            return
        }
    }

}
