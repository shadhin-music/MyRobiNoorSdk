package com.gakk.noorlibrary.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.viewModel.PodcastViewModel


class PodViewModelFactory(
    private val restRepository: RestRepository) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PodcastViewModel(restRepository) as T
    }
}