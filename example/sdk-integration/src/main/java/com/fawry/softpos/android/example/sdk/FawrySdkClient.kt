package com.fawry.softpos.android.example.sdk

import android.content.Context
import com.fawry.softpos.retailer.connect.FawryConnect
import com.fawry.softpos.retailer.connect.model.connection.ConnectionType
import com.fawry.softpos.retailer.connect.model.messages.user.UserData
import com.fawry.softpos.retailer.connect.model.messages.user.UserType
import com.fawry.softpos.retailer.connect.model.payment.PaymentOptionType
import com.fawry.softpos.retailer.connect.model.payment.inquiry.IdType
import com.fawry.softpos.retailer.ipc.IPCConnectivity
import com.fawry.softpos.retailer.modelBuilder.clearCache.ClearCache
import com.fawry.softpos.retailer.modelBuilder.inquiry.Inquiry
import com.fawry.softpos.retailer.modelBuilder.refund.CardRefund
import com.fawry.softpos.retailer.modelBuilder.sale.CardSale
import com.fawry.softpos.retailer.modelBuilder.void.CardVoid
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Demo integration wrapper for Fawry TapNPay SoftPOS.
 *
 * This class is intentionally kept UI-free so clients can reuse the same SDK calling pattern
 * from Activities, Fragments, ViewModels, or their own architecture layer. The sample apps pass
 * user-entered data into this client and receive simple callbacks that can be mapped to UI state.
 */
class FawrySdkClient(context: Context) {
    private val applicationContext = context.applicationContext
    private var fawryConnect: FawryConnect? = null

    /**
     * Creates the IPC connection to TapNPay using merchant onboarding credentials.
     *
     * Call this before sending any transaction request. Keep the callback UI-safe in the caller,
     * because the SDK may invoke it from a background thread.
     */
    fun connect(credentials: FawryCredentials, callback: ConnectionCallback) {
        val sdkCallback = FawryConnect.OnConnectionCallBack(
            onConnected = callback::onConnected,
            onDisconnected = callback::onDisconnected,
            onFailure = { errorCode, cause ->
                callback.onFailure(errorCode.toString(), cause)
            }
        )

        fawryConnect = FawryConnect.setup<IPCConnectivity.Builder>(
            ConnectionType.IPC,
            buildUserData(credentials)
        )
            .setContext(applicationContext)
            .setConnectionCallBack(sdkCallback)
            .connect()
    }

    fun disconnect() {
        fawryConnect?.disConnect()
        fawryConnect = null
    }

    fun isConnected(): Boolean = fawryConnect?.isServiceConnected() == true

    /**
     * Sends one of the supported demo operations through the active TapNPay connection.
     *
     * The request model carries strings exactly as collected by the sample UI. This keeps the demo
     * easy to follow while the SDK builder calls below show where each field is mapped.
     */
    fun sendTransaction(
        operation: FawryOperation,
        request: FawryTransactionRequest,
        callback: TransactionCallback,
    ) {
        val sdkCallback = FawryConnect.OnTransactionCallBack(
            onTransactionRequestSuccess = { response ->
                callback.onSuccess(FawryResponseFormatter.format(response))
            },
            onTransactionRequestFailure = { errorCode, cause ->
                callback.onFailure(errorCode.toString(), errorCode.getCode().toString(), cause)
            }
        )

        when (operation) {
            FawryOperation.CARD_SALE -> sendSale(request, sdkCallback)
            FawryOperation.CARD_REFUND -> sendRefund(request, sdkCallback)
            FawryOperation.CARD_VOID -> sendVoid(request, sdkCallback)
            FawryOperation.INQUIRY -> sendInquiry(request, sdkCallback)
            FawryOperation.CLEAR_CACHE -> sendClearCache(request, sdkCallback)
        }
    }

    private fun buildUserData(credentials: FawryCredentials): UserData {
        // Partner mode sends the partner code; merchant-token mode lets transaction builders
        // provide merchant token and account number with each request.
        return if (credentials.isPartnerMode) {
            UserData(
                partnerCode = credentials.partnerCode.trim(),
                userType = UserType.MCC
            )
        } else {
            UserData(userType = UserType.MCC)
        }
    }

    private fun sendSale(request: FawryTransactionRequest, callback: FawryConnect.OnTransactionCallBack) {
        fawryConnect
            ?.requestSale<CardSale.Builder>(PaymentOptionType.CARD)
            ?.setAmount(request.amount.toDoubleOrNull() ?: 0.0)
            ?.setCurrency("EGP")
            ?.setOrderID(request.orderId)
            ?.setBtc(request.btc.toLongOrNull() ?: 99901L)
            ?.setMerchantToken(request.merchantToken.trim())
            ?.setMerchantAccountNumber(request.merchantAccountNumber.trim())
            ?.send(callback)
    }

    private fun sendRefund(request: FawryTransactionRequest, callback: FawryConnect.OnTransactionCallBack) {
        fawryConnect
            ?.requestRefund<CardRefund.Builder>(PaymentOptionType.CARD)
            ?.setAmount(request.amount.toDoubleOrNull() ?: 0.0)
            ?.setOrderID(request.orderId)
            ?.setBtc(request.btc.toLongOrNull() ?: 99901L)
            ?.setMerchantToken(request.merchantToken.trim())
            ?.setMerchantAccountNumber(request.merchantAccountNumber.trim())
            ?.send(callback)
    }

    private fun sendVoid(request: FawryTransactionRequest, callback: FawryConnect.OnTransactionCallBack) {
        fawryConnect
            ?.requestVoid<CardVoid.Builder>(PaymentOptionType.CARD)
            ?.setOrderID(request.orderId)
            ?.setBtc(request.btc.toLongOrNull() ?: 99901L)
            ?.setMerchantToken(request.merchantToken.trim())
            ?.setMerchantAccountNumber(request.merchantAccountNumber.trim())
            ?.send(callback)
    }

    private fun sendInquiry(request: FawryTransactionRequest, callback: FawryConnect.OnTransactionCallBack) {
        // Demo inquiry searches the last 30 days for the provided FCRN/reference number.
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        val now = dateFormat.format(Date())
        val monthAgo = dateFormat.format(Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000))

        fawryConnect
            ?.requestInquiry<Inquiry.Builder>()
            ?.setidType(IdType.FCRN)
            ?.setTransactionId(request.referenceNumber.trim())
            ?.setFromDate(monthAgo)
            ?.setToDate(now)
            ?.setMerchantToken(request.merchantToken.trim())
            ?.setMerchantAccountNumber(request.merchantAccountNumber.trim())
            ?.send(callback)
    }

    private fun sendClearCache(request: FawryTransactionRequest, callback: FawryConnect.OnTransactionCallBack) {
        fawryConnect
            ?.requestClearCache<ClearCache.Builder>()
            ?.setClearSecurityKeys(true)
            ?.setClearProfile(true)
            ?.setBtc(request.btc.toLongOrNull() ?: 99901L)
            ?.setMerchantToken(request.merchantToken.trim())
            ?.setMerchantAccountNumber(request.merchantAccountNumber.trim())
            ?.send(callback)
    }

    /**
     * Connection callbacks are intentionally SDK-agnostic so client apps do not need to import
     * Fawry SDK callback types in their UI layer.
     */
    interface ConnectionCallback {
        fun onConnected()
        fun onDisconnected()
        fun onFailure(errorCode: String, cause: Throwable?)
    }

    /**
     * Transaction callbacks return a formatted response for display plus simple failure details.
     * Production apps can replace this with their own domain result model.
     */
    interface TransactionCallback {
        fun onSuccess(formattedResponse: String)
        fun onFailure(errorCode: String, errorCodeValue: String, cause: Throwable?)
    }
}
