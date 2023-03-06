/*
package com.gakk.noorlibrary.ui.fragments.subscription

import android.app.Activity
import android.content.Context
import android.util.Log
//import com.android.billingclient.api.*


class  BillingClientWrapper(context: Context) : PurchasesUpdatedListener{

    interface OnQueryProductsListener {
        fun onSuccess(products: List<SkuDetails>)
        fun onFailure(error: Error)
    }

    interface OnPurchaseListener {
        fun onPurchaseSuccess(purchase: Purchase?)
        fun onPurchaseFailure(error: Error)

    }

    var onPurchaseListener: OnPurchaseListener? = null

    class Error(val responseCode: Int, val debugMessage: String)

    val billingClient = BillingClient
        .newBuilder(context)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    fun purchase(activity: Activity, product: SkuDetails) {
        onConnected {
            activity.runOnUiThread {
                billingClient.launchBillingFlow(
                   activity,
                    BillingFlowParams.newBuilder().setSkuDetails(product).build()
                )

            }
        }
    }
    interface OnQueryActivePurchasesListener {
        fun onSuccessInActivePurchase(activePurchases: List<Purchase>)
        fun onFailure(error: Error)
    }

    fun queryActivePurchasesForType(
        @BillingClient.ProductType type: QueryPurchasesParams,
        listener: PurchasesResponseListener
    ) {
        onConnected {
            billingClient.queryPurchasesAsync(type, listener)
        }
    }








    fun queryProducts(listener: OnQueryProductsListener) {
        val skusList = listOf("noor_monthly_pack", "noor_yearly_pack")

        queryProductsForType(
            skusList,
            BillingClient.SkuType.SUBS
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = skuDetailsList ?: mutableListOf()
                queryProductsForType(
                    skusList,
                    BillingClient.SkuType.INAPP
                ) { billingResult, skuDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        products.addAll(skuDetailsList ?: listOf())
                        listener.onSuccess(products)
                    } else {
                        listener.onFailure(
                            Error(billingResult.responseCode, billingResult.debugMessage)
                        )
                    }
                }
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    private fun queryProductsForType(
        skusList: List<String>,
        @BillingClient.SkuType type: String,
        listener: SkuDetailsResponseListener
    ) {
        onConnected {
            billingClient.querySkuDetailsAsync(
                SkuDetailsParams.newBuilder().setSkusList(skusList).setType(type).build(),
                listener
            )
        }
    }

    private fun onConnected(block: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                block()
//                billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
//                    PurchaseHistoryResponseListener { billingResult, purchasesList ->
//                        val responseCode = billingResult.responseCode
//                        Log.d("Tag", "clicked123" )
//                        if (responseCode == BillingClient.BillingResponseCode.OK) {
//                            if (purchasesList == null || purchasesList.size == 0) {
//                                Log.d("Tag", "clicked1234" )
//                                //                        textView1.setText("No History")
//                            } else {
//                                for (purchase: PurchaseHistoryRecord in purchasesList) {
//                                    // Process the result.
//                                    //                            textView1.setText(
//                                    //                                "Purchase History="
//                                    //                                        + purchase.toString() + "\n"
//                                    //                            )
//                                    //Log.d("Tag", "clicked" + purchase.toString())
//                                    Toast.makeText(
//                                        context,
//                                        "Clicked :"+ purchase.toString(), Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        } else {
//                            Log.d("Tag","ERROR: "+ responseCode)
//                            //showResponseCode(responseCode)
//                        }
//                    })
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchaseList: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchaseList == null) {

                    onPurchaseListener?.onPurchaseSuccess(null)




                    return
                }

                purchaseList.forEach(::processPurchase)
            }

            else -> {
                //error occured or user canceled
                onPurchaseListener?.onPurchaseFailure(
                    Error(
                        billingResult.responseCode,
                        billingResult.debugMessage
                    )
                )
            }
        }
    }



    private fun processPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            onPurchaseListener?.onPurchaseSuccess(purchase)
           if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase) { billingResult ->
                    if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {


                        Log.d("TAG","purchase id :" + purchase.orderId)
                        Log.d("TAG","purchase token :" + purchase.purchaseToken)
                        Log.d("TAG","purchase time :" + purchase.purchaseTime)
                        Log.d("TAG","purchase state :" + purchase.purchaseState)
                    }
                }
            }


        }
    }

    private fun acknowledgePurchase(
        purchase: Purchase,
        callback: AcknowledgePurchaseResponseListener
    ) {
        onConnected {
            billingClient.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                    .build(),
                callback::onAcknowledgePurchaseResponse
            )
        }
    }

    private fun consumePurchase(purchase: Purchase, callback: ConsumeResponseListener) {
        onConnected {
            billingClient.consumeAsync(
                ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            ) { billingResult, purchaseToken ->
                callback.onConsumeResponse(billingResult, purchaseToken)
            }
        }
    }




}*/
