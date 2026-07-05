---
title: API Reference
nav_order: 6
---

# API Reference

Complete reference for the Fawry Payment SDK classes used in IPC integration.

---

## FawryConnect

Entry point for all Payment SDK operations.

### `FawryConnect.setup<T>(connectionType, userData)`

Create or retrieve a `FawryConnect` instance configured for IPC.

```kotlin
val builder = FawryConnect.setup<IPCConnectivity.Builder>(
    ConnectionType.IPC,
    UserData(userType = UserType.MCC)
)
```

**Parameters:**

| Name | Type | Required | Description |
|------|------|----------|-------------|
| `connectionType` | `ConnectionType` | Yes | Use `ConnectionType.IPC` |
| `userData` | `UserData` | Yes | Authentication configuration |

**Returns:** `IPCConnectivity.Builder`

---

### `requestSale<T>(paymentOptionType)`

Create a builder for a card sale request.

```kotlin
fawryConnect?.requestSale<CardSale.Builder>(PaymentOptionType.CARD)
```

**Returns:** `CardSale.Builder`

---

### `requestRefund<T>(paymentOptionType)`

Create a builder for a card refund request.

```kotlin
fawryConnect?.requestRefund<CardRefund.Builder>(PaymentOptionType.CARD)
```

**Returns:** `CardRefund.Builder`

---

### `requestVoid<T>(paymentOptionType)`

Create a builder for a card void request.

```kotlin
fawryConnect?.requestVoid<CardVoid.Builder>(PaymentOptionType.CARD)
```

**Returns:** `CardVoid.Builder`

---

### `requestInquiry<T>()`

Create a builder for a transaction inquiry.

```kotlin
fawryConnect?.requestInquiry<Inquiry.Builder>()
```

**Returns:** `Inquiry.Builder`

---

### `requestClearCache<T>()`

Create a builder for a clear cache request.

```kotlin
fawryConnect?.requestClearCache<ClearCache.Builder>()
```

**Returns:** `ClearCache.Builder`

---

### `isServiceConnected()`

Check whether the IPC service is currently bound.

```kotlin
val connected = fawryConnect?.isServiceConnected() == true
```

**Returns:** `Boolean`

---

### `disConnect()`

Unbind from TapNPay and release the connection.

```kotlin
fawryConnect?.disConnect()
```

---

## IPCConnectivity.Builder

Configure and establish the IPC connection.

| Method | Description |
|--------|-------------|
| `setContext(context)` | Application or activity context (required) |
| `setConnectionCallBack(callback)` | Connection lifecycle callback (required) |
| `connect()` | Bind to TapNPay; returns `FawryConnect?` |

---

## OnConnectionCallBack

```kotlin
FawryConnect.OnConnectionCallBack(
    onConnected = { },
    onDisconnected = { },
    onFailure = { errorCode: ErrorCode.Connection, cause: Throwable? -> }
)
```

---

## OnTransactionCallBack

```kotlin
FawryConnect.OnTransactionCallBack(
    onTransactionRequestSuccess = { response: String -> },
    onTransactionRequestFailure = { errorCode: ErrorCode.Payment, cause: Throwable? -> }
)
```

| Callback | Parameter | Description |
|----------|-----------|-------------|
| `onTransactionRequestSuccess` | `response: String` | JSON response from TapNPay |
| `onTransactionRequestFailure` | `errorCode`, `cause` | Payment-level error |

---

## Common Builder Methods

These methods are available on sale, refund, void, inquiry, and clear-cache builders:

| Method | Type | Required | Description |
|--------|------|----------|-------------|
| `setMerchantToken(token)` | `String` | Yes | Merchant secure token |
| `setMerchantAccountNumber(account)` | `String` | Yes | Merchant collection account |
| `setBtc(btc)` | `Long` | Yes | Bill type code |
| `setOrderID(orderId)` | `String` | Sale/Refund/Void | Unique transaction ID |
| `setPrintReceipt(print)` | `Boolean` | Yes | TapNPay prints receipt |
| `setDisplayInvoice(display)` | `Boolean` | Sale | TapNPay shows digital receipt |
| `setExtras(map)` | `Map<String, ExtraKey>` | No | Extra transaction data |
| `send(callback)` | `OnTransactionCallBack` | Yes | Execute the request |

---

## Enums

### ConnectionType

| Value | Description |
|-------|-------------|
| `IPC` | Inter-process communication (this guide) |
| `BLUETOOTH` | Bluetooth connection |
| `SERIAL` | USB serial connection |
| `DEEPLINK` | Deep link (web-style) |

### PaymentOptionType

| Value | Description |
|-------|-------------|
| `CARD` | Card payment |
| `QR` | Wallet QR |
| `R2P` | Request to pay |
| `CASH` | Cash sale |

### IdType

| Value | Description |
|-------|-------------|
| `FCRN` | Fawry Customer Reference Number |
| `ORDER_ID` | Merchant order ID |
| `CORRELATION_UID` | Wallet correlation UID |

### SearchType

| Value | Description |
|-------|-------------|
| `ID_TYPE` | Search by transaction ID |
| `CARD_PAN` | Search by card PAN |

### UserType

| Value | Description |
|-------|-------------|
| `MCC` | Merchant/collector authentication |
| `SOF` | SOF authentication |

---

## ErrorCode.Connection

| Code | Description |
|------|-------------|
| `SERVICE_NOT_EXIST` | TapNPay not installed |
| `ALREADY_CONNECTED` | Already connected |
| `ALREADY_DISCONNECTED` | Already disconnected |
| `UNKNOWN_ERROR` | Unexpected error |

## ErrorCode.Payment

| Code | Description |
|------|-------------|
| `CONNECTION_ERROR` | Not connected to TapNPay |
| `FORCE_UPDATE` | TapNPay needs update |
| `UNKNOWN_ERROR` | Unexpected payment error |
