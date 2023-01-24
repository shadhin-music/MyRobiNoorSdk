package com.mcc.noor.model.umrah_hajj

import android.os.Parcelable
import com.mcc.noor.model.umrah_hajjs.UmrahHajjPackageDescription
import kotlinx.parcelize.Parcelize

@Parcelize
data class UmrahHajjData(
    var image: String?,
    var isActive: Boolean?,
    var packPrice: String?,
    var packageDescription: UmrahHajjPackageDescription?,
    var packageName: String?,
    var startDate: String?,
    var umrahPackageId: String?
) : Parcelable {

    var packagePrice: String?
        get() = "৳$packPrice"
        set(value) {
            packagePrice = value
        }


}
