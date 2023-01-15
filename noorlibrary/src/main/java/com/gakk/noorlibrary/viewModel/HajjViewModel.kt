package com.gakk.noorlibrary.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gakk.noorlibrary.data.rest.Resource
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.CommonApiResponse
import com.gakk.noorlibrary.model.currency.CurrencyModel
import com.gakk.noorlibrary.model.currency.CurrentCurrencyModel
import com.gakk.noorlibrary.model.hajjpackage.HajjPackageEntryResponse
import com.gakk.noorlibrary.model.hajjpackage.HajjPackageRegistrationResponse
import com.gakk.noorlibrary.model.hajjpackage.HajjPreRegistrationListResponse
import com.gakk.noorlibrary.model.hajjpackage.HajjpackageRegPayload
import com.gakk.noorlibrary.model.hajjtracker.HajjLocationShareRequestResponse
import com.gakk.noorlibrary.model.hajjtracker.HajjShareLocationGetResponse
import com.gakk.noorlibrary.model.hajjtracker.HajjTrackingListResponse
import com.gakk.noorlibrary.model.subcategory.SubcategoriesByCategoryIdResponse
import com.gakk.noorlibrary.util.LAN_BANGLA
import com.gakk.noorlibrary.util.loadJSONFromAsset
import com.gakk.noorlibrary.util.singleArgViewModelFactory
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/26/2021, Mon
 */
class HajjViewModel(private val repository: RestRepository) : ViewModel() {
    companion object {
        val FACTORY = singleArgViewModelFactory(::HajjViewModel)
    }

    var currentRateLivaData: MutableLiveData<Resource<CurrentCurrencyModel>> =
        MutableLiveData()

    var subCategoryListData: MutableLiveData<Resource<SubcategoriesByCategoryIdResponse>> =
        MutableLiveData()

    var countryListLiveData: MutableLiveData<Resource<List<CurrencyModel>>> =
        MutableLiveData()

    var addHajjUser: MutableLiveData<Resource<HajjPackageEntryResponse>> = MutableLiveData()
    var shareLocation: MutableLiveData<Resource<HajjLocationShareRequestResponse>> =
        MutableLiveData()
    var trackLocation: MutableLiveData<Resource<HajjLocationShareRequestResponse>> =
        MutableLiveData()

    var trackRequest: MutableLiveData<Resource<HajjLocationShareRequestResponse>> =
        MutableLiveData()
    var trackList: MutableLiveData<Resource<HajjTrackingListResponse>> =
        MutableLiveData()

    var hajjLocation: MutableLiveData<Resource<HajjShareLocationGetResponse>> =
        MutableLiveData()

    var deleteData: MutableLiveData<Resource<HajjLocationShareRequestResponse>> =
        MutableLiveData()

    var addHajjPreregistration: MutableLiveData<Resource<HajjPackageRegistrationResponse>> =
        MutableLiveData()

    var preRegistrationList: MutableLiveData<Resource<HajjPreRegistrationListResponse>> =
        MutableLiveData()

    var refundRequest: MutableLiveData<Resource<HajjLocationShareRequestResponse>> =
        MutableLiveData()

    var paymentStatus: MutableLiveData<Resource<CommonApiResponse>> =
        MutableLiveData()


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
                subCategoryListData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun parseCountryList(context: Context) {
        viewModelScope.launch {
            val countryList = ArrayList<CurrencyModel>()
            var countryJsonArray: JSONArray? = null
            val countryIS: InputStream = context.getAssets().open("countrys.json")
            val json = loadJSONFromAsset(countryIS)
            json?.let {
                countryJsonArray = JSONArray(it)
            }

            var currencyJsonArray: JSONArray? = null

            val currencyIS: InputStream = context.getAssets().open("currency.json")
            val jsonTwo = loadJSONFromAsset(currencyIS)
            jsonTwo?.let {
                currencyJsonArray = JSONArray(it)
            }

            countryJsonArray?.let { jsonArray ->
                for (j in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray[j] as JSONObject
                    val countryName =
                        jsonObject.getString("countryName").toUpperCase(Locale.ENGLISH)
                    val countryCode = jsonObject.getString("countryCode")

                    currencyJsonArray?.let { currencyJA ->
                        for (i in 0 until currencyJA.length()) {
                            val jb = currencyJA[i] as JSONObject
                            val alphabeticCode = jb.getString("AlphabeticCode")
                            val currency = jb.getString("Currency")
                            val entity = jb.getString("Entity")
                            val minorUnit = jb.getString("MinorUnit")
                            val numericCode = jb.getString("NumericCode")
                            if (entity.equals(countryName, ignoreCase = true)) {
                                val currencyModel = CurrencyModel(
                                    alphabeticCode,
                                    currency,
                                    entity,
                                    minorUnit,
                                    numericCode,
                                    countryCode
                                )

                                countryList.add(currencyModel)
                            }
                        }
                    }
                }
            }

            countryListLiveData.postValue(
                Resource.success(data = countryList)
            )
        }
    }



    fun getCurrentRates(from: String, to: String) {
        viewModelScope.launch {
            currentRateLivaData.postValue(Resource.loading(data = null))
            try {
                val model = repository.getTodayCurrencyRate(from, to)
                model?.let {
                    currentRateLivaData.postValue(
                        Resource.success(
                            model
                        )
                    )

                } ?: kotlin.run {
                    currentRateLivaData.postValue(
                        Resource.error(
                            data = null,
                            message = "Error Occurred!"
                        )
                    )
                }

            } catch (e: Exception) {
                currentRateLivaData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun postHajjUser(msisdn: String) {
        viewModelScope.launch {
            addHajjUser.postValue(Resource.loading(data = null))

            try {
                addHajjUser.postValue(Resource.success(data = repository.registerHajjUser(msisdn)))
            } catch (e: Exception) {
                addHajjUser.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }


    fun locationShareRequest(trackerPhone: String) {
        viewModelScope.launch {
            shareLocation.postValue(Resource.loading(data = null))
            try {
                shareLocation.postValue(
                    Resource.success(
                        data = repository.locationShareRequest(
                            trackerPhone
                        )
                    )
                )
            } catch (e: Exception) {
                shareLocation.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun locationTrackRequestFromTracker(shaererPhone: String, trackerPhone: String) {
        viewModelScope.launch {
            trackLocation.postValue(Resource.loading(data = null))
            try {
                trackLocation.postValue(
                    Resource.success(
                        data = repository.locationTrackRequest(
                            shaererPhone,
                            trackerPhone
                        )
                    )
                )
            } catch (e: Exception) {
                trackLocation.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun locationTrackRequestFromSharer(trackerPhone: String) {
        viewModelScope.launch {
            trackRequest.postValue(Resource.loading(data = null))
            try {
                trackRequest.postValue(
                    Resource.success(
                        data = repository.locationTrackRequest(
                            trackerPhone
                        )
                    )
                )
            } catch (e: Exception) {
                trackRequest.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun loadTrackingList() {
        viewModelScope.launch {
            trackList.postValue(Resource.loading(data = null))
            try {
                trackList.postValue(
                    Resource.success(
                        data = repository.getHajjTrackingList()
                    )
                )
            } catch (e: Exception) {
                trackList.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun getHajjLocation(msisdn: String) {
        viewModelScope.launch {
            hajjLocation.postValue(Resource.loading(data = null))
            try {
                hajjLocation.postValue(
                    Resource.success(
                        data = repository.getHajjShareLocation(msisdn)
                    )
                )
            } catch (e: Exception) {
                hajjLocation.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun deleteDataHajj(id: String) {
        viewModelScope.launch {
            deleteData.postValue(Resource.loading(data = null))
            try {
                deleteData.postValue(
                    Resource.success(
                        data = repository.deleteHajjData(id)
                    )
                )
            } catch (e: Exception) {
                deleteData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun addHajjPreRegistration(
        image: File?, userName: String, dOB: String, gender: String, docType: String,
        docNumber: String, permanentDistrict: String, permanentAddress: String, presentAddress:
        String, phoneNumber: String, email: String, maritalStatus: String, maritalRefName: String

    ) {
        viewModelScope.launch {
            addHajjPreregistration.postValue(Resource.loading(data = null))

            try {
                addHajjPreregistration.postValue(
                    Resource.success(
                        repository.hajjPreregistration(
                            image,
                            HajjpackageRegPayload(
                                userName,
                                dOB,
                                gender,
                                docType,
                                docNumber,
                                permanentDistrict,
                                permanentAddress,
                                presentAddress,
                                phoneNumber,
                                LAN_BANGLA,
                                email,
                                maritalStatus,
                                maritalRefName
                            )
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("ERR", e.message!!)
                addHajjPreregistration.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun loadPreRegistrationList() {
        viewModelScope.launch {
            preRegistrationList.postValue(Resource.loading(data = null))
            try {
                preRegistrationList.postValue(
                    Resource.success(
                        data = repository.getHajjPreRegList()
                    )
                )
            } catch (e: Exception) {
                preRegistrationList.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun requestHajjRefund(
        trackingNo: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            refundRequest.postValue(Resource.loading(data = null))
            try {
                refundRequest.postValue(
                    Resource.success(
                        data = repository.requestRefund(trackingNo, phoneNumber)
                    )
                )
            } catch (e: Exception) {
                refundRequest.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }

    fun updatePaymentStatus(trackingNo: String) {
        viewModelScope.launch {
            paymentStatus.postValue(Resource.loading(data = null))
            try {
                paymentStatus.postValue(
                    Resource.success(
                        data = repository.updatePaymentStatus(trackingNo)
                    )
                )
            } catch (e: Exception) {
                paymentStatus.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}