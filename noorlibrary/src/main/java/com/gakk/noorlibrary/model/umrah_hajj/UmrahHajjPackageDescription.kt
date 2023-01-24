package com.mcc.noor.model.umrah_hajjs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class UmrahHajjPackageDescription(
    var contactDetails: String?,
    var description: String?,
    var includedFeatures: String?,
    var subHeading: String?
) : Parcelable