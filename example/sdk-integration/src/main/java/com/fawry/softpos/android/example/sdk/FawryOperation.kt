package com.fawry.softpos.android.example.sdk

/**
 * Operations exposed by the demo apps.
 *
 * Each enum maps one UI option to the matching Fawry SDK request builder in [FawrySdkClient].
 */
enum class FawryOperation(val label: String, val actionLabel: String, val needsReference: Boolean) {
    CARD_SALE("Card Sale", "Pay Now", false),
    CARD_REFUND("Card Refund", "Refund", true),
    CARD_VOID("Card Void", "Void", true),
    INQUIRY("Inquiry", "Run Inquiry", true),
    CLEAR_CACHE("Clear Cache", "Clear Cache", false);

    companion object {
        val labels = entries.map { it.label }
    }
}
