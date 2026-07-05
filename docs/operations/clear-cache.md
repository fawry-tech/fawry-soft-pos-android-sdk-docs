---
title: Clear Cache
parent: Operations
nav_order: 5
---

# Clear Cache

Clear the TapNPay app's cached security keys and/or profile data over IPC.

---

## Example

```kotlin
import com.fawry.softpos.retailer.modelBuilder.clearCache.ClearCache

fawryConnect
    ?.requestClearCache<ClearCache.Builder>()
    ?.setClearSecurityKeys(true)
    ?.setClearProfile(true)
    ?.setMerchantToken("YOUR_MERCHANT_TOKEN")
    ?.setMerchantAccountNumber("YOUR_ACCOUNT_NUMBER")
    ?.send(
        FawryConnect.OnTransactionCallBack(
            onTransactionRequestSuccess = { response ->
                Log.d("TAG", "Cache cleared: $response")
            },
            onTransactionRequestFailure = { errorCode, cause ->
                Log.e("TAG", "Clear cache failed: $errorCode", cause)
            }
        )
    )
```

---

## Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `clearSecurityKeys` | `Boolean` | No       | Clear cached security keys |
| `clearProfile` | `Boolean` | No       | Clear cached profile data |
| `merchantToken` | `String` | Yes      | Merchant secure token |
| `merchantAccountNumber` | `String` | Yes      | Merchant collection account |

---

## Notes

- Use clear cache during troubleshooting or when rotating merchant credentials.
- Clearing security keys may require re-authentication in TapNPay.
