package com.gakk.noorlibrary.data.rest.youtube

import com.gm.shadhin.data.rest.youtube.model.YoutubeBody
import com.gakk.noorlibrary.model.youtube.YoutubeVideoDetails
import com.gakk.noorlibrary.data.rest.api.RetrofitBuilder
import com.gakk.noorlibrary.data.rest.youtube.YoutubeConfig.youtubeBaseUrl
import com.gakk.noorlibrary.util.ykeyTotal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class YoutubeRepository(private val scope: CoroutineScope) {


    private suspend fun youtubeApiService(): YoutubeApiService {
        return RetrofitBuilder().getYoutubeService(youtubeBaseUrl)
    }

    fun fetchVideo(videoId: String, isPodcast: Boolean, callbackFunc: (details: YoutubeVideoDetails) -> Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val details = fetchVideoDetails(videoId, isPodcast)
                withContext(Dispatchers.Main) {
                    callbackFunc.invoke(details)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchVideoDetails(videoId: String, isPodcast: Boolean): YoutubeVideoDetails {
        return youtubeApiService().videoDetails(ykeyTotal, YoutubeBody.buildBody(videoId, isPodcast))
    }
}