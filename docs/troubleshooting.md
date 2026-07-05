---
title: Troubleshooting
nav_order: 8
---

# Troubleshooting

Common issues and their solutions for IPC integration.

---

## TapNPay App Not Installed

**Error:** `SERVICE_NOT_EXIST` or `CONNECTION_ERROR` with message "Tap N Pay app is not installed"

**Solutions:**

1. Install the Fawry TapNPay app (`com.fawry.softpos`) on the device.
2. Verify the `<queries>` entry in your `AndroidManifest.xml`:

   ```xml
   <queries>
       <package android:name="com.fawry.softpos" />
   </queries>
   ```

3. Test on a **physical device** — emulators may not have TapNPay installed.

---

## Connection Fails Immediately

**Symptom:** `onFailure` fires right after `connect()`.

**Solutions:**

- Confirm TapNPay is installed and not force-stopped.
- Use `applicationContext` in `setContext()`, not an activity context that may be destroyed.
- Check logcat for `SecurityException` — ensure your app is signed and installed correctly.
- Restart both your app and TapNPay.

---

## Transaction Callback Never Fires

**Symptom:** TapNPay UI opens but your callback is not called.

**Solutions:**

- Do not disconnect (`disConnect()`) while a transaction is in progress.
- Ensure your activity is not destroyed while TapNPay is in the foreground.
- Check that you passed a valid `OnTransactionCallBack` to `.send()`.
- Look for process death in logcat if the device is low on memory.

---

## FORCE_UPDATE Error

**Error:** `ErrorCode.Payment.FORCE_UPDATE`

**Cause:** The installed TapNPay version is older than what your Payment SDK requires.

**Solutions:**

- Update TapNPay to the latest version from your Fawry contact or Play Store.
- Ensure your `tapnpay-payment-sdk.aar` matches the TapNPay version in your sandbox.

---

## Invalid Merchant Credentials

**Symptom:** Transaction returns failure with authentication or configuration errors.

**Solutions:**

- Verify `merchantToken` and `merchantAccountNumber` match your Fawry sandbox credentials.
- For partner mode, ensure `partnerCode` is set in `UserData`.
- Confirm `btc` (bill type code) is correct for your merchant profile.
- Run **Clear Cache** if credentials were recently rotated.

---

## ALREADY_CONNECTED

**Error:** `ErrorCode.Connection.ALREADY_CONNECTED`

**Cause:** `connect()` was called when already connected.

**Solutions:**

- Call `disConnect()` before reconnecting.
- Reuse the existing `fawryConnect` instance instead of calling `connect()` again.

---

## ProGuard / R8 Stripping SDK Classes

**Symptom:** `ClassNotFoundException` or `NoSuchMethodError` in release builds.

**Solutions:**

- Add the ProGuard keep rules from <a href="{% link installation.md %}">Installation</a>.
- Test release builds on device before shipping.

---

## SDK Classes Not Found at Compile Time

**Symptom:** Unresolved references to `FawryConnect`, `IPCConnectivity`, etc.

**Solutions:**

1. Confirm `tapnpay-payment-sdk.aar` is in `app/libs/`.
2. Verify `build.gradle` includes:

   ```gradle
   implementation fileTree(dir: "libs", include: ["*.aar"])
   ```

3. Sync project and rebuild.

---

## Response Parsing Errors

**Symptom:** Gson or JSON parsing fails on the response string.

**Solutions:**

- Log the raw `response` string before parsing.
- Check `statusCode` in the header before accessing body fields.
- Handle null fields — not all receipt fields are present on every transaction.

---

## Testing Checklist

- [ ] TapNPay installed on physical Android device (API 23+)
- [ ] `<queries>` for `com.fawry.softpos` in manifest
- [ ] `tapnpay-payment-sdk.aar` in `libs/` and synced
- [ ] Valid merchant credentials entered
- [ ] `onConnected` fires before sending transactions
- [ ] Unique `orderId` (UUID) for each request
- [ ] Callback marshalled to main thread for UI updates
