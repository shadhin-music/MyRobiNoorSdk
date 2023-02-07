package com.gakk.noorlibrary.model.umrah_hajjs

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class UmrahHajjPackageDescription(
    var contactDetails: String?,
    var description: String?,
    var includedFeatures: String?,
    var subHeading: String?
) : Parcelable