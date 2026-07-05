---
title: Card Void
parent: Operations
nav_order: 3
---

# Card Void

Void (cancel) a recently completed card transaction. Voids are typically available only within the same batch/settlement period.

---

## Example

```kotlin
import com.fawry.softpos.retailer.connect.model.payment.PaymentOptionType
import com.fawry.softpos.retailer.connect.model.payment.inquiry.IdType
import com.fawry.softpos.retailer.connect.model.payment.inquiry.SearchType
import com.fawry.softpos.retailer.modelBuilder.void.CardVoid
import java.util.UUID

fawryConnect
    ?.requestVoid<CardVoid.Builder>(PaymentOptionType.CARD)
    ?.setSearchType(SearchType.ID_TYPE)
    ?.setIdType(IdType.FCRN)
    ?.setOriginalTransactionId("ORIGINAL_FCRN_HERE")
    ?.setOrderID(UUID.randomUUID().toString())
    ?.setBtc(99901)
    ?.setMerchantToken("YOUR_MERCHANT_TOKEN")
    ?.setMerchantAccountNumber("YOUR_ACCOUNT_NUMBER")
    ?.setPrintReceipt(false)
    ?.send(
        FawryConnect.OnTransactionCallBack(
            onTransactionRequestSuccess = { response -> handleSuccess(response) },
            onTransactionRequestFailure = { errorCode, cause ->
                Log.e("TAG", "Void failed: $errorCode", cause)
            }
        )
    )
```

---

## Parameters

| Parameter               | Type         | Required | Description                                   |
|-------------------------|--------------|----------|-----------------------------------------------|
| `originalTransactionId` | `String`     | Yes      | FCRN from the original sale                   |
| `searchType`            | `SearchType` | Yes      | `SearchType.ID_TYPE` or `SearchType.CARD_PAN` |
| `idType`                | `IdType`     | Yes      | `IdType.FCRN`, `IdType.ORDER_ID`, etc.        |
| `orderId`               | `String`     | Yes      | Unique ID for this void request               |
| `merchantToken`         | `String`     | Yes      | Merchant secure token                         |
| `merchantAccountNumber` | `String`     | Yes      | Merchant collection account                   |
| `btc`                   | `Long`       | Yes      | Bill type code                                |
| `printReceipt`          | `Boolean`    | Yes      | Let TapNPay print receipt                     |

---

## Notes

- Void does not require an amount.
- Use the FCRN from the original sale response as `originalTransactionId`.
