# TapNPay Flutter Example

A Flutter sample demonstrating Fawry SoftPOS **IPC** integration with the TapNPay app, for teams building a Flutter merchant app on Android.

The SDK integration logic is not reimplemented here — the native Android bridge reuses the same shared `FawrySdkClient` from [`example/sdk-integration`](../example/sdk-integration) that the View-based and Jetpack Compose Android samples use, via a cross-project Gradle module include. Only the platform-channel bridge and the Flutter UI are specific to this app.

## Architecture

```text
Flutter UI (lib/main.dart)
    │  calls
    ▼
TapNPayClient (lib/tapnpay_client.dart)
    │  MethodChannel "com.fawry.softpos.tapnpay_flutter_example/sdk"
    │  EventChannel  "com.fawry.softpos.tapnpay_flutter_example/sdk/connectionEvents"
    ▼
TapNPayBridge.kt (android/app/.../TapNPayBridge.kt)
    │  calls
    ▼
FawrySdkClient (example/sdk-integration, shared with the Android samples)
    │  calls
    ▼
Fawry Payment SDK (tapnpay-payment-sdk.aar) ⇄ TapNPay app (IPC)
```

- **MethodChannel** handles one-shot requests: `connect`, `disconnect`, `isConnected`, `sendTransaction`. Each resolves exactly once, matching the SDK's callback pattern.
- **EventChannel** streams connection status changes (`connected` / `disconnected` / `failed`), so the UI also reacts to spontaneous disconnects that are not tied to a specific `connect`/`disconnect` call.

## Prerequisites

- Flutter SDK (stable channel)
- Android Studio or a configured Android toolchain
- A physical Android device (API 23+) with the **Fawry TapNPay** app installed (`com.fawry.softpos`)
- `tapnpay-payment-sdk.aar` from Fawry (version 1.1.0.0 or later)
- Fawry merchant credentials: `merchantToken`, `merchantAccountNumber`, and optionally `partnerCode`

## 1. Add the Payment SDK

This app reuses the same AAR as the Android samples — place it once in `example/sdk-integration/libs/`:

```bash
cp /path/to/tapnpay-payment-sdk.aar ../example/sdk-integration/libs/
```

No copy is needed inside `tapnpay_flutter_example/` itself; the Gradle module include in `android/settings.gradle.kts` points directly at `example/sdk-integration`.

## 2. Install dependencies

```bash
flutter pub get
```

## 3. Install TapNPay

Install the Fawry TapNPay app on the same device you will run this example on. IPC requires both apps on the same device.

## 4. Run

```bash
flutter run
```

1. Enter your merchant credentials (Partner Code or Merchant Token mode, Merchant Token, Account Number).
2. Tap **Connect** and wait for "Connected".
3. Select an operation (Card Sale, Refund, Void, Inquiry, or Clear Cache) and fill in the required fields.
4. Tap the action button (e.g. **Pay Now**).
5. TapNPay opens its payment UI. Complete the transaction.
6. The formatted JSON response appears in the Response panel.

## Project Structure

```text
tapnpay_flutter_example/
├── android/
│   ├── app/src/main/kotlin/.../
│   │   ├── MainActivity.kt        ← registers the platform channels
│   │   └── TapNPayBridge.kt       ← bridges Dart calls to FawrySdkClient
│   ├── app/build.gradle.kts       ← depends on project(":sdk-integration")
│   └── settings.gradle.kts        ← includes example/sdk-integration by path
├── lib/
│   ├── main.dart                  ← app entry point
│   ├── tapnpay_client.dart        ← Dart wrapper around the platform channels
│   ├── models/                    ← FawryOperation, FawryCredentials, FawryTransactionRequest,
│   │                                 TapNPayConnectionEvent/Status, TapNPayException
│   ├── screens/
│   │   └── tapnpay_demo_page.dart ← demo UI (credentials, operations, response)
│   └── widgets/
│       └── demo_card.dart         ← shared titled-card layout
└── pubspec.yaml
```

## Credential Modes & Operations

See [`example/README.md`](../example/README.md#credential-modes) for the full explanation of Partner Code vs. Merchant Token mode and the required fields per operation — they are identical across all three samples (View, Compose, Flutter).

## Troubleshooting

- **Connection fails with `SERVICE_NOT_EXIST`** — install TapNPay (`com.fawry.softpos`) on the device and confirm the `<queries>` entry is present in `android/app/src/main/AndroidManifest.xml`.
- **Gradle sync fails to find `:sdk-integration`** — make sure this repository's `example/sdk-integration` folder exists alongside `tapnpay_flutter_example/` (both are part of the same monorepo checkout); the `settings.gradle.kts` include points at `../../example/sdk-integration`.
- **`tapnpay-payment-sdk.aar` not found** — confirm it is placed in `example/sdk-integration/libs/`, not inside this Flutter project.

See [Troubleshooting](../docs/troubleshooting.md) for more SDK-level issues.
