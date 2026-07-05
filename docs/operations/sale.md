---
title: Card Sale
parent: Operations
nav_order: 1
---

# Card Sale

Accept a card payment through the TapNPay app over IPC.

---

## Basic Example

```kotlin
import com.fawry.softpos.retailer.connect.model.payment.PaymentOptionType
import com.fawry.softpos.retailer.modelBuilder.sale.CardSale
import java.util.UUID

fawryConnect
    ?.requestSale<CardSale.Builder>(PaymentOptionType.CARD)
    ?.setAmount(150.00)
    ?.setCurrency("EGP")
    ?.setOrderID(UUID.randomUUID().toString())
    ?.setBtc(99901)
    ?.setMerchantToken("YOUR_MERCHANT_TOKEN")
    ?.setMerchantAccountNumber("YOUR_ACCOUNT_NUMBER")
    ?.setPrintReceipt(false)
    ?.setDisplayInvoice(true)
    ?.send(
        FawryConnect.OnTransactionCallBack(
            onTransactionRequestSuccess = { response -> handleSuccess(response) },
            onTransactionRequestFailure = { errorCode, cause ->
                Log.e("TAG", "Sale failed: $errorCode", cause)
            }
        )
    )
```

---

## Parameters

| Parameter               | Type                    | Required | Description                                          |
|-------------------------|-------------------------|----------|------------------------------------------------------|
| `amount`                | `Double`                | Yes      | Transaction amount                                   |
| `currency`              | `String`                | Yes      | Currency code (e.g. `"EGP"`)                         |
| `orderId`               | `String`                | No       | Unique transaction ID (UUID, 36 chars)               |
| `merchantToken`         | `String`                | Yes      | Merchant secure token from Fawry                     |
| `merchantAccountNumber` | `String`                | Yes      | Merchant collection account                          |
| `btc`                   | `Long`                  | Yes      | Bill type code from Fawry                            |
| `printReceipt`          | `Boolean`               | No       | Let TapNPay print the customer receipt               |
| `displayInvoice`        | `Boolean`               | NO       | Let TapNPay display digital receipt on screen        |
| `promoCode`             | `String`                | No       | Voucher promo code                                   |
| `extras`                | `Map<String, ExtraKey>` | No       | Additional key-value data saved with the transaction |
| `tips`                  | `Double`                | No       | Tips amount                                          |
| `billingAccount`        | `String`                | No       | Customer identifier for bill payment (not purchase)  |

---

## Success Response

On success, `statusCode` in the response header is `1`:

```json
{
    "header": {
        "status": {
            "statusCode": 1,
            "statusDesc": "Payment.VALUE_PAYMENT_STATUS_SUCCESS"
        }
    },
    "body": {
        "fawryReference": "1435895",
        "amount": 150.0,
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

See [Response Handling]({% link ../response-handling.md %}) for the full response structure.
