package com.fawry.softpos.tapnpay_flutter_example

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.fawry.softpos.android.example.sdk.FawryCredentials
import com.fawry.softpos.android.example.sdk.FawryOperation
import com.fawry.softpos.android.example.sdk.FawrySdkClient
import com.fawry.softpos.android.example.sdk.FawryTransactionRequest
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Native bridge that exposes the shared [FawrySdkClient] (from `example/sdk-integration`) to the
 * Flutter app over platform channels.
 *
 * Design:
 * - A [MethodChannel] handles one-shot requests (`connect`, `disconnect`, `isConnected`,
 *   `sendTransaction`) and resolves exactly once per call, mirroring the SDK callback pattern.
 * - An [EventChannel] streams connection status changes so the Dart UI also reflects
 *   spontaneous disconnects that happen outside of a direct `connect`/`disconnect` call.
 *
 * Client apps integrating their own Flutter bridge can copy this class as a starting point;
 * only the platform channel plumbing is TapNPay-example specific, all SDK calls go through
 * [FawrySdkClient].
 */
class TapNPayBridge(context: Context) : MethodChannel.MethodCallHandler, EventChannel.StreamHandler {

    companion object {
        private const val METHOD_CHANNEL = "com.fawry.softpos.tapnpay_flutter_example/sdk"
        private const val EVENT_CHANNEL = "com.fawry.softpos.tapnpay_flutter_example/sdk/connectionEvents"
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val fawrySdkClient = FawrySdkClient(context)
    private var connectionEventSink: EventChannel.EventSink? = null

    fun register(messenger: BinaryMessenger) {
        MethodChannel(messenger, METHOD_CHANNEL).setMethodCallHandler(this)
        EventChannel(messenger, EVENT_CHANNEL).setStreamHandler(this)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        connectionEventSink = events
    }

    override fun onCancel(arguments: Any?) {
        connectionEventSink = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "connect" -> handleConnect(call, result)
            "disconnect" -> handleDisconnect(result)
            "isConnected" -> runOnMain { result.success(fawrySdkClient.isConnected()) }
            "sendTransaction" -> handleSendTransaction(call, result)
            else -> runOnMain { result.notImplemented() }
        }
    }

    private fun handleConnect(call: MethodCall, result: MethodChannel.Result) {
        val credentials = FawryCredentials(
            isPartnerMode = call.argument<Boolean>("isPartnerMode") ?: true,
            partnerCode = call.argument<String>("partnerCode").orEmpty(),
        )

        // The SDK may invoke onConnected/onFailure once for this request, but onDisconnected can
        // also fire later on its own; only the first callback resolves the pending method result.
        val resultResolved = AtomicBoolean(false)

        fawrySdkClient.connect(
            credentials,
            object : FawrySdkClient.ConnectionCallback {
                override fun onConnected() {
                    emitConnectionEvent("connected")
                    resolveOnce(resultResolved) { result.success(null) }
                }

                override fun onDisconnected() {
                    emitConnectionEvent("disconnected")
                }

                override fun onFailure(errorCode: String, cause: Throwable?) {
                    emitConnectionEvent("failed", errorCode, cause?.message)
                    resolveOnce(resultResolved) {
                        result.error(errorCode, cause?.message, null)
                    }
                }
            }
        )
    }

    private fun handleDisconnect(result: MethodChannel.Result) {
        fawrySdkClient.disconnect()
        emitConnectionEvent("disconnected")
        runOnMain { result.success(null) }
    }

    private fun handleSendTransaction(call: MethodCall, result: MethodChannel.Result) {
        val operation = FawryOperation.entries.firstOrNull { it.name == call.argument<String>("operation") }
        if (operation == null) {
            runOnMain { result.error("INVALID_OPERATION", "Unknown operation: ${call.argument<String>("operation")}", null) }
            return
        }

        val request = FawryTransactionRequest(
            amount = call.argument<String>("amount").orEmpty(),
            btc = call.argument<String>("btc").orEmpty(),
            orderId = call.argument<String>("orderId").orEmpty(),
            merchantToken = call.argument<String>("merchantToken").orEmpty(),
            merchantAccountNumber = call.argument<String>("merchantAccountNumber").orEmpty(),
            referenceNumber = call.argument<String>("referenceNumber").orEmpty(),
        )

        fawrySdkClient.sendTransaction(
            operation,
            request,
            object : FawrySdkClient.TransactionCallback {
                override fun onSuccess(formattedResponse: String) {
                    runOnMain { result.success(formattedResponse) }
                }

                override fun onFailure(errorCode: String, errorCodeValue: String, cause: Throwable?) {
                    runOnMain { result.error(errorCode, cause?.message, errorCodeValue) }
                }
            }
        )
    }

    private fun emitConnectionEvent(status: String, errorCode: String? = null, message: String? = null) {
        runOnMain {
            connectionEventSink?.success(
                mapOf(
                    "status" to status,
                    "errorCode" to errorCode,
                    "message" to message,
                )
            )
        }
    }

    private fun resolveOnce(resolved: AtomicBoolean, action: () -> Unit) {
        if (resolved.compareAndSet(false, true)) {
            runOnMain(action)
        }
    }

    private fun runOnMain(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            mainHandler.post(action)
        }
    }
}
