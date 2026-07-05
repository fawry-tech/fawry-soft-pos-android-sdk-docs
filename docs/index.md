---
title: Home
layout: home
nav_order: 1
---

# Fawry SoftPOS Android SDK (IPC)

Integrate card payments into your Android app using the Fawry SoftPOS (TapNPay) app. The Payment SDK handles communication between your app and TapNPay over **IPC** (Inter-Process Communication), providing a seamless in-app payment experience on Android devices.

---

## Architecture

Your merchant app integrates the **Payment SDK** and binds to the Fawry **TapNPay** (SoftPOS) app over IPC/AIDL. The SDK sends operation requests to TapNPay, TapNPay handles the payment UI and card processing, then returns the transaction result through callbacks.

```
┌──────────────────────┐      bind             ┌──────────────────────┐
│  Merchant Android App│ ────────────────────▶ │      TapNPay         │
│  + Payment SDK       │ ◀──── onConnected ─── │  (SoftPOS app)       │
└──────────┬───────────┘                       └──────────┬───────────┘
           │                                              │
           │  1. Build operation request                  │  3. Open TapNPay UI
           │  2. send()                                   │  4. Process card
           ▼                                              ▼
┌──────────────────────┐      callback         ┌──────────────────────┐
│  Transaction Callback│ ◀──────────────────── │  Transaction Result  │
│  Success / Failure   │                       │  JSON or ErrorCode   │
└──────────────────────┘                       └──────────────────────┘
```

1. Your app connects to TapNPay with `FawryConnect.setup(...).connect()`.
2. After `onConnected`, your app builds a sale, refund, void, inquiry, or clear-cache request and calls `.send()`.
3. TapNPay opens its payment flow, processes the operation, and completes the transaction.
4. Your app receives the result in `onTransactionRequestSuccess` or `onTransactionRequestFailure`.

---

## Supported Operations

| Operation | Description |
|-----------|-------------|
| [Card Sale]({% link operations/sale.md %}) | Accept a card payment |
| [Card Refund]({% link operations/refund.md %}) | Refund a previous transaction |
| [Card Void]({% link operations/void.md %}) | Void/cancel a recent transaction |
| [Inquiry]({% link operations/inquiry.md %}) | Query the status of a transaction |
| [Clear Cache]({% link operations/clear-cache.md %}) | Clear SoftPOS app cache and keys |

---

## Quick Links

- <a href="{% link getting-started.md %}">Getting Started</a> -- Install the SDK and make your first payment in 5 minutes
- <a href="{% link installation.md %}">Installation</a> -- Detailed setup instructions and ProGuard rules
- <a href="{% link connection-setup.md %}">Connection Setup</a> -- IPC connection and authentication modes
- <a href="{% link api-reference.md %}">API Reference</a> -- Full SDK API documentation
- <a href="{% link response-handling.md %}">Response Handling</a> -- Parse transaction results from callbacks
- <a href="{% link troubleshooting.md %}">Troubleshooting</a> -- Common issues and fixes

---

## Requirements

- A Fawry merchant account with SoftPOS credentials
- An Android device (API 23+) with the Fawry TapNPay app installed (`com.fawry.softpos`)
- Payment SDK `.aar` file (version 1.1.0.0 or later) from Fawry
