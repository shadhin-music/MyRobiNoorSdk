package com.gakk.noorlibrary.model.khatam

import androidx.annotation.Keep

@Keep
data class AddCommentPayload(val CategoryId: String, val TextContentId: String, val Message: String)