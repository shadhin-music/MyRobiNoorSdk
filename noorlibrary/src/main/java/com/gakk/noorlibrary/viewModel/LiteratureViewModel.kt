package com.gakk.noorlibrary.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.literature.*
import com.gakk.noorlibrary.model.roza.IftarAndSheriTimeforBD
import com.gakk.noorlibrary.model.subcategory.SubcategoriesByCategoryIdResponse
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch
import kotlin.Exception

internal class LiteratureViewModel(private val repository: RestRepository) : ViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::LiteratureViewModel)
    }

    var literatureListData: MutableLiveData<Resource<LiteratureListResponse>> = MutableLiveData()
    var literatureListImageData: MutableLiveData<Resource<LiteratureListResponse>> = MutableLiveData()
    var isFavData: MutableLiveData<Resource<IsFavouriteResponse>> = MutableLiveData()
    var favOrUnFavData: MutableLiveData<Resource<FavUnFavResponse>> = MutableLiveData()
    var subCategoryListData: MutableLiveData<Resource<SubcategoriesByCategoryIdResponse>> =
        MutableLiveData()
    var ramadanListData: MutableLiveData<Resource<IftarAndSheriTimeforBD>> = MutableLiveData()
    fun loadSubCategoriesByCatId(catId: String, pageNo: String) {
        viewModelScope.launch {
            subCategoryListData.postValue(Resource.loading(data = null))
            try {
                subCategoryListData.postValue(
                    Resource.success(
                        repository.getSubCategoriesByCatId(catId, pageNo)
                    )
                )

            } catch (e: Exception) {
                Log.i("ERR", e.message!!)
                subCategoryListData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }


    fun loadIsLiteratureFav(catId: String, subCatId: String, literatureId: String) {
        viewModelScope.launch {
            isFavData.postValue(Resource.loading(data = null))
            try {
                isFavData.postValue(
                    Resource.success(
                        repository.loadIsLiteratureFavourite(
                            FavUnFavPayload(catId, literatureId, subCatId),
                        )
                    )
                )
            } catch (e: Exception) {
                Log.i("ERR", e.message!!)
                isFavData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun favouriteOrUnFavouriteLiterature(
        catId: String,
        subCatId: String,
        literatureId: String,
        makeFavourite: Boolean
    ) {
        viewModelScope.launch {
            favOrUnFavData.postValue(Resource.loading(data = null))
            try {
                favOrUnFavData.postValue(
                    Resource.success(
                        repository.favouriteOrUnFavouriteLiteratureById(
                            FavUnFavPayload(catId, literatureId, subCatId),
                            makeFavourite
                        )
                    )
                )
            } catch (e: Exception) {
                Log.i("ERR", e.message!!)
                favOrUnFavData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun loadTextBasedLiteratureListBySubCategory(catId: String, subCatId: String, pageNo: String) {
        viewModelScope.launch {
            literatureListData.postValue(Resource.loading(data = null))
            try {
                literatureListData.postValue(
                    Resource.success(
                        repository.geTextBasedtLiteratureListBySubCategory(
                            catId,
                            subCatId,
                            pageNo
                        )
                    )
                )
            } catch (e: Exception) {
//                Log.i("ERR", e.message!!)
                literatureListData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun loadImageBasedLiteratureListBySubCategory(catId: String, subCatId: String, pageNo: String) {
        viewModelScope.launch {
            literatureListData.postValue(Resource.loading(data = null))
            try {
                literatureListData.postValue(
                    Resource.success(
                        repository.geImageBasedtLiteratureListBySubCategory(
                            catId,
                            subCatId,
                            pageNo
                        )
                    )
                )
            } catch (e: Exception) {
                literatureListData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun loadFavouriteLiteratureListBySubCategory(catId: String, subCatId: String, pageNo: String) {
        viewModelScope.launch {
            literatureListData.postValue(Resource.loading(data = null))
            try {
                literatureListData.postValue(
                    Resource.success(
                        repository.getFavouriteLiteraturesBySubCategory(
                            FavouriteLiteraturePayload(catId, subCatId), pageNo
                        )
                    )
                )
            } catch (e: Exception) {
                Log.i("ERR", e.message!!)
                literatureListData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }


    fun loadImageLiteratureListBySubCategory(catId: String, subCatId: String, pageNo: String) {
        viewModelScope.launch {
            literatureListImageData.postValue(Resource.loading(data = null))
            try {
                literatureListImageData.postValue(
                    Resource.success(
                        repository.geImageBasedtLiteratureListBySubCategory(
                            catId,
                            subCatId,
                            pageNo
                        )
                    )
                )
            } catch (e: Exception) {
                literatureListImageData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun loadRamadanTimingData(name: String) {
        viewModelScope.launch {
            ramadanListData.postValue(Resource.loading(data = null))

            try {
                ramadanListData.postValue(
                    Resource.success(
                        data =repository.getRamadanTimingData(name)
                    )
                )
            } catch (e: Exception) {
                Log.e("ERR", e.message!!)
                ramadanListData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}