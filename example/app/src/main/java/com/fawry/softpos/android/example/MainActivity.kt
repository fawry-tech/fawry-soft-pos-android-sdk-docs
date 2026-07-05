package com.fawry.softpos.android.example

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fawry.softpos.android.example.sdk.FawryCredentials
import com.fawry.softpos.android.example.sdk.FawryOperation
import com.fawry.softpos.android.example.sdk.FawrySdkClient
import com.fawry.softpos.android.example.sdk.FawryTransactionRequest
import java.util.UUID

/**
 * Sample activity demonstrating IPC integration with Fawry TapNPay.
 *
 * Flow:
 * 1. Enter merchant credentials
 * 2. Tap Connect to bind to TapNPay via IPC
 * 3. Select an operation and tap Send
 * 4. TapNPay processes the payment and returns JSON to the callback
 */
class MainActivity : AppCompatActivity() {

    private lateinit var fawrySdkClient: FawrySdkClient

    private lateinit var connectionStatus: TextView
    private lateinit var responseOutput: TextView
    private lateinit var connectButton: Button
    private lateinit var disconnectButton: Button
    private lateinit var sendButton: Button
    private lateinit var operationSpinner: Spinner
    private lateinit var partnerCodeInput: EditText
    private lateinit var merchantTokenInput: EditText
    private lateinit var merchantAccountInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var btcInput: EditText
    private lateinit var referenceInput: EditText
    private lateinit var orderIdInput: EditText
    private lateinit var referenceSection: View
    private lateinit var authModeGroup: RadioGroup
    private lateinit var responseScroll: ScrollView

    private val operations = FawryOperation.entries

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fawrySdkClient = FawrySdkClient(applicationContext)

        bindViews()
        setupOperationSpinner()
        setupButtons()
        generateOrderId()
    }

    private fun bindViews() {
        connectionStatus = findViewById(R.id.connectionStatus)
        responseOutput = findViewById(R.id.responseOutput)
        connectButton = findViewById(R.id.connectButton)
        disconnectButton = findViewById(R.id.disconnectButton)
        sendButton = findViewById(R.id.sendButton)
        operationSpinner = findViewById(R.id.operationSpinner)
        partnerCodeInput = findViewById(R.id.partnerCodeInput)
        merchantTokenInput = findViewById(R.id.merchantTokenInput)
        merchantAccountInput = findViewById(R.id.merchantAccountInput)
        amountInput = findViewById(R.id.amountInput)
        btcInput = findViewById(R.id.btcInput)
        referenceInput = findViewById(R.id.referenceInput)
        orderIdInput = findViewById(R.id.orderIdInput)
        referenceSection = findViewById(R.id.referenceSection)
        authModeGroup = findViewById(R.id.authModeGroup)
        responseScroll = findViewById(R.id.responseScroll)
    }

    private fun setupOperationSpinner() {
        operationSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            operations.map { it.label }
        )
        operationSpinner.setSelection(0)
        operationSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val operation = operations[position]
                referenceSection.visibility = if (operation.needsReference) View.VISIBLE else View.GONE
                sendButton.text = operation.actionLabel
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
    }

    private fun setupButtons() {
        connectButton.setOnClickListener { connectIpc() }
        disconnectButton.setOnClickListener { disconnectIpc() }
        sendButton.setOnClickListener { sendTransaction() }
        findViewById<Button>(R.id.newOrderIdButton).setOnClickListener { generateOrderId() }
    }

    private fun generateOrderId() {
        orderIdInput.setText(UUID.randomUUID().toString())
    }

    private fun connectIpc() {
        updateConnectionStatus("Connecting...")
        responseOutput.text = ""

        fawrySdkClient.connect(
            buildCredentials(),
            object : FawrySdkClient.ConnectionCallback {
                override fun onConnected() {
                    runOnUiThread {
                        updateConnectionStatus("Connected")
                        connectButton.isEnabled = false
                        disconnectButton.isEnabled = true
                        sendButton.isEnabled = true
                        Toast.makeText(this@MainActivity, "Connected to TapNPay", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onDisconnected() {
                    runOnUiThread {
                        updateConnectionStatus("Disconnected")
                        connectButton.isEnabled = true
                        disconnectButton.isEnabled = false
                        sendButton.isEnabled = false
                    }
                }

                override fun onFailure(errorCode: String, cause: Throwable?) {
                    runOnUiThread {
                        updateConnectionStatus("Failed: $errorCode")
                        showResponse("$errorCode\n${cause?.message}")
                        Log.e(TAG, "Connection failed: $errorCode", cause)
                    }
                }
            }
        )
    }

    private fun disconnectIpc() {
        fawrySdkClient.disconnect()
        updateConnectionStatus("Disconnected")
        connectButton.isEnabled = true
        disconnectButton.isEnabled = false
        sendButton.isEnabled = false
    }

    private fun buildCredentials(): FawryCredentials {
        val isPartnerMode = findViewById<RadioButton>(R.id.partnerModeRadio).isChecked
        return FawryCredentials(
            isPartnerMode = isPartnerMode,
            partnerCode = partnerCodeInput.text.toString().trim()
        )
    }

    private fun sendTransaction() {
        if (!fawrySdkClient.isConnected()) {
            Toast.makeText(this, "Connect to TapNPay first", Toast.LENGTH_SHORT).show()
            return
        }

        responseOutput.text = "Processing..."
        sendButton.isEnabled = false

        fawrySdkClient.sendTransaction(
            operations[operationSpinner.selectedItemPosition],
            buildTransactionRequest(),
            object : FawrySdkClient.TransactionCallback {
                override fun onSuccess(formattedResponse: String) {
                    runOnUiThread {
                        sendButton.isEnabled = true
                        showResponse(formattedResponse)
                    }
                }

                override fun onFailure(errorCode: String, errorCodeValue: String, cause: Throwable?) {
                    runOnUiThread {
                        sendButton.isEnabled = true
                        showResponse("$errorCode [$errorCodeValue]\n${cause?.message}")
                        Log.e(TAG, "Transaction failed: $errorCode", cause)
                    }
                }
            }
        )
    }

    private fun buildTransactionRequest(): FawryTransactionRequest {
        return FawryTransactionRequest(
            amount = amountInput.text.toString(),
            btc = btcInput.text.toString(),
            orderId = orderIdInput.text.toString(),
            merchantToken = merchantTokenInput.text.toString().trim(),
            merchantAccountNumber = merchantAccountInput.text.toString().trim(),
            referenceNumber = referenceInput.text.toString().trim()
        )
    }

    private fun updateConnectionStatus(status: String) {
        connectionStatus.text = "Connection: $status"
    }

    private fun showResponse(message: String) {
        responseOutput.text = message
        responseScroll.post {
            responseScroll.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroy() {
        fawrySdkClient.disconnect()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "FawryAndroidExample"
    }
}
