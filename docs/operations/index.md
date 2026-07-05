---
title: Operations
nav_order: 5
has_children: true
---

# Operations

The Payment SDK supports five operations through the TapNPay app. Each operation uses a builder pattern: configure the request with setter methods, then call `.send()` with an `OnTransactionCallBack`. You must set **`setMerchantToken`**, **`setMerchantAccountNumber`**, and **`setBtc`** on every flow.

| Operation | Builder | Description |
|-----------|---------|-------------|
| [Card Sale]({% link operations/sale.md %}) | `requestSale()` | Accept a card payment |
| [Card Refund]({% link operations/refund.md %}) | `requestRefund()` | Refund a previous transaction |
| [Card Void]({% link operations/void.md %}) | `requestVoid()` | Void/cancel a recent transaction |
| [Inquiry]({% link operations/inquiry.md %}) | `requestInquiry()` | Query transaction status |
| [Clear Cache]({% link operations/clear-cache.md %}) | `requestClearCache()` | Clear app cache and keys |

All operations follow the same lifecycle:

1. Ensure IPC connection is established (`onConnected` has fired).
2. Build the request using the appropriate builder on the same `fawryConnect` instance.
3. Call `.send()` with an `OnTransactionCallBack`.
4. TapNPay opens its payment UI and processes the request.
5. Your callback receives a JSON response string with the result.

Unlike the Web SDK deep-link flow, **no server-side signature** is required for IPC. Merchant credentials are sent directly with each request.
