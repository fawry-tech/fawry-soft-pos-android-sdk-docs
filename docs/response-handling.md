---
title: Response Handling
nav_order: 7
---

# Response Handling

After TapNPay processes a transaction, your `OnTransactionCallBack.onTransactionRequestSuccess` receives a JSON string. Parse it to determine success or failure and extract transaction details.

---

## How Responses Work

1. Your app calls `.send()` on a transaction builder.
2. TapNPay opens its payment UI and processes the transaction.
3. On completion, the AIDL callback returns a JSON response string.
4. Your `onTransactionRequestSuccess` or `onTransactionRequestFailure` handler runs on the binder thread — marshal to the main thread if updating UI.

```kotlin
FawryConnect.OnTransactionCallBack(
    onTransactionRequestSuccess = { response ->
        runOnUiThread { handleResponse(response) }
    },
    onTransactionRequestFailure = { errorCode, cause ->
        runOnUiThread {
            showError("$errorCode: ${cause?.message}")
        }
    }
)
```

---

## Parsing the Response

```kotlin
import com.google.gson.Gson
import com.google.gson.JsonObject

fun handleResponse(response: String) {
    val json = Gson().fromJson(response, JsonObject::class.java)
    val header = json.getAsJsonObject("header")
    val status = header?.getAsJsonObject("status")
    val statusCode = status?.get("statusCode")?.asInt ?: -1
    val statusDesc = status?.get("statusDesc")?.asString ?: "Unknown"

    when (statusCode) {
        1 -> {
            val body = json.getAsJsonObject("body")
            val fcrn = body?.get("fawryReference")?.asString
            val amount = body?.get("amount")?.asDouble
            Log.d("TAG", "Success! FCRN=$fcrn, amount=$amount")
        }
        else -> {
            Log.e("TAG", "Transaction failed: $statusDesc")
        }
    }
}
```

---

## Status Codes

| statusCode | Meaning |
|------------|---------|
| `1` | Success |
| `8201` | Pending |
| `8209` | Failed |
| `3` | Reversed |
| Other | See `statusDesc` and `hostStatusDesc` |

---

## Response Header

| Field | Type | Description |
|-------|------|-------------|
| `requestUuid` | String | Echoed request UUID |
| `messageCode` | String | `purchase`, `void`, `refund`, `inquiry` |
| `serverTimestamp` | String | `[YYYYMMDDHHMMSS]` |
| `status.statusCode` | Int | `1` = success |
| `status.statusDesc` | String | Status description |
| `status.hostStatusCode` | Int | Fawry host error code |
| `status.hostStatusDesc` | String | Host error description |

---

## Response Body (Sale)

| Field | Type | Description |
|-------|------|-------------|
| `fawryReference` | String | FCRN — save this for refund/void/inquiry |
| `amount` | Double | Transaction amount |
| `currency` | String | Currency (e.g. `EGP,2`) |
| `btc` | Int | Bill type code |
| `transactionType` | String | `Sale`, `Void`, `Refund` |
| `clientTerminalSequenceID` | String | Unique terminal sequence ID |
| `signature` | String | Response signature for validation |
| `receiptInfo` | Object | Receipt and card details |

### receiptInfo

| Field | Description |
|-------|-------------|
| `authId` | Authorization ID from issuer |
| `rrn` | Retrieval reference number |
| `receiptNumber` | Fawry receipt number |
| `merchantId` | Merchant ID |
| `terminalId` | Terminal ID |
| `cardInfo.cardAcctId` | Last 4 digits of card |
| `cardInfo.cardHolderName` | Cardholder name |
| `cardInfo.cardScheme` | VISA, MEEZA, etc. |

---

## Success Example

```json
{
    "header": {
        "status": {
            "statusCode": 1,
            "statusDesc": "Payment.VALUE_PAYMENT_STATUS_SUCCESS",
            "hostStatusCode": 200,
            "hostStatusDesc": "SUCCESS"
        }
    },
    "body": {
        "fawryReference": "1435895",
        "amount": 55.0,
        "currency": "EGP,2",
        "transactionType": "Sale",
        "receiptInfo": {
            "authId": "638377",
            "rrn": "729820005487",
            "cardInfo": {
                "cardHolderName": "MOHAMED/KAREMA",
                "cardAcctId": "7342",
                "cardScheme": "VISA"
            }
        }
    }
}
```

---

## Failure Example

```json
{
    "header": {
        "status": {
            "statusCode": 8209,
            "statusDesc": "FAILED",
            "hostStatusCode": 200,
            "hostStatusDesc": "SUCCESS"
        }
    },
    "body": {
        "fawryReference": "1435895",
        "amount": 55.0
    }
}
```

---

## Payment Callback Failures

When `onTransactionRequestFailure` is called (before or instead of a JSON response):

| Error | Typical Cause |
|-------|---------------|
| `CONNECTION_ERROR` | TapNPay not installed or not connected |
| `FORCE_UPDATE` | TapNPay version incompatible with Payment SDK |
| `UNKNOWN_ERROR` | Bind failure, remote exception, or invalid request |

---

## Best Practices

- Always save the `fawryReference` (FCRN) from successful sales for refund, void, and inquiry.
- Use a unique `orderId` (UUID) for every transaction.
- Marshal callback results to the main thread before updating UI.
- Log the full response string during development for debugging.
