# Fawry SoftPOS IPC Examples

This folder contains two complete Android integration examples for the Fawry SoftPOS Payment SDK over **IPC**. Both samples show how a third-party merchant app installs the Payment SDK, binds to the TapNPay app, and runs sale, refund, void, inquiry, and clear-cache operations.

The SDK integration logic is the same in both examples — only the UI layer differs. Both reuse the shared [`sdk-integration/`](sdk-integration) module, which wraps the raw `FawryConnect` calls behind `FawrySdkClient`.

| Module | UI toolkit | Entry point |
|--------|------------|-------------|
| `app/` | XML Views + `AppCompatActivity` | `MainActivity.kt` + `activity_main.xml` |
| `app-compose/` | Jetpack Compose + `ComponentActivity` | `MainActivity.kt` + `FawryDemoScreen.kt` |
| `sdk-integration/` | Android library (no UI) | `FawrySdkClient.kt` |

> A third sample, [`tapnpay_flutter_example/`](../tapnpay_flutter_example), lives at the repository root and shows the same integration from a Flutter app. Its native Android bridge depends on this same `sdk-integration` module via a cross-project Gradle include — see its [README](../tapnpay_flutter_example/README.md).

## What Each Example Includes

### View-based (`app/`)

- `MainActivity.kt` — IPC connection lifecycle and all supported operations
- `activity_main.xml` — sample UI for credentials, connection, and transactions
- `libs/` — place your `tapnpay-payment-sdk.aar` here

### Jetpack Compose (`app-compose/`)

- `MainActivity.kt` — Compose entry point
- `FawryDemoViewModel.kt` — IPC connection lifecycle and transaction requests
- `FawryDemoScreen.kt` — Compose UI for credentials, connection, and transactions
- Reuses `tapnpay-payment-sdk.aar` from `app/libs/` (no second copy required)

## Prerequisites

Before running either example, make sure you have:

- Android Studio (latest stable)
- A physical Android device (API 23+) with the **Fawry TapNPay** app installed (`com.fawry.softpos`)
- `tapnpay-payment-sdk.aar` from Fawry (version 1.1.0.0 or later)
- Fawry merchant credentials: `merchantToken`, `merchantAccountNumber`, and optionally `partnerCode`

## Project Structure

```text
example/
├── app/                              ← View-based example
│   ├── libs/
│   │   └── tapnpay-payment-sdk.aar  ← add this file (not committed)
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/.../MainActivity.kt
│   │   └── res/
│   └── build.gradle
├── app-compose/                      ← Jetpack Compose example
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   └── java/.../
│   │       ├── MainActivity.kt
│   │       ├── FawryDemoViewModel.kt
│   │       └── FawryDemoScreen.kt
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## 1. Add the Payment SDK

Copy `tapnpay-payment-sdk.aar` into `app/libs/`:

```bash
cp /path/to/tapnpay-payment-sdk.aar app/libs/
```

The View-based app's `build.gradle` already includes:

```gradle
implementation fileTree(dir: "libs", include: ["*.aar"])
```

The Compose module reads the same AAR from `../app/libs/`, so you only need one copy.

## 2. Open in Android Studio

> **Important:** Open the **`example/`** folder, **not** `example/app/` or `example/app-compose/`.  
> Opening a module folder directly causes Gradle sync errors such as  
> `Task 'prepareKotlinBuildScriptModel' not found in project ':app-compose'`.

1. Open Android Studio.
2. Select **File → Open** and choose the **`example/`** folder.
3. Wait for Gradle sync to complete.
4. If you previously opened a module folder directly, close that project, delete any  
   `.idea` folder inside it, and reopen **`example/`**.

## 3. Install TapNPay

Install the Fawry TapNPay app on the same device where you will run the example. IPC requires both apps on the same device.

## 4. Run an Example

1. Connect a physical Android device (API 23+).
2. In the run configuration dropdown, choose either:
   - **app** — View-based example (`Fawry IPC Example`)
   - **app-compose** — Jetpack Compose example (`Fawry IPC Example (Compose)`)
3. Build and run the selected module.
4. Enter your merchant credentials:
   - **Partner Code** (if using partner mode)
   - **Merchant Token**
   - **Account Number**
   - **Bill Type Code (BTC)**
5. Tap **Connect** and wait for "Connected".
6. Select an operation (Card Sale, Refund, Void, Inquiry, or Clear Cache).
7. Tap the action button (e.g. **Pay Now**).
8. TapNPay opens its payment UI. Complete the transaction.
9. The JSON response appears in the Response panel.

## Integration Flow Demonstrated

```
Your App                    TapNPay App
   │                            │
   │── connect() IPC ──────────▶│ bind ConnectService
   │◀── onConnected ────────────│
   │                            │
   │── requestSale().send() ───▶│ process payment
   │◀── onTransactionSuccess ───│ JSON response
   │                            │
   │── disConnect() ───────────▶│ unbind
```

## Credential Modes

Both samples support two authentication modes via radio buttons:

| Mode | UserData | Transaction fields |
|------|----------|-------------------|
| Partner Code | `partnerCode` set | `merchantToken` + `merchantAccountNumber` |
| Merchant Token | `userType = MCC` only | `merchantToken` + `merchantAccountNumber` |

See the [Connection Setup](../docs/connection-setup.md) documentation for all authentication modes.

## Operations

| Operation | Required fields |
|-----------|----------------|
| Card Sale | amount, orderId, btc, merchantToken, merchantAccountNumber |
| Card Refund | amount, FCRN, orderId, btc, merchantToken, merchantAccountNumber |
| Card Void | FCRN, orderId, btc, merchantToken, merchantAccountNumber |
| Inquiry | FCRN, merchantToken, merchantAccountNumber |
| Clear Cache | btc, merchantToken, merchantAccountNumber |

## Troubleshooting

### SDK classes not found at compile time

Confirm `tapnpay-payment-sdk.aar` is in `app/libs/` and Gradle sync succeeded.

### Connection fails with SERVICE_NOT_EXIST

- Install TapNPay (`com.fawry.softpos`) on the device.
- Verify `<queries>` is in `AndroidManifest.xml`.

### Transaction callback never fires

- Do not disconnect while a transaction is in progress.
- Ensure you tapped **Connect** and saw "Connected" before sending.

### FORCE_UPDATE error

Update TapNPay to a version compatible with your Payment SDK.

See [Troubleshooting](../docs/troubleshooting.md) for more details.

## Security Notes

- Never commit real `merchantToken` values to source control.
- Use sandbox credentials for development and testing.
- Add ProGuard keep rules before enabling `minifyEnabled` in release builds.

## Next Steps

- Read the [Getting Started](../docs/getting-started.md) guide
- Review the [API Reference](../docs/api-reference.md)
- Explore individual [Operations](../docs/operations/) documentation
