---
title: Inquiry
parent: Operations
nav_order: 4
---

# Transaction Inquiry

Query the status of a previous transaction by its ID over IPC.

---

## Example

```kotlin
import com.fawry.softpos.retailer.connect.model.payment.inquiry.IdType
import com.fawry.softpos.retailer.modelBuilder.inquiry.Inquiry

fawryConnect
    ?.requestInquiry<Inquiry.Builder>()
    ?.setidType(IdType.FCRN)
    ?.setTransactionId("THE_FCRN_TO_LOOKUP")
    ?.setFromDate("20250101000000")
    ?.setToDate("20251231235959")
    ?.setMerchantToken("YOUR_MERCHANT_TOKEN")
    ?.setMerchantAccountNumber("YOUR_ACCOUNT_NUMBER")
    ?.setPrintReceipt(false)
    ?.send(
        FawryConnect.OnTransactionCallBack(
            onTransactionRequestSuccess = { response -> handleSuccess(response) },
            onTransactionRequestFailure = { errorCode, cause ->
                Log.e("TAG", "Inquiry failed: $errorCode", cause)
            }
        )
    )
```

---

## Parameters

| Parameter               | Type      | Required | Description                                                   |
|-------------------------|-----------|---------|---------------------------------------------------------------|
| `idType`                | `IdType`  | Yes     | `IdType.FCRN`, `IdType.ORDER_ID`, or `IdType.CORRELATION_UID` |
| `transactionId`         | `String`  | Yes     | FCRN, order ID, or correlation UID to look up                 |
| `fromDate`              | `String`  | No      | Start of search range `[YYYYMMDDHHMMSS]`                      |
| `toDate`                | `String`  | No      | End of search range `[YYYYMMDDHHMMSS]`                        |
| `merchantToken`         | `String`  | Yes     | Merchant secure token                                         |
| `merchantAccountNumber` | `String`  | Yes     | Merchant collection account                                   |
| `printReceipt`          | `Boolean` | No      | Let TapNPay print receipt                                     |

---

## IdType Values

| Value | Description |
|-------|-------------|
| `IdType.FCRN` | Look up by Fawry Customer Reference Number |
| `IdType.ORDER_ID` | Look up by your order ID |
| `IdType.CORRELATION_UID` | Look up by wallet correlation UID |
