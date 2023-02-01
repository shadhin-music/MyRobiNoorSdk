package com.gakk.noorlibrary.model.zakat


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
class ZakatModel(
    @SerializedName("agriculture")
    @Expose
    var agriculture: Double?,
    @SerializedName("businessPayment")
    @Expose
    var businessPayment: Double?,
    @SerializedName("carPayment")
    @Expose
    var carPayment: Double?,
    @SerializedName("cash")
    @Expose
    var cash: Double?,
    @SerializedName("cashInBankaccount")
    @Expose
    var cashInBankaccount: Double?,
    @SerializedName("cashInBusiness")
    @Expose
    var cashInBusiness: Double?,
    @SerializedName("creditCardPayment")
    @Expose
    var creditCardPayment: Double?,
    @SerializedName("familyLoan")
    @Expose
    var familyLoan: Double?,
    @SerializedName("familyLoansAndOthers")
    @Expose
    var familyLoansAndOthers: Double?,
    @SerializedName("houseRent")
    @Expose
    var houseRent: Double?,
    @SerializedName("isActive")
    @Expose
    var isActive: Boolean?,
    @SerializedName("language")
    @Expose
    var language: String?,
    @SerializedName("otherCapital")
    @Expose
    var otherCapital: Double?,
    @SerializedName("otherInvestments")
    @Expose
    var otherInvestments: Double?,
    @SerializedName("otherLoans")
    @Expose
    var otherLoans: Double?,
    @SerializedName("pension")
    @Expose
    var pension: Double?,
    @SerializedName("products")
    @Expose
    var products: Double?,
    @SerializedName("property")
    @Expose
    var `property`: Double?,
    @SerializedName("stockMarketInvestment")
    @Expose
    var stockMarketInvestment: Double?,
    @SerializedName("valueOfGold")
    @Expose
    var valueOfGold: Double?,
    @SerializedName("valueOfSilver")
    @Expose
    var valueOfSilver: Double?,
    @SerializedName("year")
    @Expose
    var year: Int?,
    @SerializedName("createdOn")
    @Expose
    var createdOn: String? = "",
    @SerializedName("id")
    @Expose
    var id: String? = ""

)