package com.ironledger.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.AccountRepository
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.domain.Account
import com.ironledger.app.domain.DashboardSummary
import com.ironledger.app.domain.LedgerAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val summary: DashboardSummary = DashboardSummary(),
    val accounts: List<Account> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    accountRepository: AccountRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {
    val uiState = combine(
        accountRepository.observeAccounts(),
        transactionRepository.observeTransactions()
    ) { accounts, transactions ->
        HomeUiState(
            summary = LedgerAnalytics.buildDashboard(accounts, transactions),
            accounts = accounts
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}
