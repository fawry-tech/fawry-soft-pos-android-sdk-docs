---
title: Getting Started
nav_order: 2
---

# Getting Started

This guide walks you through the minimal steps to integrate the Fawry SoftPOS Android Payment SDK over IPC and process your first card payment.

---

## Prerequisites

Before you begin, make sure you have:

1. **Fawry merchant credentials** -- `merchantAccountNumber`, `merchantToken`, and optionally `partnerCode`
2. **Android device** (API 23+) with the Fawry TapNPay app installed
3. **Payment SDK `.aar`** file from Fawry (version 1.1.0.0 or later)
4. **Android Studio** with Kotlin support

---

## Step 1: Add the SDK

Place the `tapnpay-payment-sdk.aar` file in your app's `libs/` directory and add it to `build.gradle`:

```gradle
dependencies {
    implementation fileTree(dir: "libs", include: ["*.aar"])
    implementation "androidx.appcompat:appcompat:1.6.1"
    implementation "com.google.code.gson:gson:2.10.1"
}
```

See [Installation]({% link installation.md %}) for ProGuard rules and manifest configuration.

---

## Step 2: Configure AndroidManifest

Add a package query so your app can discover TapNPay:

```xml
<manifest ...>
    <queries>
        <package android:name="com.fawry.softpos" />
    </queries>
    ...
</manifest>
```

---

## Step 3: Connect via IPC

Create a connection callback and connect to TapNPay:

```kotlin
import com.fawry.softpos.retailer.connect.FawryConnect
import com.fawry.softpos.retailer.connect.model.ErrorCode
import com.fawry.softpos.retailer.connect.model.connection.ConnectionType
import com.fawry.softpos.retailer.connect.model.messages.user.UserData
import com.fawry.softpos.retailer.connect.model.messages.user.UserType
import com.fawry.softpos.retailer.ipc.IPCConnectivity

val connectionCallback = FawryConnect.OnConnectionCallBack(
    onConnected = {
        Log.d("FawryConnect", "Connected to TapNPay")
    },
    onDisconnected = {
        Log.d("FawryConnect", "Disconnected from TapNPay")
    },
    onFailure = { errorCode: ErrorCode.Connection, cause: Throwable? ->
        Log.e("FawryConnect", "Connection failed: $errorCode", cause)
    }
)

val fawryConnect: FawryConnect? = FawryConnect.setup<IPCConnectivity.Builder>(
    ConnectionType.IPC,
    UserData(userType = UserType.MCC)
)
    .setContext(applicationContext)
    .setConnectionCallBack(connectionCallback)
    .connect()
```

See [Connection Setup]({% link connection-setup.md %}) for authentication modes (credentials, merchant token, partner code).

---

## Step 4: Make a Payment

Use the same `fawryConnect` instance to send a card sale request:

```kotlin
import com.fawry.softpos.retailer.connect.model.payment.PaymentOptionType
import com.fawry.softpos.retailer.modelBuilder.sale.CardSale
import java.util.UUID

fawryConnect
    ?.requestSale<CardSale.Builder>(PaymentOptionType.CARD)
    ?.setAmount(100.00)
    ?.setCurrency("EGP")
    ?.setOrderID(UUID.randomUUID().toString())
    ?.setBtc(99901)
    ?.setMerchantToken("YOUR_MERCHANT_TOKEN")
    ?.setMerchantAccountNumber("YOUR_ACCOUNT_NUMBER")
    ?.setPrintReceipt(false)
    ?.setDisplayInvoice(true)
    ?.send(
        FawryConnect.OnTransactionCallBack(
            onTransactionRequestSuccess = { response ->
                Log.d("FawryConnect", "Payment success: $response")
            },
            onTransactionRequestFailure = { errorCode, cause ->
                Log.e("FawryConnect", "Payment failed: $errorCode", cause)
            }
        )
    )
```

TapNPay opens its payment UI on the device. When the transaction completes, your success or failure callback receives a JSON response string.

---

## Step 5: Handle the Response

Parse the JSON response to check status and extract the FCRN:

```kotlin
import com.google.gson.Gson
import com.google.gson.JsonObject

fun handleSuccess(response: String) {
    val json = Gson().fromJson(response, JsonObject::class.java)
    val statusCode = json.getAsJsonObject("header")
        ?.getAsJsonObject("status")
        ?.get("statusCode")?.asInt

    if (statusCode == 1) {
        val fcrn = json.getAsJsonObject("body")?.get("fawryReference")?.asString
        Log.d("FawryConnect", "Payment successful! FCRN: $fcrn")
    } else {
        val statusDesc = json.getAsJsonObject("header")
            ?.getAsJsonObject("status")
            ?.get("statusDesc")?.asString
        Log.e("FawryConnect", "Payment failed: $statusDesc")
    }
}
```

See [Response Handling]({% link response-handling.md %}) for the full response structure.

---

## Disconnect

When your app no longer needs the connection:

```kotlin
fawryConnect?.disConnect()
```

---

## Next Steps

- Read the <a href="{% link api-reference.md %}">API Reference</a> for the full list of builder methods
- See <a href="{% link operations/sale.md %}">Card Sale</a> for the supported sale flow
- Set up <a href="{% link operations/refund.md %}">Card Refund</a> and <a href="{% link operations/void.md %}">Card Void</a> flows
- Review the <a href="{% link troubleshooting.md %}">Troubleshooting</a> guide for common issues
- Run the [example apps](https://github.com/fawry-tech/fawry-soft-pos-android-sdk-docs/tree/main/example) (View-based or Jetpack Compose) for a complete working integration
