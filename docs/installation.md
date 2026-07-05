---
title: Installation
nav_order: 3
---

# Installation

---

## Add the Payment SDK

The SDK is distributed as a `.aar` file. You will receive `tapnpay-payment-sdk.aar` from Fawry.

1. Create a `libs` directory in your app module (if it does not exist).
2. Copy `tapnpay-payment-sdk.aar` into `app/libs/`.
3. Add the dependency in your app's `build.gradle`:

```gradle
dependencies {
    implementation fileTree(dir: "libs", include: ["*.aar"])
}
```

4. Sync the project in Android Studio.

---

## Required Dependencies

The Payment SDK requires these libraries at runtime:

```gradle
dependencies {
    implementation fileTree(dir: "libs", include: ["*.aar"])
    implementation "androidx.appcompat:appcompat:1.6.1"
    implementation "com.google.code.gson:gson:2.10.1"
}
```

---

## AndroidManifest Configuration

### Package Query

Android 11+ requires declaring that your app queries the TapNPay package:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <queries>
        <package android:name="com.fawry.softpos" />
    </queries>

    <application ...>
        ...
    </application>
</manifest>
```

---

## ProGuard Rules

If you enable code obfuscation (`minifyEnabled true`), add these rules to `proguard-rules.pro`:

```
-keep public class com.google.gson.** {public private protected *;}
-keep public class com.fawry.softpos.retailer.ipc.IPCConnectivity { public *;}
-keep public class com.fawry.softpos.retailer.ipc.IPCConnectivity$Builder { public *;}
-keep class com.fawry.softpos.retailer.connect.FawryConnect { public *;}
-keep class com.fawry.softpos.retailer.connect.FawryConnect$OnConnectionCallBack { public *;}
-keep class com.fawry.softpos.retailer.connect.FawryConnect$OnTransactionCallBack { public *;}
-keep class com.fawry.softpos.retailer.connect.FawryConnect$Companion { public *;}
-keep class com.fawry.softpos.retailer.utilities.Connectivity { public *;}
-keep class com.fawry.softpos.retailer.utilities.ConnectivityBuilder { public *;}
-keep class com.fawry.softpos.retailer.utilities.SaleBuilder { public *;}
-keep class com.fawry.softpos.retailer.utilities.Sale { public *;}
-keep class com.fawry.softpos.retailer.utilities.RefundBuilder { public *;}
-keep class com.fawry.softpos.retailer.utilities.Refund { public *;}
-keep class com.fawry.softpos.retailer.utilities.VoidBuilder { public *;}
-keep class com.fawry.softpos.retailer.utilities.VoidModel { public *;}
-keep class com.fawry.softpos.retailer.utilities.InquiryBuilder { public *;}
-keep class com.fawry.softpos.retailer.utilities.Inquiry { public *;}
-keep enum com.fawry.softpos.retailer.connect.model.connection.ConnectionType { public *;}
-keep class com.fawry.softpos.retailer.connect.model.connection.ConnectionData { public *;}
-keep enum com.fawry.softpos.retailer.connect.model.connection.ConnectionStatus { public *;}
-keep enum com.fawry.softpos.retailer.connect.model.payment.PaymentOptionType { public *;}
-keep enum com.fawry.softpos.retailer.connect.model.payment.inquiry.IdType { public *;}
-keep interface com.fawry.softpos.retailer.connect.model.ErrorCode { public *;}
-keep enum com.fawry.softpos.retailer.connect.model.ErrorCode$Request { public *;}
-keep enum com.fawry.softpos.retailer.connect.model.ErrorCode$Payment { public *;}
-keep enum com.fawry.softpos.retailer.connect.model.ErrorCode$General { public *;}
-keep enum com.fawry.softpos.retailer.connect.model.ErrorCode$Connection { public *;}
-keep enum com.fawry.softpos.retailer.connect.model.ErrorCode$Configurations { public *;}
-keep class com.fawry.softpos.retailer.connect.model.messages.user.UserData {*;}
-keep class com.fawry.softpos.retailer.connect.model.messages.user.UserType {*;}
-keep class com.fawry.softpos.retailer.connect.model.payment.extrakeys.ExtraKey {*;}
-keepclassmembers public class com.fawry.softpos.retailer.connect.model.payment.** {*;}
-keepclassmembers public class com.fawry.softpos.retailer.connect.model.messages.** {*;}
```

---

## Verify Installation

After adding the SDK, confirm the classes are available:

```kotlin
import com.fawry.softpos.retailer.connect.FawryConnect
import com.fawry.softpos.retailer.ipc.IPCConnectivity

// If these imports resolve, the SDK is installed correctly
```

---

## Project Structure

After installation, your project should look like:

```
your-android-app/
├── app/
│   ├── libs/
│   │   └── tapnpay-payment-sdk.aar  ← SDK AAR file
│   ├── src/main/
│   │   ├── AndroidManifest.xml      ← includes <queries> for com.fawry.softpos
│   │   └── java/.../YourActivity.kt ← integration code
│   ├── build.gradle                 ← fileTree libs dependency
│   └── proguard-rules.pro           ← keep rules (if minify enabled)
└── build.gradle
```

---

## Minimum Requirements

| Requirement | Value |
|-------------|-------|
| minSdkVersion | 23 |
| TapNPay package | `com.fawry.softpos` |
| Payment SDK version | 1.1.0.0 or later |
| Kotlin / Java | Kotlin 1.8+ or Java 8+ |

---

## TapNPay App

Your integration target is the Fawry TapNPay (SoftPOS) app. Install it on the same device where you run your merchant app. The IPC flow binds to TapNPay's `ConnectService` — both apps must be on the same device.

Download TapNPay from your Fawry onboarding contact or the link provided in your sandbox credentials.
