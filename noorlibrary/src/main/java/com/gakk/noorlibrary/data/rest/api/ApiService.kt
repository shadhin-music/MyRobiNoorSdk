package com.gakk.noorlibrary.data.rest.api

import com.gakk.noorlibrary.model.CommonApiResponse
import com.gakk.noorlibrary.model.billboard.BillboardResponse
import com.gakk.noorlibrary.model.hajjpackage.HajjPackageEntryResponse
import com.gakk.noorlibrary.model.hajjpackage.HajjPackageRegistrationResponse
import com.gakk.noorlibrary.model.hajjpackage.HajjPreRegistrationListResponse
import com.gakk.noorlibrary.model.hajjtracker.HajjLocationShareRequestResponse
import com.gakk.noorlibrary.model.hajjtracker.HajjShareLocationGetResponse
import com.gakk.noorlibrary.model.hajjtracker.HajjSharingListResponse
import com.gakk.noorlibrary.model.hajjtracker.HajjTrackingListResponse
import com.gakk.noorlibrary.model.home.HomeDataResponse
import com.gakk.noorlibrary.model.islamicName.IslamicNameResponse
import com.gakk.noorlibrary.model.khatam.KhatamQuranVideosResponse
import com.gakk.noorlibrary.model.literature.FavUnFavResponse
import com.gakk.noorlibrary.model.literature.IsFavouriteResponse
import com.gakk.noorlibrary.model.literature.LiteratureListResponse
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
import com.gakk.noorlibrary.model.ssl.SslPaymentInitiateResponse
import com.gakk.noorlibrary.model.subcategory.SubcategoriesByCategoryIdResponse
import com.gakk.noorlibrary.model.subs.CheckSubResponse
import com.gakk.noorlibrary.model.tracker.AllPrayerDataResponse
import com.gakk.noorlibrary.model.tracker.PostPrayerDataResponse
import com.gakk.noorlibrary.model.tracker.ramadan.AllRamadanDataResponse
import com.gakk.noorlibrary.model.tracker.ramadan.add.PostRamadanDataResponse
import com.gakk.noorlibrary.model.video.category.VideosByCategoryApiResponse
import com.mcc.noor.model.umrah_hajj.CheckUmrahReg
import com.mcc.noor.model.umrah_hajj.UmrahHajjModel
import com.mcc.noor.model.umrah_hajj.UmrahHajjRegResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    @GET("subcategory/bycategory/{catId}/{pageNo}/10")
    suspend fun getSubCategoriesByCatId(
        @Path("catId") catId: String,
        @Path("pageNo") pageNo: String
    ): SubcategoriesByCategoryIdResponse

    @GET("textcontent/bycategory/{catId}/{subCatId}/{pageNo}/30")
    suspend fun getAllTextBasedLiteratureBySubCategory(
        @Path("catId") catId: String,
        @Path("subCatId") subCatId: String,
        @Path("pageNo") pageNo: String
    ): LiteratureListResponse

    @GET("imagecontent/bycategory/{catId}/{subCatId}/{pageNo}/10")
    suspend fun getAllImageBasedLiteratureBySubCategory(
        @Path("catId") catId: String,
        @Path("subCatId") subCatId: String,
        @Path("pageNo") pageNo: String
    ): LiteratureListResponse

    @GET("ninetyninename/{languageCode}/1/100")
    suspend fun getNamesOfAllah(@Path("languageCode") languageCode: String): NamesOfAllahApiResponse

    @Multipart
    @POST("textcontent/getfavorite/{pageNo}/10")
    suspend fun getFavouriteLiteratureBySubCategory(
        @Part("payload") body: RequestBody,
        @Path("pageNo") pageNo: String
    ): LiteratureListResponse

    @Multipart
    @POST("textcontent/isFavorite")
    suspend fun isLiteratureFavourite(@Part("payload") body: RequestBody): IsFavouriteResponse

    @Multipart
    @POST("textcontent/favorite")
    suspend fun favouriteLiteratureById(@Part("payload") body: RequestBody): FavUnFavResponse

    @Multipart
    @POST("textcontent/unfavorite")
    suspend fun unFavouriteLiteratureById(@Part("payload") body: RequestBody): FavUnFavResponse

    @GET("account/getuser/{id}")
    suspend fun getUserInfo(@Path("id") id: String): UserInfoResponse

    @Multipart
    @PUT("account/updateprofile")
    suspend fun updateInfowithoutPic(@Part("payload") body: RequestBody): UserInfoResponse

    @Multipart
    @PUT("account/updateprofile")
    suspend fun updateInfowithPic(
        @Part image: MultipartBody.Part?,
        @Part("payload") body: RequestBody
    ): UserInfoResponse

    @GET("billboard/publishedcontent/{languageCode}")
    suspend fun getBillbordData(@Path("languageCode") languageCode: String): BillboardResponse

    @GET("publish/publishedcontent/{languageCode}")
    suspend fun getHomeData(@Path("languageCode") languageCode: String): HomeDataResponse

    @GET("json?sensor=true")
    suspend fun getNearbyPlace(
        @Query("key") key: String,
        @Query("radius") radius: String,
        @Query("location") location: String,
        @Query("type") type: String,
        @Query("language") language: String
    ): NearbyResponse

    @GET("CheckSubsNoorG.aspx?")
    suspend fun checkSubscription(
        @Query("mid") mid: String,
        @Query("subscriptionID") subscriptionID: String
    ): CheckSubResponse

    @GET("SubscriptionStatus.aspx?")
    suspend fun checkSubscriptionRobi(
        @Query("smsisdn") smsisdn: String,
        @Query("serviceid") serviceid: String
    ): String


    @GET("CancelSubsNoorG.aspx")
    suspend fun cancelSubscription(
        @Query("mid") mid: String,
        @Query("subscriptionID") subscriptionID: String
    ): String


    @GET("Unsubscription.aspx")
    suspend fun cancelSubscriptionRobi(
        @Query("sMSISDN") sMSISDN: String,
        @Query("ServiceID") ServiceID: String,
        @Query("Dsource") Dsource: String
    ): String

    @GET("digi.msisdn.get.app")
    suspend fun getNetworkStatus(): String

    @GET("videocontent/bycategory/{catId}/{subCatId}/{pageNo}/30")
    suspend fun getAllTVideosBySubCategory(
        @Path("catId") catId: String,
        @Path("subCatId") subCatId: String,
        @Path("pageNo") pageNo: String
    ): VideosByCategoryApiResponse

    @GET("audiocontent/bycategory/{catId}/{subCatId}/{pageNo}/10")
    suspend fun getAllTAudiosBySubCategory(
        @Path("catId") catId: String,
        @Path("subCatId") subCatId: String,
        @Path("pageNo") pageNo: String
    ): VideosByCategoryApiResponse

    @GET("surah/{languageCode}/{pageNo}/114")
    suspend fun getAllSurah(
        @Path("languageCode") languageCode: String,
        @Path("pageNo") pageNo: String
    ): SurahListResponse

    @GET("surah/{id}")
    suspend fun getSurahDetails(@Path("id") id: String): SurahDetailsResponse

    @GET("ayat/bysurah/{id}/{pageNo}/10")
    suspend fun getAyatBySurahId(
        @Path("id") id: String,
        @Path("pageNo") pageNo: String
    ): AyahsBySurah

    @GET("surah/favorite/{pageNo}/114")
    suspend fun getFavouriteSurah(@Path("pageNo") pageNo: String): SurahListResponse


    @GET("surah/isFavorite/{id}")
    suspend fun isSurahFavourite(@Path("id") id: String): IsFavouriteResponse


    @POST("surah/favorite/{id}")
    suspend fun favouriteSurah(@Path("id") id: String): FavouriteResponse

    @HTTP(method = "DELETE", path = "surah/unfavorite/{id}", hasBody = true)
    suspend fun unFavouriteSurah(@Path("id") id: String): UnfavouriteResponse


    @GET("scholar/{lang}/null/null")
    suspend fun getAllScholars(@Path("lang") lang: String): ScholarsResponse

    @GET("scholar/{scholarsId}")
    suspend fun getScholarsById(@Path("scholarsId") scholarsId: String): SingleScholarResponse

    @GET("quranschool/byscholar/{scholarsId}/null/null")
    suspend fun getQuranSchoolByScholars(@Path("scholarsId") scholarsId: String): QuranSchoolResponse


    //islamic names
    @GET("islamicname/{lang}/null/null/{gender}")
    suspend fun getIslamicName(
        @Path("lang") lang: String,
        @Path("gender") gender: String
    ): IslamicNameResponse

    @POST("islamicname/favorite/{id}")
    suspend fun makeIslamicNameFavorite(@Path("id") id: String): FavouriteResponse

    @HTTP(method = "DELETE", path = "islamicname/unfavorite/{id}", hasBody = true)
    suspend fun removeIslamicNameFromFav(@Path("id") id: String): UnfavouriteResponse

    @GET("islamicname/favorite/{lang}/{gender}/null/null")
    suspend fun getFavouritedIslamicNames(
        @Path("lang") lang: String,
        @Path("gender") gender: String
    ): IslamicNameResponse

    @GET
    suspend fun getCurrencyDetails(@Url url: String): ResponseBody

    //Prayer Tracker
    @GET("Salahtracker/bymonth/{fromMonth}/{toMonth}/1/100")
    suspend fun getAllPrayerData(
        @Path("fromMonth") fromMonth: String,
        @Path("toMonth") toMonth: String
    ): AllPrayerDataResponse


    @GET("prayer/{name}/BD")
    suspend fun getRamadanTimingData(
        @Path("name") name: String
    ): IftarAndSheriTimeforBD


    @Multipart
    @POST("Salahtracker/add")
    suspend fun addPrayerData(@Part("payload") body: RequestBody): PostPrayerDataResponse

    @Multipart
    @PUT("Salahtracker/edit")
    suspend fun updatePrayerData(@Part("payload") body: RequestBody): PostPrayerDataResponse


    //Ramadan Tracker
    @GET("ramadantracker/byyear/{fromMonth}/{toMonth}/1/100")
    suspend fun getAllRamadanData(
        @Path("fromMonth") fromMonth: String,
        @Path("toMonth") toMonth: String
    ): AllRamadanDataResponse

    @Multipart
    @POST("ramadantracker/add")
    suspend fun addRamadanData(@Part("payload") body: RequestBody): PostRamadanDataResponse

    @Multipart
    @PUT("ramadantracker/edit")
    suspend fun updateRamadanData(@Part("payload") body: RequestBody): PostRamadanDataResponse

    //Live Podcast

    @GET("streaming/geturl/bn/{CategoryId}/{SubcategoryId}")
    suspend fun getLive(
        @Path("CategoryId") CategoryId: String,
        @Path("SubcategoryId") SubcategoryId: String
    ): LiveVideosResponse

    @GET("streaming/getrecordurl/bn/{CategoryId}")
    suspend fun getLiveVideos(
        @Path("CategoryId") CategoryId: String
    ): LiveVideosResponse

    @POST("deviceinfos/add/")
    suspend fun addDeviceId(@Query("deviceId") deviceId: String): HajjLocationShareRequestResponse

    @GET("comment/comment/get/{categoryId}/{topicsId}/{pageNumber}")
    suspend fun getCommentList(
        @Path("categoryId") categoryId: String,
        @Path("topicsId") topicsId: String,
        @Path("pageNumber") pageNumber: Int
    ): CommentListResponse

    @Multipart
    @POST("comment/comment/add")
    suspend fun addComment(
        @Part("payload") body: RequestBody
    ): AddCommentResponse

    @POST("comment/comment/like/{commentId}")
    suspend fun addCommentLike(@Path("commentId") commentId: String): AddCommentResponse

    @GET("streaming/getkahtmequran/bn")
    suspend fun getKhatamQuranVideos(): KhatamQuranVideosResponse

    @Multipart
    @POST("hajjpackageentry/add")
    suspend fun hajjPackageEntry(@Part("payload") body: RequestBody): HajjPackageEntryResponse

    //Hajj Tracker

    @POST("hajjpackageentry/hajjlocationsharerequest/{trackerPhone}")
    suspend fun hajjLocationShareRequest(@Path("trackerPhone") trackerPhone: String): HajjLocationShareRequestResponse

    @Multipart
    @POST("hajjpackageentry/addhajjtraking")
    suspend fun hajjLocationTrackingRequest(@Part("payload") body: RequestBody): HajjLocationShareRequestResponse

    @GET("hajjpackageentry/gethajjsharinglist")
    suspend fun getHajjSharingList(): HajjSharingListResponse

    @Multipart
    @POST("hajjpackageentry/addthajjtrakingdata")
    suspend fun addtHajjTrakingData(@Part("payload") body: RequestBody): HajjLocationShareRequestResponse


    @POST("hajjpackageentry/hajjlocationtrackrequest/{sharerUserPhone}")
    suspend fun hajjLocationTrackRequest(@Path("sharerUserPhone") sharerUserPhone: String): HajjLocationShareRequestResponse

    @GET("hajjpackageentry/gethajjtrakinglist")
    suspend fun getHajjTrackingList(): HajjTrackingListResponse

    @GET("hajjpackageentry/gethajjsharerlocation/{msisdn}")
    suspend fun getSharerLocation(@Path("msisdn") msisdn: String): HajjShareLocationGetResponse

    @POST("hajjpackageentry/deletehajjtraking/{id}")
    suspend fun deleteHajjTrackingData(@Path("id") id: String): HajjLocationShareRequestResponse

    //Nagad Payment

    @POST("NgPayInitiateNoor")
    suspend fun initiateNagadPayment(
        @Header("Content-Type") content_type: String,
        @Body param: RequestBody
    ): PaymentInitiateResponse

    @POST("SubsStatus")
    suspend fun checkNagadSubStatus(
        @Header("Content-Type") content_type: String,
        @Body param: RequestBody
    ): NagadSubStatusResponse

    //SSL Payment

    @POST("SSLPayInitiateDP")
    suspend fun initiateSslPayment(
        @Header("Content-Type") content_type: String,
        @Body param: RequestBody
    ): SslPaymentInitiateResponse

    @POST("SSLPayInitiateDPNoorRng")
    suspend fun initiateSslPaymentRange(
        @Header("Content-Type") content_type: String,
        @Body param: RequestBody
    ): SslPaymentInitiateResponse

    @POST("subsstatus")
    suspend fun checkSslSubStatus(
        @Header("Content-Type") content_type: String,
        @Body param: RequestBody
    ): NagadSubStatusResponse


    @Multipart
    @POST("hajjpackagereg/add")
    suspend fun hajjPackageReg(
        @Part image: MultipartBody.Part?,
        @Part("payload") body: RequestBody
    ): HajjPackageRegistrationResponse

    @GET("hajjpackagereg/getlist")
    suspend fun getHajjPreRegistrationList(): HajjPreRegistrationListResponse

    @POST("hajjpackagereg/refundrequest/{trackingNo}/{phoneNumber}")
    suspend fun refundRequest(
        @Path("trackingNo") trackingNo: String,
        @Path("phoneNumber") phoneNumber: String
    ): HajjLocationShareRequestResponse

    @POST("hajjpackagereg/paymentnotify/{trackingNo}")
    suspend fun paymentStatusUpdateAndSendEmail(
        @Path("trackingNo") trackingNo: String
    ): CommonApiResponse

    // umrah hajj package

    @GET("Umrah/Packages")
    suspend fun getAllUmrahPackage(): UmrahHajjModel


    @POST("Umrah/Registration")
    suspend fun postUmrahPersonalInfo(
        @Body param: RequestBody
    ): UmrahHajjRegResponse

    @GET("Umrah/RegistrationStatus/{passport}")
    suspend fun checkUmrahPersonalInfo(
        @Path("passport") passport: String,
    ): CheckUmrahReg

    @GET("Umrah/RegistrationHistory/{Msisdn}")
    suspend fun UmrahGetAllPaymentHistory(
        @Path("Msisdn") Msisdn: String,
    ): CheckUmrahReg

    @POST("Umrah/PaymentNotification")
    suspend fun UmrahPaymentStatus(
        @Body param: RequestBody
    ): UmrahHajjRegResponse
}
