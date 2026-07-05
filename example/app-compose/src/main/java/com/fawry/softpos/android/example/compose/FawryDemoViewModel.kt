package com.fawry.softpos.android.example.compose

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fawry.softpos.android.example.sdk.FawryCredentials
import com.fawry.softpos.android.example.sdk.FawryOperation
import com.fawry.softpos.android.example.sdk.FawrySdkClient
import com.fawry.softpos.android.example.sdk.FawryTransactionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Holds IPC connection state and transaction logic for the Compose sample.
 *
 * The SDK integration is identical to the View-based example; only the UI layer differs.
 */
class FawryDemoViewModel(application: Application) : AndroidViewModel(application) {

    private val fawrySdkClient = FawrySdkClient(application.applicationContext)

    private val _uiState = MutableStateFlow(FawryDemoUiState(orderId = UUID.randomUUID().toString()))
    val uiState: StateFlow<FawryDemoUiState> = _uiState.asStateFlow()

    fun onPartnerModeChanged(isPartnerMode: Boolean) {
        _uiState.update { it.copy(isPartnerMode = isPartnerMode) }
    }

    fun onPartnerCodeChanged(value: String) {
        _uiState.update { it.copy(partnerCode = value) }
    }

    fun onMerchantTokenChanged(value: String) {
        _uiState.update { it.copy(merchantToken = value) }
    }

    fun onMerchantAccountChanged(value: String) {
        _uiState.update { it.copy(merchantAccountNumber = value) }
    }

    fun onOperationChanged(operation: FawryOperation) {
        _uiState.update { it.copy(selectedOperation = operation) }
    }

    fun onAmountChanged(value: String) {
        _uiState.update { it.copy(amount = value) }
    }

    fun onBtcChanged(value: String) {
        _uiState.update { it.copy(btc = value) }
    }

    fun onOrderIdChanged(value: String) {
        _uiState.update { it.copy(orderId = value) }
    }

    fun onReferenceChanged(value: String) {
        _uiState.update { it.copy(referenceNumber = value) }
    }

    fun generateOrderId() {
        _uiState.update { it.copy(orderId = UUID.randomUUID().toString()) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun connect() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                connectionStatus = "Connecting...",
                isConnecting = true,
                responseText = ""
            )
        }

        fawrySdkClient.connect(
            buildCredentials(state),
            object : FawrySdkClient.ConnectionCallback {
                override fun onConnected() {
                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                connectionStatus = "Connected",
                                isConnected = true,
                                isConnecting = false,
                                isSendEnabled = true,
                                toastMessage = "Connected to TapNPay"
                            )
                        }
                    }
                }

                override fun onDisconnected() {
                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                connectionStatus = "Disconnected",
                                isConnected = false,
                                isConnecting = false,
                                isSendEnabled = false
                            )
                        }
                    }
                }

                override fun onFailure(errorCode: String, cause: Throwable?) {
                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                connectionStatus = "Failed: $errorCode",
                                isConnected = false,
                                isConnecting = false,
                                isSendEnabled = false,
                                responseText = "$errorCode\n${cause?.message.orEmpty()}"
                            )
                        }
                        Log.e(TAG, "Connection failed: $errorCode", cause)
                    }
                }
            }
        )
    }

    fun disconnect() {
        fawrySdkClient.disconnect()
        _uiState.update {
            it.copy(
                connectionStatus = "Disconnected",
                isConnected = false,
                isConnecting = false,
                isSendEnabled = false
            )
        }
    }

    fun sendTransaction() {
        if (!fawrySdkClient.isConnected()) {
            _uiState.update { it.copy(toastMessage = "Connect to TapNPay first") }
            return
        }

        val state = _uiState.value
        _uiState.update {
            it.copy(
                responseText = "Processing...",
                isSending = true,
                isSendEnabled = false
            )
        }

        fawrySdkClient.sendTransaction(
            state.selectedOperation,
            buildTransactionRequest(state),
            object : FawrySdkClient.TransactionCallback {
                override fun onSuccess(formattedResponse: String) {
                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                responseText = formattedResponse,
                                isSending = false,
                                isSendEnabled = true
                            )
                        }
                    }
                }

                override fun onFailure(errorCode: String, errorCodeValue: String, cause: Throwable?) {
                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                responseText = "$errorCode [$errorCodeValue]\n${cause?.message.orEmpty()}",
                                isSending = false,
                                isSendEnabled = true
                            )
                        }
                        Log.e(TAG, "Transaction failed: $errorCode", cause)
                    }
                }
            }
        )
    }

    private fun buildCredentials(state: FawryDemoUiState): FawryCredentials {
        return FawryCredentials(
            isPartnerMode = state.isPartnerMode,
            partnerCode = state.partnerCode
        )
    }

    private fun buildTransactionRequest(state: FawryDemoUiState): FawryTransactionRequest {
        return FawryTransactionRequest(
            amount = state.amount,
            btc = state.btc,
            orderId = state.orderId,
            merchantToken = state.merchantToken,
            merchantAccountNumber = state.merchantAccountNumber,
            referenceNumber = state.referenceNumber
        )
    }

    override fun onCleared() {
        fawrySdkClient.disconnect()
        super.onCleared()
    }

    companion object {
        private const val TAG = "FawryAndroidCompose"
    }
}
