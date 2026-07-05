package com.fawry.softpos.android.example.sdk

import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * Formats the raw TapNPay JSON response for display in the demo apps.
 *
 * Integrators can keep the raw response, parse it into their own model, or replace this formatter
 * with their production receipt/status handling.
 */
object FawryResponseFormatter {
    fun format(response: String): String {
        return try {
            val json = Gson().fromJson(response, JsonObject::class.java)
            val statusCode = json.getAsJsonObject("header")
                ?.getAsJsonObject("status")
                ?.get("statusCode")?.asInt
            val statusDesc = json.getAsJsonObject("header")
                ?.getAsJsonObject("status")
                ?.get("statusDesc")?.asString
            val fcrn = json.getAsJsonObject("body")?.get("fawryReference")?.asString

            buildString {
                append("Status: $statusCode - $statusDesc\n")
                if (!fcrn.isNullOrBlank()) append("FCRN: $fcrn\n")
                append("\n--- Full Response ---\n")
                append(response)
            }
        } catch (e: Exception) {
            response
        }
    }
}
