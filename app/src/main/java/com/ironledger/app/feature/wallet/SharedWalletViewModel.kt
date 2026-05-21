package com.ironledger.app.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.SharedWalletRepository
import com.ironledger.app.domain.SharedWallet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SharedWalletViewModel @Inject constructor(
    private val walletRepository: SharedWalletRepository
) : ViewModel() {
    val wallets: StateFlow<List<SharedWallet>> = walletRepository.observeWallets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
