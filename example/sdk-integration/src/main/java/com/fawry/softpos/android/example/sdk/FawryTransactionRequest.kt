package com.fawry.softpos.android.example.sdk

/**
 * Transaction input collected by the demo UI before it is mapped to Fawry SDK builders.
 *
 * Values are strings to keep this sample close to form input. Client apps can validate and convert
 * them earlier if their production flow has stricter requirements.
 */
data class FawryTransactionRequest(
    val amount: String,
    val btc: String,
    val orderId: String,
    val merchantToken: String,
    val merchantAccountNumber: String,
    val referenceNumber: String,
)
