package com.gakk.noorlibrary.data.rest.api

import android.util.ArrayMap
import android.util.Log
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.model.CommonApiResponse
import com.gakk.noorlibrary.model.UserLocation
import com.gakk.noorlibrary.model.auth.AuthPayload
import com.gakk.noorlibrary.model.billboard.BillboardResponse
import com.gakk.noorlibrary.model.currency.CurrentCurrencyModel
import com.gakk.noorlibrary.model.hajjpackage.*
import com.gakk.noorlibrary.model.hajjtracker.*
import com.gakk.noorlibrary.model.home.HomeDataResponse
import com.gakk.noorlibrary.model.islamicName.IslamicNameResponse
import com.gakk.noorlibrary.model.khatam.AddCommentPayload
import com.gakk.noorlibrary.model.khatam.KhatamQuranVideosResponse
import com.gakk.noorlibrary.model.literature.*
import com.gakk.noorlibrary.model.nagad.NagadInitiatePaylaod
import com.gakk.noorlibrary.model.nagad.NagadSubStatusResponse
import com.gakk.noorlibrary.model.nagad.PaymentInitiateResponse
import com.gakk.noorlibrary.model.nearby.NearbyResponse
import com.gakk.noorlibrary.model.podcast.AddCommentResponse
import com.gakk.noorlibrary.model.podcast.CommentListResponse
import com.gakk.noorlibrary.model.podcast.LiveVideosResponse
import com.gakk.noorlibrary.model.quran.ayah.AyahsBySurah
import com.gakk.noorlibrary.model.quran.surah.SurahListResponse
import com.gakk.noorlibrary.model.quran.surah.favourite.FavouriteResponse
import com.gakk.noorlibrary.model.quran.surah.unfavourite.UnfavouriteResponse
import com.gakk.noorlibrary.model.quran.surahDetail.SurahDetailsResponse
import com.gakk.noorlibrary.model.roza.IftarAndSheriTimeforBD
import com.gakk.noorlibrary.model.ssl.SslInitiatePayload
import com.gakk.noorlibrary.model.ssl.SslPaymentInitiateResponse
import com.gakk.noorlibrary.model.subcategory.SubcategoriesByCategoryIdResponse
import com.gakk.noorlibrary.model.subs.CheckSubResponse
import com.gakk.noorlibrary.model.umrah_hajj.*
import com.gakk.noorlibrary.model.video.category.VideosByCategoryApiResponse
import com.gakk.noorlibrary.model.zakat.SaveZakatResponse
import com.gakk.noorlibrary.model.zakat.ZakatDelResponse
import com.gakk.noorlibrary.model.zakat.ZakatListResponse
import com.gakk.noorlibrary.model.zakat.ZakatModel
import com.gakk.noorlibrary.util.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File


class RestRepository(
    private val contentApiService: ApiService,
    private val nearbyApiService: ApiService,
    private val subApiService: ApiService,
    private val subApiServiceRobi: ApiService,
    private val dataApiService: ApiService,
    private val subApiServiceNagad: ApiService,
    private val subApiServiceSsl: ApiService,
    private val authService: ApiService
) {


    suspend fun getSubCategoriesByCatId(
        catId: String,
        pageNo: String
    ): SubcategoriesByCategoryIdResponse {
        return contentApiService.getSubCategoriesByCatId(catId, pageNo)
    }


    suspend fun geTextBasedtLiteratureListBySubCategory(
        catId: String,
        subCatId: String,
        pageNo: String
    ): LiteratureListResponse {
        return contentApiService.getAllTextBasedLiteratureBySubCategory(catId, subCatId, pageNo)
    }

    suspend fun geImageBasedtLiteratureListBySubCategory(
        catId: String,
        subCatId: String,
        pageNo: String
    ): LiteratureListResponse {
        return contentApiService.getAllImageBasedLiteratureBySubCategory(catId, subCatId, pageNo)
    }

    suspend fun loadIsLiteratureFavourite(payload: FavUnFavPayload): IsFavouriteResponse {

        val gson = Gson()
        val params = gson.toJson(payload)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        Log.i("BODY", "body: $payload")

        return contentApiService.isLiteratureFavourite(body)

    }

    suspend fun getFavouriteLiteraturesBySubCategory(
        payload: FavouriteLiteraturePayload,
        pageNo: String
    ): LiteratureListResponse {

        val gson = Gson()
        val params = gson.toJson(payload)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)

        return contentApiService.getFavouriteLiteratureBySubCategory(body, pageNo)
    }

    suspend fun favouriteOrUnFavouriteLiteratureById(
        payload: FavUnFavPayload,
        makeFavourite: Boolean
    ): FavUnFavResponse {

        val gson = Gson()
        val params = gson.toJson(payload)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)

        when (makeFavourite) {
            true -> return contentApiService.favouriteLiteratureById(body)
            else -> return contentApiService.unFavouriteLiteratureById(body)
        }

    }

    suspend fun getBillboradList(): BillboardResponse {
        return contentApiService.getBillbordData(AppPreference.language!!)
    }

    suspend fun getHomeDatalist(): HomeDataResponse {
        return contentApiService.getHomeData(AppPreference.language!!)
    }

    suspend fun getNearbyPlaceOfGivenType(
        key: String,
        radius: String,
        location: UserLocation,
        placeType: String,
        language: String
    ): NearbyResponse {
        val resultList = nearbyApiService.getNearbyPlace(
            key,
            radius,
            location.lat.toString() + "," + location.lng.toString(),
            placeType, language
        )
        return resultList
    }

    suspend fun checkSubscription(msisdn: String, subscriptionId: String): CheckSubResponse {
        return subApiService.checkSubscription(msisdn, subscriptionId)
    }

    suspend fun checkSubscriptionRobi(msisdn: String, subscriptionId: String): String {
        return subApiServiceRobi.checkSubscriptionRobi(msisdn, subscriptionId)
    }

    suspend fun cancelSubscription(msisdn: String, subscriptionId: String): String {
        return subApiService.cancelSubscription(msisdn, subscriptionId)
    }

    suspend fun cancelSubscriptionRobi(msisdn: String, subscriptionId: String): String {
        return subApiServiceRobi.cancelSubscriptionRobi(msisdn, subscriptionId, "app")
    }

    suspend fun checkNetwork(): String {
        return dataApiService.getNetworkStatus()
    }

    suspend fun geVideosBySubCategory(
        catId: String,
        subCatId: String,
        pageNo: String
    ): VideosByCategoryApiResponse {
        return contentApiService.getAllTVideosBySubCategory(catId, subCatId, pageNo)
    }

    suspend fun getAudiosBySubCategory(
        catId: String,
        subCatId: String,
        pageNo: String
    ): VideosByCategoryApiResponse {
        return contentApiService.getAllTAudiosBySubCategory(catId, subCatId, pageNo)
    }

    suspend fun getAllSurahList(pageNo: String): SurahListResponse {
        return contentApiService.getAllSurah(AppPreference.language!!, pageNo)
    }

    suspend fun getAllFavouriteSurahList(pageNo: String): SurahListResponse {
        return contentApiService.getFavouriteSurah(pageNo)
    }

    suspend fun getSurahDetailsById(id: String): SurahDetailsResponse {
        return contentApiService.getSurahDetails(id)
    }

    suspend fun getIsSurahFavourite(id: String): IsFavouriteResponse {
        return contentApiService.isSurahFavourite(id)
    }

    suspend fun getAyahBySurahId(id: String, pageNo: String): AyahsBySurah {
        return contentApiService.getAyatBySurahId(id, pageNo)
    }

    suspend fun favouriteSurah(id: String): FavouriteResponse {
        return contentApiService.favouriteSurah(id)
    }

    suspend fun unFavouriteSurah(id: String): UnfavouriteResponse {
        return contentApiService.unFavouriteSurah(id)
    }

    suspend fun getRamadanTimingData(name: String): IftarAndSheriTimeforBD {
        return contentApiService.getRamadanTimingData(name)
    }

    //Live Podcast

    suspend fun getLive(categoryId: String, subCategoryId: String): LiveVideosResponse {
        return contentApiService.getLive(categoryId, subCategoryId)
    }

    suspend fun getLiveVideos(categoryId: String): LiveVideosResponse {
        return contentApiService.getLiveVideos(categoryId)
    }

    suspend fun getCommentList(
        categoryId: String,
        topicsId: String,
        pageNumber: Int
    ): CommentListResponse {
        return contentApiService.getCommentList(categoryId, topicsId, pageNumber)
    }

    suspend fun postComment(
        categoryId: String, textContentId: String, message: String
    ): AddCommentResponse {

        val commentPayload = AddCommentPayload(categoryId, textContentId, message)
        val gson = Gson()
        val params = gson.toJson(commentPayload)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        val result = contentApiService.addComment(body)
        return result
    }

    suspend fun likeComment(commentId: String): AddCommentResponse {
        return contentApiService.addCommentLike(commentId)
    }

    suspend fun getKhatamQuranVideos(): KhatamQuranVideosResponse {
        return contentApiService.getKhatamQuranVideos()
    }

    suspend fun addDeviceinfo(token: String): HajjLocationShareRequestResponse {
        return contentApiService.addDeviceId(token)
    }

    suspend fun initiatePaymentSsl(
        msisdn: String,
        serviceId: String,
        customerName: String,
        customerEmail: String
    ): SslPaymentInitiateResponse {

        val paymentModel =
            SslInitiatePayload(
                msisdn,
                serviceId,
                SSL_PUSER,
                customerName,
                customerEmail,
                DONATION_CHANNEL
            )
        val gson = Gson()
        val params = gson.toJson(paymentModel)

        val JSON = "application/json; charset=utf-8".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)

        return subApiServiceSsl.initiateSslPayment("application/json", body)
    }


    suspend fun initiatePaymentSslRange(
        msisdn: String,
        serviceId: String,
        customerName: String,
        customerEmail: String,
        channel:String = DONATION_CHANNEL,
        amount:String = ""
    ): SslPaymentInitiateResponse {

        val paymentModel =
            SslInitiatePayload(
                msisdn,
                serviceId,
                SSL_PUSER,
                customerName,
                customerEmail,
                channel,
                amount
            )
        val gson = Gson()
        val params = gson.toJson(paymentModel)

        val JSON = "application/json; charset=utf-8".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)

        return subApiServiceSsl.initiateSslPaymentRange("application/json", body)
    }
    suspend fun checkSubStatusSsl(
        msisdn: String,
        serviceId: String
    ): NagadSubStatusResponse {
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
        jsonParams["MSISDN"] = msisdn
        jsonParams["serviceid"] = serviceId

        val JSON = "application/json; charset=utf-8".toMediaType()
        val body = JSONObject(jsonParams).toString().toRequestBody(JSON)

        return subApiServiceSsl.checkSslSubStatus("application/json", body)
    }
    suspend fun hajjPreregistration(
        image: File?,
        payload: HajjpackageRegPayload
    ): HajjPackageRegistrationResponse {

        val gson = Gson()
        val params = gson.toJson(payload)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        Log.e("Param", params)
        val multiPartImg = image?.let { ImageHelper.getMultiPartImage(it) }

        return contentApiService.hajjPackageReg(multiPartImg, body)
    }

    suspend fun getHajjPreRegList(): HajjPreRegistrationListResponse {
        return contentApiService.getHajjPreRegistrationList()
    }

    suspend fun requestRefund(
        trackingNo: String,
        phoneNumber: String
    ): HajjLocationShareRequestResponse {
        return contentApiService.refundRequest(trackingNo, phoneNumber)
    }

    suspend fun updatePaymentStatus(
        trackingNo: String
    ): CommonApiResponse {
        return contentApiService.paymentStatusUpdateAndSendEmail(trackingNo)
    }

    // umrah hajj package

    suspend fun getUmrahHajjPackage(): UmrahHajjModel {
        return contentApiService.getAllUmrahPackage()
    }

    // zakat calculator save data

    suspend fun saveZakatData(
        payload : ZakatModel
    ): SaveZakatResponse {

        val gson = Gson()
        val jsonObj: JsonElement = gson.fromJson(gson.toJson(payload), JsonElement::class.java)
        jsonObj.asJsonObject.remove("createdOn")
        jsonObj.asJsonObject.remove("id")

        return contentApiService.saveZakatData(Gson().toJson(jsonObj))
    }


    suspend fun getZakatList(): ZakatListResponse {

        return contentApiService.getZakatList()
    }

    suspend fun deleteZakat(id:String): ZakatDelResponse {

        return contentApiService.delZakat(id)
    }



    suspend fun postUmrahHajjPersoanlInfo(data: UmrahHajjPersonalPostModel): UmrahHajjRegResponse {

        val JSON = "application/json; charset=utf-8".toMediaType()

        val body = JSONObject(Gson().toJson(data)).toString().toRequestBody(JSON)

        return contentApiService.postUmrahPersonalInfo(body)
    }

    suspend fun checkUmrahPersonalInfo(passport: String): CheckUmrahReg {

        return contentApiService.checkUmrahPersonalInfo(passport)
    }

    suspend fun GetAllPaymentHistoryUmrah(msisdn: String): CheckUmrahReg {

        return contentApiService.UmrahGetAllPaymentHistory(msisdn)
    }

    suspend fun UmrahPaymentNotification(data: UmrahPaymentStatus): UmrahHajjRegResponse {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val body = JSONObject(Gson().toJson(data)).toString().toRequestBody(JSON)
        return contentApiService.UmrahPaymentStatus(body)
    }

    suspend fun login(phoneNumber: String): String? {
        kotlin.runCatching { Gson().toJson(AuthPayload(phoneNumber)) }.getOrNull()
            ?.let { jsonString ->
                val response = safeApiCall { authService.login(jsonString) }
                AppPreference.userToken = response?.data?.data?.token
                AppPreference.userId = response?.data?.data?.userId
                AppPreference.userNumber = phoneNumber
            }
        return AppPreference.userToken
    }

}

