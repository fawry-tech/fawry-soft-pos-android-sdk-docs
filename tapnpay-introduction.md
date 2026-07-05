# TapNPay

TapNPay lets merchants accept in-person card payments through the Fawry SoftPOS Android app. It supports both website integrations and native Android app integrations, so choose the path that matches where your checkout experience runs.

## Choose Your Integration

### Android SDK

Use the Android SDK when your payment flow starts from a native Android app.

The Android app includes the Fawry Payment SDK, binds to the TapNPay app over IPC/AIDL, sends sale, refund, void, inquiry, or clear-cache requests, and receives transaction results through callbacks.

[Start Android Integration](https://developers.fawrystaging.com/tapnpay/androidSdk/introduction)

### Web SDK

Use the Web SDK when your payment flow starts from a website, web app, or mobile browser on an Android device.

The website loads the Fawry SoftPOS Web SDK, requests a payment signature from your backend, opens the TapNPay app through a deep link, and receives the result on a callback page.

[Start Web Integration](https://developers.fawrystaging.com/tapnpay/webSdk/introduction)

## Integration Overview

```
┌──────────────────────┐       Connect.      ┌──────────────────────┐
│ Android App          │ ─────────────────▶  │ TapNPay SoftPOS App  │
│ Payment SDK          │ ◀──── callbacks ─── │ Card processing      │
└──────────────────────┘                     └──────────────────────┘

┌──────────────────────┐     deep link      ┌──────────────────────┐
│ Website + Web SDK    │ ─────────────────▶ │ TapNPay SoftPOS App  │
│ Backend signature    │ ◀── callback page  │ Card processing      │
└──────────────────────┘                    └──────────────────────┘
```

## Common Capabilities

Both integrations support the main TapNPay operations:

- Card sale
- Card refund
- Card void
- Transaction inquiry
- Clear cache

## Before You Start

Make sure you have:

- A Fawry merchant account with TapNPay credentials
- The Fawry TapNPay app installed on an Android device
- The required SDK package for your selected integration
- Backend support for signature generation when using the Web SDK

