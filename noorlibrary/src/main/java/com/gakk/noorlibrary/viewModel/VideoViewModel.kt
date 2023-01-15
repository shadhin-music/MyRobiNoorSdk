package com.gakk.noorlibrary.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.khatam.KhatamQuranVideosResponse
import com.gakk.noorlibrary.model.video.category.VideoByGroup
import com.gakk.noorlibrary.model.video.category.VideosByCategoryApiResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

class VideoViewModel(private val repository: RestRepository) : ViewModel() {

    var videoSubCatOrPlayList: MutableLiveData<Resource<MutableList<VideoByGroup>>> =
        MutableLiveData()

    var videoList: MutableLiveData<Resource<VideosByCategoryApiResponse>> = MutableLiveData()

    var quranvideosResponse: MutableLiveData<Resource<KhatamQuranVideosResponse>> =
        MutableLiveData()

    fun loadIslamicVideosAndPlayListsByCatId(catId: String, pageNo: String) {
        viewModelScope.launch {
            try {

                videoSubCatOrPlayList.postValue(Resource.loading(data = null))
                val list = repository.getSubCategoriesByCatId(catId, pageNo).data ?: mutableListOf()
                // when(list==null){}

                val catOrPlayList: MutableList<VideoByGroup> = mutableListOf()
                for (current in list) {

                    //videocontent/bycategory/60373e395d7a4acd37d5b4e0/603dc49528547ae2a43b013d/1/100
                    val contents = repository.geVideosBySubCategory(
                        catId = catId,
                        subCatId = current.id!!,
                        pageNo = "1"
                    ).data
                    Log.e("CATID", "catId:$catId sub:${current.id} ${contents == null}")

                    contents?.let {
                        catOrPlayList.add(VideoByGroup(catId = current.id!!, it))
                    }

                }
                videoSubCatOrPlayList.postValue(Resource.success(catOrPlayList))

            } catch (e: Exception) {
                Log.i("Exception", "${e.message}")
                videoSubCatOrPlayList.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }

        }

    }

    fun loadIslamicAudiosAndPlayListsByCatId(catId: String, pageNo: String) {
        viewModelScope.launch {
            try {

                videoSubCatOrPlayList.postValue(Resource.loading(data = null))
                val list = repository.getSubCategoriesByCatId(catId, pageNo).data ?: mutableListOf()
                // when(list==null){}

                val catOrPlayList: MutableList<VideoByGroup> = mutableListOf()
                for (current in list) {

                    //audiocontent/bycategory/60373e395d7a4acd37d5b4e0/603dc49528547ae2a43b013d/1/100
                    val contents = repository.getAudiosBySubCategory(
                        catId = catId,
                        subCatId = current.id!!,
                        pageNo = "1"
                    ).data
                    Log.e("CATID", "catId:$catId sub:${current.id} ${contents == null}")
                    contents?.let {
                        catOrPlayList.add(VideoByGroup(catId = current.id!!, it))
                    }

                }
                videoSubCatOrPlayList.postValue(Resource.success(catOrPlayList))

            } catch (e: Exception) {
                Log.i("Exception", "${e.message}")
                videoSubCatOrPlayList.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }

        }

    }

    fun loadIslamicVideosByCatId(catId: String, subCatId: String, pageNo: String) {
        viewModelScope.launch {
            try {

                videoList.postValue(Resource.loading(data = null))
                val contents = repository.geVideosBySubCategory(
                    catId = catId,
                    subCatId = subCatId,
                    pageNo = "1"
                )
                videoList.postValue(Resource.success(contents))

            } catch (e: Exception) {
                Log.i("Exception", "${e.message}")
                videoList.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }

        }

    }
    fun loadKhatamQuranVideos() {
        viewModelScope.launch {
            quranvideosResponse.postValue(Resource.loading(data = null))

            try {
                quranvideosResponse.postValue(
                    Resource.success(
                        repository.getKhatamQuranVideos()
                    )
                )
            } catch (e: Exception) {
                quranvideosResponse.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }


    companion object {
        val FACTORY = singleArgViewModelFactory(::VideoViewModel)
    }


}