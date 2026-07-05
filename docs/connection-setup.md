---
title: Connection Setup
nav_order: 4
---

# Connection Setup

IPC is the recommended connection type for Android-to-Android integration. Your app binds to the TapNPay `ConnectService` and exchanges transaction requests over AIDL.

---

## Connection Flow

1. Create a `FawryConnect` instance with `ConnectionType.IPC`.
2. Provide `UserData` for authentication configuration.
3. Set the application `Context` and a connection callback.
4. Call `.connect()` to bind to TapNPay.
5. When `onConnected` fires, you can send transaction requests.
6. Call `disConnect()` when finished.

---

## Basic Connection

```kotlin
val connectionCallback = FawryConnect.OnConnectionCallBack(
    onConnected = { /* ready */ },
    onDisconnected = { /* service unbound */ },
    onFailure = { errorCode, cause ->
        Log.e("TAG", "Connection failed: $errorCode", cause)
    }
)

val fawryConnect = FawryConnect.setup<IPCConnectivity.Builder>(
    ConnectionType.IPC,
    UserData()
)
    .setContext(applicationContext)
    .setConnectionCallBack(connectionCallback)
    .connect()
```

---

## Authentication Modes

TapNPay supports multiple authentication modes. Configure `UserData` based on your onboarding:

### Partner Code Mode (recommended for third-party integrators)

Username and password are not required. Send `partnerCode` in `UserData` and provide `merchantToken` + `merchantAccountNumber` with every transaction.

```kotlin
UserData(
    partnerCode = "YOUR_PARTNER_CODE"
)
```

Transaction builders:

```kotlin![4.png](../../../../../../Downloads/doc-changes/4.png)
.setMerchantToken("![4.png](../../../../../../Downloads/doc-changes/4.png)YOUR_MERCHANT_T![4.png](../../../../../../Downloads/doc-changes/4.png)OKEN")
.setMerchantAccountNumber("YOUR_ACCOUNT_NUMBER")
```

### Merchant Token Mode

All merchant configuration is done at sandbox onboarding. Username and password are not required.

```kotlin
UserData()
```

### Credentials Mode

Provide TapNPay account username and password:

```kotlin
UserData(
    username = "your_username",
    password = "your_password"
)
```

### Credentials + Token Mode

Provide username along with merchant token and account number:

```kotlin
UserData(
    username = "your_username"
)
```

---

## UserData Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `username` | `String?` | Depends on mode | TapNPay account username |
| `password` | `String?` | Depends on mode | TapNPay account password |
| `partnerCode` | `String?` | Partner mode | Partner code from Fawry |
| `otpReceiverMobileNumber` | `String?` | Optional | OTP receiver mobile for partner flow |

---

## Connection Callbacks

| Callback | When it fires |
|----------|---------------|
| `onConnected` | IPC service is bound and ready |
| `onDisconnected` | Service was unbound |
| `onFailure` | Connection failed (app not installed, bind error, etc.) |

### Common Connection Error Codes

| Error | Description |
|-------|-------------|
| `SERVICE_NOT_EXIST` | TapNPay app is not installed |
| `ALREADY_CONNECTED` | Connection already established |
| `ALREADY_DISCONNECTED` | Disconnect called when not connected |
| `UNKNOWN_ERROR` | Unexpected bind or security error |

---

## Check Connection Status

```kotlin
val isConnected = fawryConnect?.isServiceConnected() == true
```

---

## Disconnect

```kotlin
fawryConnect?.disConnect()
```

Always disconnect when your activity is destroyed or you no longer need the connection:

```kotlin
override fun onDestroy() {
    fawryConnect?.disConnect()
    super.onDestroy()
}
```

---

## SDK Version Compatibility

Before each transaction, the SDK checks that the installed TapNPay version is compatible with your Payment SDK. If an update is required, `onTransactionRequestFailure` returns `ErrorCode.Payment.FORCE_UPDATE` and TapNPay may prompt the user to update.
