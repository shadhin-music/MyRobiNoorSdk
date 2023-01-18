package com.gakk.noorlibrary.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.islamicName.IslamicNameResponse
import com.gakk.noorlibrary.model.quran.surah.favourite.FavouriteResponse
import com.gakk.noorlibrary.model.quran.surah.unfavourite.UnfavouriteResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/18/2021, Sun
 */
internal class IslamicNameViewModel(private val repository: RestRepository) : ViewModel() {

    var nameResponse: MutableLiveData<Resource<IslamicNameResponse>> = MutableLiveData()

    var favouriteIslamicNameResponse: MutableLiveData<Resource<FavouriteResponse>> =
        MutableLiveData()

    var unFavouriteIslamicNameResponse: MutableLiveData<Resource<UnfavouriteResponse>> =
        MutableLiveData()

    var allFavNameResponse: MutableLiveData<Resource<IslamicNameResponse>> = MutableLiveData()



    companion object {
        val TAG = "IslamicNameViewModel"
        val FACTORY = singleArgViewModelFactory(::IslamicNameViewModel)
    }

    fun getIslamicNameByGender(gender: String) {
        viewModelScope.launch {
            nameResponse.postValue(Resource.loading(data = null))

            try {
                nameResponse.postValue(
                    Resource.success(
                        data = repository.getIslamicNamesByGender(
                            gender
                        )
                    )
                )
            } catch (e: Exception) {
                nameResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun getAllFavoritedNames(gender: String) {
        viewModelScope.launch {
            allFavNameResponse.postValue(Resource.loading(data = null))

            try {
                allFavNameResponse.postValue(
                    Resource.success(
                        data = repository.getAllFavoritedIslamicNames(
                            gender
                        )
                    )
                )
            } catch (e: Exception) {
                allFavNameResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }


    fun favouriteIslamicName(id: String) {
        viewModelScope.launch {
            favouriteIslamicNameResponse.postValue(Resource.loading(data = null))

            try {
                favouriteIslamicNameResponse.postValue(
                    Resource.success(
                        data = repository.makeIslamicNameFav(
                            id
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, e.localizedMessage ?: e.message ?: "")
                favouriteIslamicNameResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun unFavouriteIslamicName(id: String) {
        viewModelScope.launch {
            unFavouriteIslamicNameResponse.postValue(Resource.loading(data = null))

            try {
                unFavouriteIslamicNameResponse.postValue(
                    Resource.success(
                        data = repository.removeIslamicNameFromFav(
                            id
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, e.localizedMessage ?: e.message ?: "")
                unFavouriteIslamicNameResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

}