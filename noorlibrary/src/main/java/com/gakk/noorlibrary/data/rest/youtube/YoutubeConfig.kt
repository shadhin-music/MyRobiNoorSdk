package com.gakk.noorlibrary.data.rest.youtube

object YoutubeConfig {
    const val youtubeBaseUrl = "https://www.youtube.com/youtubei/v1/"

    const val bodyJsonPodcast = "{\n" +
            "  \"playbackContext\": {\n" +
            "    \"contentPlaybackContext\": {\n" +
            "      \"signatureTimestamp\": \"18927\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"context\": {\n" +
            "    \"client\": {\n" +
            "      \"hl\": \"en-GB\",\n" +
            "      \"gl\": \"GB\",\n" +
            "      \"clientName\": \"WEB\",\n" +
            "      \"clientVersion\": \"2.20210728.00.00\"\n" +
            "    },\n" +
            "    \"user\": {\n" +
            "      \"lockedSafetyMode\": false\n" +
            "    }\n" +
            "  },\n" +
            "\n" +
            "  \"videoId\": \"\"\n" +
            "}"

    const val bodyJson = "{\n" +
            " \n" +
            " \n" +
            "  \"context\": {\n" +
            "    \"client\": {\n" +
            "      \"hl\": \"en-GB\",\n" +
            "      \"clientName\": \"ANDROID\",\n" +
            "      \"gl\": \"GB\",\n" +
            "      \"clientVersion\": \"17.10.35\",\n" +
            "      \"platform\": \"MOBILE\"\n" +
            "    },\n" +
            "    \"user\": {\n" +
            "      \"lockedSafetyMode\": false\n" +
            "    }\n" +
            "  },\n" +
            "  \"racyCheckOk\": true,\n" +
            "  \"videoId\": \"8WPUYkuzvXs\"\n" +
            "}"
}