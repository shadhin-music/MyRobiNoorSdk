package com.gakk.noorlibrary.data.rest.api

import android.util.ArrayMap
import android.util.Log
import androidx.annotation.Keep
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
import com.gakk.noorlibrary.model.names.NamesOfAllahApiResponse
import com.gakk.noorlibrary.model.nearby.NearbyResponse
import com.gakk.noorlibrary.model.podcast.AddCommentResponse
import com.gakk.noorlibrary.model.podcast.CommentListResponse
import com.gakk.noorlibrary.model.podcast.LiveVideosResponse
import com.gakk.noorlibrary.model.profile.UserInfoResponse
import com.gakk.noorlibrary.model.quran.ayah.AyahsBySurah
import com.gakk.noorlibrary.model.quran.surah.SurahListResponse
import com.gakk.noorlibrary.model.quran.surah.favourite.FavouriteResponse
import com.gakk.noorlibrary.model.quran.surah.unfavourite.UnfavouriteResponse
import com.gakk.noorlibrary.model.quran.surahDetail.SurahDetailsResponse
import com.gakk.noorlibrary.model.quranSchool.QuranSchoolResponse
import com.gakk.noorlibrary.model.quranSchool.ScholarsResponse
import com.gakk.noorlibrary.model.quranSchool.SingleScholarResponse
import com.gakk.noorlibrary.model.roza.IftarAndSheriTimeforBD
import com.gakk.noorlibrary.model.ssl.SslInitiatePayload
import com.gakk.noorlibrary.model.ssl.SslPaymentInitiateResponse
import com.gakk.noorlibrary.model.subcategory.SubcategoriesByCategoryIdResponse
import com.gakk.noorlibrary.model.subs.CheckSubResponse
import com.gakk.noorlibrary.model.tracker.AllPrayerDataResponse
import com.gakk.noorlibrary.model.tracker.PostPrayerDataResponse
import com.gakk.noorlibrary.model.tracker.PrayerAddModel
import com.gakk.noorlibrary.model.tracker.SalahStatus
import com.gakk.noorlibrary.model.tracker.ramadan.AllRamadanDataResponse
import com.gakk.noorlibrary.model.tracker.ramadan.add.PostRamadanDataResponse
import com.gakk.noorlibrary.model.tracker.ramadan.add.RamadanAddModel
import com.gakk.noorlibrary.model.video.category.VideosByCategoryApiResponse
import com.gakk.noorlibrary.util.*
import com.google.gson.Gson
import com.mcc.noor.model.umrah_hajj.*
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

    suspend fun getNinetyNineNamesOfAllah(): NamesOfAllahApiResponse {
        return contentApiService.getNamesOfAllah(AppPreference.language!!)
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

    suspend fun getUserInfo(id: String): UserInfoResponse {
        return contentApiService.getUserInfo(id)
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

    suspend fun getAllScholars(): ScholarsResponse {
        return contentApiService.getAllScholars(AppPreference.language!!)
    }

    suspend fun getScholarsById(id: String): SingleScholarResponse {
        return contentApiService.getScholarsById(id)
    }

    suspend fun getQuranSchoolByScholars(id: String): QuranSchoolResponse {
        return contentApiService.getQuranSchoolByScholars(id)
    }

    suspend fun getIslamicNamesByGender(gender: String): IslamicNameResponse {
        return contentApiService.getIslamicName(AppPreference.language!!, gender)
    }

    suspend fun makeIslamicNameFav(id: String): FavouriteResponse {
        return contentApiService.makeIslamicNameFavorite(id)
    }

    suspend fun removeIslamicNameFromFav(id: String): UnfavouriteResponse {
        return contentApiService.removeIslamicNameFromFav(id)
    }

    suspend fun getAllFavoritedIslamicNames(gender: String): IslamicNameResponse {
        return contentApiService.getFavouritedIslamicNames(AppPreference.language!!, gender)
    }

    suspend fun getAllPrayerData(fromMonth: String, toMonth: String): AllPrayerDataResponse {
        return contentApiService.getAllPrayerData(fromMonth, toMonth)
    }

    suspend fun getRamadanTimingData(name: String): IftarAndSheriTimeforBD {
        return contentApiService.getRamadanTimingData(name)
    }

    suspend fun addPrayerData(
        createdOn: String,
        language: String,
        salahStatus: SalahStatus
    ): PostPrayerDataResponse {
        val addPrayerModel = PrayerAddModel(
            CreatedOn = createdOn, Language = language,
            SalahStatus = salahStatus
        )
        val gson = Gson()
        val params = gson.toJson(addPrayerModel)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        val result = contentApiService.addPrayerData(body)
        return result
    }

    suspend fun updatePrayerData(
        id: String,
        createdBy: String,
        createdOn: String,
        language: String,
        salahStatus: SalahStatus
    ): PostPrayerDataResponse {
        val addPrayerModel = PrayerAddModel(
            id = id,
            CreatedBy = createdBy,
            CreatedOn = createdOn, Language = language,
            SalahStatus = salahStatus
        )
        val gson = Gson()
        val params = gson.toJson(addPrayerModel)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        val result = contentApiService.updatePrayerData(body)
        return result
    }

    suspend fun getAllRamadanData(fromMonth: String, toMonth: String): AllRamadanDataResponse {
        return contentApiService.getAllRamadanData(fromMonth, toMonth)
    }


    suspend fun addRamadanData(
        createdOn: String,
        language: String,
        ramadanStatus: Boolean
    ): PostRamadanDataResponse {
        val addPrayerModel = RamadanAddModel(
            CreatedOn = createdOn, Language = language,
            RamadanStatus = ramadanStatus
        )
        val gson = Gson()
        val params = gson.toJson(addPrayerModel)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        val result = contentApiService.addRamadanData(body)
        return result
    }

    suspend fun updateRamadanData(
        id: String,
        createdBy: String,
        createdOn: String,
        language: String,
        ramadanStatus: Boolean
    ): PostRamadanDataResponse {
        val addPrayerModel = RamadanAddModel(
            id = id,
            CreatedBy = createdBy,
            CreatedOn = createdOn, Language = language,
            RamadanStatus = ramadanStatus
        )
        val gson = Gson()
        val params = gson.toJson(addPrayerModel)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        val result = contentApiService.updateRamadanData(body)
        return result
    }

    suspend fun getTodayCurrencyRate(from: String, to: String): CurrentCurrencyModel? {
        var currentCurrencyModel: CurrentCurrencyModel? = null
        val url = String.format(
            "http://data.fixer.io/api/latest?access_key=6777107e8e6d8515777655152d451e7a&base=EUR&symbols=%s,%s",
            from,
            to
        )
        val response = contentApiService.getCurrencyDetails(url)
        withContext(Dispatchers.IO) {
            val jsonObject = JSONObject(response.string())
            val isSuccess: Boolean = jsonObject.getBoolean("success")
            if (isSuccess) {
                currentCurrencyModel = CurrentCurrencyModel(
                    isSuccess,
                    jsonObject.getInt("timestamp"),
                    jsonObject.getString("date"),
                    jsonObject.getJSONObject("rates").getString(from),
                    jsonObject.getJSONObject("rates").getString(to)
                )
            }
        }

        return currentCurrencyModel
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

    suspend fun registerHajjUser(
        msisdn: String
    ): HajjPackageEntryResponse {
        val registerModel = HajjPackagePayload(msisdn)
        val gson = Gson()
        val params = gson.toJson(registerModel)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        val result = contentApiService.hajjPackageEntry(body)
        return result
    }

    suspend fun addDeviceinfo(token: String): HajjLocationShareRequestResponse {
        return contentApiService.addDeviceId(token)
    }

    suspend fun locationShareRequest(trackerPhone: String): HajjLocationShareRequestResponse {
        return contentApiService.hajjLocationShareRequest(trackerPhone)
    }

    suspend fun locationTrackRequest(
        sharerPhone: String,
        trackerPhone: String
    ): HajjLocationShareRequestResponse {
        val registerModel = LocationTrackPayload(sharerPhone, trackerPhone)
        val gson = Gson()
        val params = gson.toJson(registerModel)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        val result = contentApiService.hajjLocationTrackingRequest(body)
        return result
    }

    suspend fun getHajjSharingList(): HajjSharingListResponse {
        return contentApiService.getHajjSharingList()
    }

    suspend fun hajjLocationSave(
        lat: String,
        lon: String
    ): HajjLocationShareRequestResponse {
        val registerModel = LocationSavePayload(lat, lon)
        val gson = Gson()
        val params = gson.toJson(registerModel)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        val result = contentApiService.addtHajjTrakingData(body)
        return result
    }

    suspend fun locationTrackRequest(sharerUserPhone: String): HajjLocationShareRequestResponse {
        return contentApiService.hajjLocationTrackRequest(sharerUserPhone)
    }

    suspend fun getHajjTrackingList(): HajjTrackingListResponse {
        return contentApiService.getHajjTrackingList()
    }

    suspend fun getHajjShareLocation(msisdn: String): HajjShareLocationGetResponse {
        return contentApiService.getSharerLocation(msisdn)
    }

    suspend fun deleteHajjData(id: String): HajjLocationShareRequestResponse {
        return contentApiService.deleteHajjTrackingData(id)
    }


    suspend fun initiatePaymentNagad(
        msisdn: String,
        serviceId: String
    ): PaymentInitiateResponse {

        val paymentModel = NagadInitiatePaylaod(msisdn, serviceId, NAGAD_PUSER)
        val gson = Gson()
        val params = gson.toJson(paymentModel)

        val JSON = "application/json; charset=utf-8".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)

        return subApiServiceNagad.initiateNagadPayment("application/json", body)
    }

    suspend fun checkSubStatusNagad(
        msisdn: String,
        serviceId: String
    ): NagadSubStatusResponse {
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
        jsonParams["MSISDN"] = msisdn
        jsonParams["serviceid"] = serviceId

        val JSON = "application/json; charset=utf-8".toMediaType()
        val body = JSONObject(jsonParams).toString().toRequestBody(JSON)

        return subApiServiceNagad.checkNagadSubStatus("application/json", body)
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

        return subApiServiceSsl.initiateSslPaymentRange("application/json", body)
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

