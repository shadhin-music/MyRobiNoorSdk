package com.gm.shadhin.data.rest.youtube.model

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.gakk.noorlibrary.data.rest.youtube.YoutubeConfig.bodyJson
import com.gakk.noorlibrary.data.rest.youtube.YoutubeConfig.bodyJsonPodcast
import java.util.regex.Matcher
import java.util.regex.Pattern

@Keep
data class YoutubeBody(
    @SerializedName("context")
    var context: Context? = null,
    @SerializedName("playbackContext")
    var playbackContext: PlaybackContext? = null,
    @SerializedName("videoId")
    var videoId: String? = null
) {
    data class Context(
        @SerializedName("client")
        var client: Client? = null,
        @SerializedName("user")
        var user: User? = null
    ) {
        data class Client(
            @SerializedName("clientName")
            var clientName: String? = null,
            @SerializedName("clientVersion")
            var clientVersion: String? = null,
            @SerializedName("gl")
            var gl: String? = null,
            @SerializedName("hl")
            var hl: String? = null
        )

        data class User(
            @SerializedName("lockedSafetyMode")
            var lockedSafetyMode: Boolean? = null
        )
    }

    data class PlaybackContext(
        @SerializedName("contentPlaybackContext")
        var contentPlaybackContext: ContentPlaybackContext? = null
    ) {
        data class ContentPlaybackContext(
            @SerializedName("signatureTimestamp")
            var signatureTimestamp: String? = null
        )
    }

    companion object {
        fun buildBody(videoId: String?, isPodcast: Boolean): YoutubeBody? {
            if (isPodcast){
                return Gson().fromJson(bodyJsonPodcast, YoutubeBody::class.java).apply {
                    this.videoId = videoId
                }
            }else {
                return Gson().fromJson(bodyJson, YoutubeBody::class.java).apply {
                    this.videoId = videoId
                }
            }

        }

        fun extractYoutubeId(ytUrl: String?): String? {
            var vId: String? = null
            val pattern: Pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE
            )
            val matcher: Matcher = pattern.matcher(ytUrl)
            if (matcher.matches()) {
                vId = matcher.group(1)
            }
            return vId
        }
    }
}
