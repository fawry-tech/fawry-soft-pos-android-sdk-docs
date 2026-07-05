package com.fawry.softpos.android.example.sdk

/**
 * Credentials required to establish the TapNPay IPC connection.
 *
 * The sample supports both onboarding modes shown in the UI:
 * partner-code mode and merchant-token mode.
 */
data class FawryCredentials(
    val isPartnerMode: Boolean,
    val partnerCode: String,
)
