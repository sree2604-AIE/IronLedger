package com.ironledger.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
            biometricLockManager.authenticate(
                activity = this,
                onSuccess = { setMainContent() },
                onError = { /* In production, show error or fallback to PIN */ setMainContent() }
            )
        } else {
            setMainContent()
        }
    }

    private fun setMainContent() {
        setContent {
            IronLedgerRoot()
        }
    }
}
