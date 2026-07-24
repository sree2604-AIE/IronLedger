package com.ironledger.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.fragment.app.FragmentActivity
import com.ironledger.app.core.security.BiometricLockManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var biometricLockManager: BiometricLockManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (biometricLockManager.canAuthenticate()) {
            // Show lock screen FIRST, only reveal app on success
            biometricLockManager.authenticate(
                activity = this,
                onSuccess = {
                    runOnUiThread { setMainContent() }
                },
                onError = { errorCode ->
                    // Auth was cancelled, failed, or dismissed — close the app
                    // Error code 10 = user pressed back/cancelled
                    // Error code 13 = cancelled by system
                    runOnUiThread { finish() }
                }
            )
            // Do NOT call setMainContent() here — wait for onSuccess
        } else {
            // Device has no biometric/PIN set up — open app normally
            setMainContent()
        }
    }

    private fun setMainContent() {
        setContent {
            IronLedgerRoot()
        }
    }
}
