package com.example.swapchallenge.base

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel

/**
 * BaseViewModel to store the shared elements between ViewModels.
 *
 * @param loading - represents the status of the spinningWheel inside the .xml of each fragment.
 */

open class BaseViewModel : ViewModel() {
    val loading: MediatorLiveData<Boolean> = MediatorLiveData()
}