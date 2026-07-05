package com.fawry.softpos.android.example.compose

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fawry.softpos.android.example.sdk.FawryOperation
import com.fawry.softpos.android.example.compose.ui.theme.BackgroundGray
import com.fawry.softpos.android.example.compose.ui.theme.PrimaryBlue
import com.fawry.softpos.android.example.compose.ui.theme.ResponseBackground
import com.fawry.softpos.android.example.compose.ui.theme.TextSecondary

@Composable
fun FawryDemoScreen(viewModel: FawryDemoViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Fawry IPC Example (Compose)",
            color = PrimaryBlue,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "IPC integration sample for Fawry TapNPay",
            color = TextSecondary,
            fontSize = 14.sp
        )

        ConnectionCard(
            connectionStatus = uiState.connectionStatus,
            isConnectEnabled = !uiState.isConnected && !uiState.isConnecting,
            isDisconnectEnabled = uiState.isConnected || uiState.isConnecting,
            onConnect = viewModel::connect,
            onDisconnect = viewModel::disconnect
        )

        CredentialsCard(
            isPartnerMode = uiState.isPartnerMode,
            partnerCode = uiState.partnerCode,
            merchantToken = uiState.merchantToken,
            merchantAccountNumber = uiState.merchantAccountNumber,
            onPartnerModeChanged = viewModel::onPartnerModeChanged,
            onPartnerCodeChanged = viewModel::onPartnerCodeChanged,
            onMerchantTokenChanged = viewModel::onMerchantTokenChanged,
            onMerchantAccountChanged = viewModel::onMerchantAccountChanged
        )

        OperationCard(
            selectedOperation = uiState.selectedOperation,
            amount = uiState.amount,
            btc = uiState.btc,
            orderId = uiState.orderId,
            referenceNumber = uiState.referenceNumber,
            isSendEnabled = uiState.isSendEnabled && !uiState.isSending,
            onOperationChanged = viewModel::onOperationChanged,
            onAmountChanged = viewModel::onAmountChanged,
            onBtcChanged = viewModel::onBtcChanged,
            onOrderIdChanged = viewModel::onOrderIdChanged,
            onReferenceChanged = viewModel::onReferenceChanged,
            onGenerateOrderId = viewModel::generateOrderId,
            onSend = viewModel::sendTransaction
        )

        ResponseCard(responseText = uiState.responseText)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ConnectionCard(
    connectionStatus: String,
    isConnectEnabled: Boolean,
    isDisconnectEnabled: Boolean,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    ExampleCard(title = "Connection") {
        Text(
            text = "Connection: $connectionStatus",
            color = TextSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onConnect,
                enabled = isConnectEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text("Connect")
            }
            OutlinedButton(
                onClick = onDisconnect,
                enabled = isDisconnectEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text("Disconnect")
            }
        }
    }
}

@Composable
private fun CredentialsCard(
    isPartnerMode: Boolean,
    partnerCode: String,
    merchantToken: String,
    merchantAccountNumber: String,
    onPartnerModeChanged: (Boolean) -> Unit,
    onPartnerCodeChanged: (String) -> Unit,
    onMerchantTokenChanged: (String) -> Unit,
    onMerchantAccountChanged: (String) -> Unit,
) {
    ExampleCard(title = "Credentials") {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isPartnerMode,
                    onClick = { onPartnerModeChanged(true) }
                )
                Text("Partner Code")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !isPartnerMode,
                    onClick = { onPartnerModeChanged(false) }
                )
                Text("Merchant Token")
            }
        }

        OutlinedTextField(
            value = partnerCode,
            onValueChange = onPartnerCodeChanged,
            label = { Text("Partner Code") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = merchantToken,
            onValueChange = onMerchantTokenChanged,
            label = { Text("Merchant Token") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = merchantAccountNumber,
            onValueChange = onMerchantAccountChanged,
            label = { Text("Account Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OperationCard(
    selectedOperation: FawryOperation,
    amount: String,
    btc: String,
    orderId: String,
    referenceNumber: String,
    isSendEnabled: Boolean,
    onOperationChanged: (FawryOperation) -> Unit,
    onAmountChanged: (String) -> Unit,
    onBtcChanged: (String) -> Unit,
    onOrderIdChanged: (String) -> Unit,
    onReferenceChanged: (String) -> Unit,
    onGenerateOrderId: () -> Unit,
    onSend: () -> Unit,
) {
    var operationExpanded by remember { mutableStateOf(false) }

    ExampleCard(title = "Operation") {
        ExposedDropdownMenuBox(
            expanded = operationExpanded,
            onExpandedChange = { operationExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedOperation.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Operation") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = operationExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = operationExpanded,
                onDismissRequest = { operationExpanded = false }
            ) {
                FawryOperation.entries.forEach { operation ->
                    DropdownMenuItem(
                        text = { Text(operation.label) },
                        onClick = {
                            onOperationChanged(operation)
                            operationExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChanged,
            label = { Text("Amount") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = btc,
            onValueChange = onBtcChanged,
            label = { Text("Bill Type Code (BTC)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = orderId,
                onValueChange = onOrderIdChanged,
                label = { Text("Order ID") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            TextButton(onClick = onGenerateOrderId) {
                Text("New")
            }
        }

        if (selectedOperation.needsReference) {
            OutlinedTextField(
                value = referenceNumber,
                onValueChange = onReferenceChanged,
                label = { Text("Reference Number (FCRN)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true
            )
        }

        Button(
            onClick = onSend,
            enabled = isSendEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(selectedOperation.actionLabel)
        }
    }
}

@Composable
private fun ResponseCard(responseText: String) {
    val responseScrollState = rememberScrollState()

    LaunchedEffect(responseText) {
        responseScrollState.animateScrollTo(responseScrollState.maxValue)
    }

    ExampleCard(title = "Response") {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(ResponseBackground)
                    .verticalScroll(responseScrollState)
                    .padding(8.dp)
            ) {
                Text(
                    text = responseText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ExampleCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}
