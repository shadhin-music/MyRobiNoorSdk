package com.gakk.noorlibrary.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.literature.IsFavouriteResponse
import com.gakk.noorlibrary.model.quran.ayah.AyahsBySurah
import com.gakk.noorlibrary.model.quran.surah.SurahListResponse
import com.gakk.noorlibrary.model.quran.surah.favourite.FavouriteResponse
import com.gakk.noorlibrary.model.quran.surah.unfavourite.UnfavouriteResponse
import com.gakk.noorlibrary.model.quran.surahDetail.SurahDetailsResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

internal class QuranViewModel(private val repository: RestRepository) : ViewModel()  {

    companion object {
        val FACTORY = singleArgViewModelFactory(::QuranViewModel)
    }

    var surahListResponse: MutableLiveData<Resource<SurahListResponse>> = MutableLiveData()

    var surahDetailsResponse: MutableLiveData<Resource<SurahDetailsResponse>> = MutableLiveData()

    var ayahBySurahId: MutableLiveData<Resource<AyahsBySurah>> = MutableLiveData()

    var favouriteSurahResponse: MutableLiveData<Resource<FavouriteResponse>> = MutableLiveData()

    var unFavouriteSurahResponse: MutableLiveData<Resource<UnfavouriteResponse>> = MutableLiveData()

    var isSurahFavouriteResponse: MutableLiveData<Resource<IsFavouriteResponse>> = MutableLiveData()

    fun getAllSurah(pageNo:String) {
        viewModelScope.launch {
            surahListResponse.postValue(Resource.loading(data = null))

            try {
                surahListResponse.postValue(Resource.success(data = repository.getAllSurahList(pageNo)))
            } catch (e: Exception) {
                surahListResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun getSurahDetailsById(id:String) {
        viewModelScope.launch {
            surahDetailsResponse.postValue(Resource.loading(data = null))

            try {
                surahDetailsResponse.postValue(Resource.success(data = repository.getSurahDetailsById(id)))
            } catch (e: Exception) {
                surahDetailsResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun getAyahBySurahId(id:String,pageNo: String) {
        viewModelScope.launch {
            ayahBySurahId.postValue(Resource.loading(data = null))

            try {
                ayahBySurahId.postValue(Resource.success(data = repository.getAyahBySurahId(id,pageNo)))
            } catch (e: Exception) {
                ayahBySurahId.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }


    fun getAllFavouriteSurah(pageNo:String) {
        viewModelScope.launch {
            surahListResponse.postValue(Resource.loading(data = null))

            try {
                surahListResponse.postValue(Resource.success(data = repository.getAllFavouriteSurahList(pageNo)))
            } catch (e: Exception) {
                surahListResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun favouriteSurah(id:String) {
        viewModelScope.launch {
            favouriteSurahResponse.postValue(Resource.loading(data = null))

            try {
                favouriteSurahResponse.postValue(Resource.success(data = repository.favouriteSurah(id)))
            } catch (e: Exception) {
                Log.e("EXCWWD",e.localizedMessage)
                favouriteSurahResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun unFavouriteSurah(id:String) {
        viewModelScope.launch {
            unFavouriteSurahResponse.postValue(Resource.loading(data = null))

            try {
                unFavouriteSurahResponse.postValue(Resource.success(data = repository.unFavouriteSurah(id)))
            } catch (e: Exception) {
                Log.e("Exception",e.localizedMessage)
                unFavouriteSurahResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun getIsSurahFavourtie(id:String) {
        viewModelScope.launch {
            isSurahFavouriteResponse.postValue(Resource.loading(data = null))

            try {
                isSurahFavouriteResponse.postValue(Resource.success(data = repository.getIsSurahFavourite(id)))
            } catch (e: Exception) {
                Log.i("EXCEPTION",e.localizedMessage)
                isSurahFavouriteResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}