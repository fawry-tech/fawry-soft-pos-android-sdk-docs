---
title: Card Refund
parent: Operations
nav_order: 2
---

# Card Refund

Refund a previously completed card transaction over IPC.

---

## Example

```kotlin
import com.fawry.softpos.retailer.connect.model.payment.PaymentOptionType
import com.fawry.softpos.retailer.connect.model.payment.inquiry.IdType
import com.fawry.softpos.retailer.connect.model.payment.inquiry.SearchType
import com.fawry.softpos.retailer.modelBuilder.refund.CardRefund
import java.util.UUID

fawryConnect
    ?.requestRefund<CardRefund.Builder>(PaymentOptionType.CARD)
    ?.setAmount(150.00)
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
                Log.e("TAG", "Refund failed: $errorCode", cause)
            }
        )
    )
```

---

## Parameters

| Parameter               | Type         | Required    | Description                                   |
|-------------------------|--------------|-------------|-----------------------------------------------|
| `amount`                | `Double`     | Yes         | Refund amount                                 |
| `originalTransactionId` | `String`     | Yes         | FCRN from the original sale                   |
| `searchType`            | `SearchType` | Yes         | `SearchType.ID_TYPE` or `SearchType.CARD_PAN` |
| `idType`                | `IdType`     | Yes         | `IdType.FCRN`, `IdType.ORDER_ID`, etc.        |
| `orderId`               | `String`     | No          | Unique ID for this refund request             |
| `merchantToken`         | `String`     | Yes         | Merchant secure token                         |
| `merchantAccountNumber` | `String`     | Yes         | Merchant collection account                   |
| `btc`                   | `Long`       | Yes         | Bill type code                                |
| `printReceipt`          | `Boolean`    | No          | Let TapNPay print receipt                     |
| `fromDate` / `toDate`   | `String`     | Conditional | Date range when using card PAN search         |

---

## Notes

- Use the FCRN (`fawryReference`) returned from the original sale as `originalTransactionId`.
- Refunds must be within the allowed refund window configured by Fawry.
