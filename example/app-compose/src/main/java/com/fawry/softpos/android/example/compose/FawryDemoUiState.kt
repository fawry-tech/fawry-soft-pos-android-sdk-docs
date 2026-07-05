package com.fawry.softpos.android.example.compose

import com.fawry.softpos.android.example.sdk.FawryOperation

data class FawryDemoUiState(
    val connectionStatus: String = "Disconnected",
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isSendEnabled: Boolean = false,
    val isSending: Boolean = false,
    val isPartnerMode: Boolean = true,
    val partnerCode: String = "100",
    val merchantToken: String = "",
    val merchantAccountNumber: String = "ACCOUNT_NUMBER",
    val selectedOperation: FawryOperation = FawryOperation.CARD_SALE,
    val amount: String = "10.00",
    val btc: String = "99901",
    val orderId: String = "",
    val referenceNumber: String = "",
    val responseText: String = "Response will appear here...",
    val toastMessage: String? = null,
)
