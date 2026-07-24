package com.ironledger.app.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.SharedWalletRepository
import com.ironledger.app.domain.SharedWallet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
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

    fun createWallet(name: String, members: List<String>) {
        viewModelScope.launch {
            walletRepository.addWallet(
                SharedWallet(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    totalBalancePaise = 0L,
                    members = members
                )
            )
        }
    }

    fun addExpense(walletId: String, amountPaise: Long, description: String, paidByMemberId: String) {
        viewModelScope.launch {
            walletRepository.addExpense(walletId, amountPaise, description, paidByMemberId)
        }
    }
}
