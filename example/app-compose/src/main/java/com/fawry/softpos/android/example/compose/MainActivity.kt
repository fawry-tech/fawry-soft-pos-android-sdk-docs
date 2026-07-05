package com.fawry.softpos.android.example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.fawry.softpos.android.example.compose.ui.theme.FawryAndroidDemoTheme

/**
 * Jetpack Compose sample activity demonstrating IPC integration with Fawry TapNPay.
 *
 * Flow:
 * 1. Enter merchant credentials
 * 2. Tap Connect to bind to TapNPay via IPC
 * 3. Select an operation and tap Send
 * 4. TapNPay processes the payment and returns JSON to the callback
 */
class MainActivity : ComponentActivity() {

    private val viewModel: FawryDemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides this@MainActivity) {
                FawryAndroidDemoTheme {
                    FawryDemoScreen(viewModel = viewModel)
                }
            }
        }
    }
}
