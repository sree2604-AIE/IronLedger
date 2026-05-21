package com.ironledger.app.feature.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.AccountRepository
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.domain.DashboardSummary
import com.ironledger.app.domain.LedgerAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AiViewModel @Inject constructor(
    accountRepository: AccountRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {
    val summary = combine(
        accountRepository.observeAccounts(),
        transactionRepository.observeTransactions()
    ) { accounts, transactions ->
        LedgerAnalytics.buildDashboard(accounts, transactions)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardSummary()
    )
}
