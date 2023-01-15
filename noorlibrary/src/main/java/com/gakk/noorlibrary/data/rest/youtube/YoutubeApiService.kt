package com.gakk.noorlibrary.data.rest.youtube

import com.gm.shadhin.data.rest.youtube.model.YoutubeBody
import com.gakk.noorlibrary.model.youtube.YoutubeVideoDetails
import retrofit2.http.*

interface YoutubeApiService {

    @POST("player")
    suspend fun videoDetails(
        @Query("key") key:String,
        @Body body: YoutubeBody?
    ): YoutubeVideoDetails
}