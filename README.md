# Fawry SoftPOS Android SDK

Integrate card payments into your Android app using the Fawry SoftPOS (TapNPay) app. The Android Payment SDK communicates with TapNPay by binding to the SoftPOS service and receiving transaction results through callbacks.

**[View Full Documentation](https://fawry-tech.github.io/fawry-soft-pos-android-sdk-docs/)**

## Architecture

Your merchant app integrates the **Payment SDK** and binds to the Fawry **TapNPay** (SoftPOS) app over IPC/AIDL. The SDK sends operation requests to TapNPay, TapNPay handles the payment UI and card processing, then returns the transaction result through callbacks.

```
┌──────────────────────┐      bind             ┌──────────────────────┐
│  Merchant Android App│ ────────────────────▶ │      TapNPay         │
│  + Payment SDK       │ ◀──── onConnected ─── │  (SoftPOS app)       │
└──────────┬───────────┘                       └──────────┬───────────┘
           │                                              │
           │  1. Build operation request                  │  3. Open TapNPay UI
           │  2. send()                                   │  4. Process card
           ▼                                              ▼
┌──────────────────────┐      callback         ┌──────────────────────┐
│  Transaction Callback│ ◀──────────────────── │  Transaction Result  │
│  Success / Failure   │                       │  JSON or ErrorCode   │
└──────────────────────┘                       └──────────────────────┘
```

1. Your app connects to TapNPay with `FawryConnect.setup(...).connect()`.
2. After `onConnected`, your app builds a sale, refund, void, inquiry, or clear-cache request and calls `.send()`.
3. TapNPay opens its payment flow, processes the operation, and completes the transaction.
4. Your app receives the result in `onTransactionRequestSuccess` or `onTransactionRequestFailure`.

## Supported Operations

| Operation     | Description                         |
|---------------|-------------------------------------|
| Card Sale     | Accept a card payment               |
| Card Refund   | Refund a previous transaction       |
| Card Void     | Void/cancel a recent transaction    |
| Inquiry       | Query the status of a transaction   |
| Clear Cache   | Clear SoftPOS app cache and keys    |

## Quick Start

### 1. Add the SDK

Place `tapnpay-payment-sdk.aar` in your app's `libs/` directory:

```gradle
dependencies {
    implementation fileTree(dir: "libs", include: ["*.aar"])
}
```

### 2. Configure AndroidManifest

```xml
<queries>
    <package android:name="com.fawry.softpos" />
</queries>
```

### 3. Connect to TapNPay

```kotlin
val connectionCallback = FawryConnect.OnConnectionCallBack(
    onConnected = { /* ready to send transactions */ },
    onDisconnected = { },
    onFailure = { errorCode, cause -> Log.e("TAG", "Connection failed: $errorCode", cause) }
)

val fawryConnect = FawryConnect.setup<IPCConnectivity.Builder>(
    ConnectionType.IPC,
    UserData(userType = UserType.MCC)
)
    .setContext(applicationContext)
    .setConnectionCallBack(connectionCallback)
    .connect()
```

### 4. Make a Payment

```kotlin
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
            onTransactionRequestSuccess = { response -> Log.d("TAG", "Success: $response") },
            onTransactionRequestFailure = { errorCode, cause ->
                Log.e("TAG", "Failed: $errorCode", cause)
            }
        )
    )
```

## Example Sample Apps

This repository includes three runnable samples that demonstrate a complete TapNPay integration — an Android View-based app, an Android Jetpack Compose app, and a Flutter app. All three show how to add the Payment SDK, connect to TapNPay, and run sale, refund, void, inquiry, and clear-cache operations, and all three reuse the same `FawrySdkClient` wrapper from `example/sdk-integration/`.

| Module | UI | Key files |
|--------|-----|-----------|
| `example/app/` | Android XML Views | `MainActivity.kt`, `activity_main.xml` |
| `example/app-compose/` | Android Jetpack Compose | `MainActivity.kt`, `FawryDemoViewModel.kt`, `FawryDemoScreen.kt` |
| `example/sdk-integration/` | Android library (shared, no UI) | `FawrySdkClient.kt` |
| `tapnpay_flutter_example/` | Flutter | `lib/main.dart`, `lib/tapnpay_client.dart`, `android/.../TapNPayBridge.kt` |

The two Android app modules and the Flutter app's native bridge all depend on `example/sdk-integration/`; place `tapnpay-payment-sdk.aar` there once and every sample reuses it.

### Run an Android Sample

1. Copy `tapnpay-payment-sdk.aar` into `example/sdk-integration/libs/`.
2. Open the `example/` folder in Android Studio.
3. Install the **Fawry TapNPay** app on a physical Android device.
4. Select the **app** or **app-compose** run configuration and build on the device.
5. Enter your merchant credentials and tap **Connect**, then run an operation.

See `example/README.md` for the full setup, credential modes, and troubleshooting steps.

### Run the Flutter Sample

1. Copy `tapnpay-payment-sdk.aar` into `example/sdk-integration/libs/` (same file as above).
2. From `tapnpay_flutter_example/`, run `flutter pub get`, then `flutter run` on a physical Android device with TapNPay installed.

See `tapnpay_flutter_example/README.md` for the full setup and platform-channel architecture.

## Requirements

- Fawry merchant account with SoftPOS credentials (`merchantAccountNumber`, `merchantToken`, and optionally `partnerCode`)
- Android device (API 23+) with the Fawry TapNPay app (`com.fawry.softpos`) installed
- Payment SDK `.aar` (version 1.1.0.0 or later) from Fawry

## Documentation

| Guide | Description |
|-------|-------------|
| [Getting Started](docs/getting-started.md) | Install the SDK and make your first payment |
| [Installation](docs/installation.md) | Detailed setup and ProGuard rules |
| [Connection Setup](docs/connection-setup.md) | Connection setup and authentication modes |
| [API Reference](docs/api-reference.md) | Full SDK API documentation |
| [Operations](docs/operations/) | Sale, refund, void, inquiry, clear cache |
| [Response Handling](docs/response-handling.md) | Parse transaction results |
| [Troubleshooting](docs/troubleshooting.md) | Common issues and fixes |

## License

Copyright Fawry Payment Solutions. All rights reserved.
