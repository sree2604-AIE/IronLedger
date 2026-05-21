package com.ironledger.app.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.domain.AnalyticsSnapshot
import com.ironledger.app.domain.LedgerAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    transactionRepository: TransactionRepository
) : ViewModel() {
    val snapshot = transactionRepository.observeTransactions()
        .map { LedgerAnalytics.buildAnalytics(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LedgerAnalytics.buildAnalytics(emptyList())
        )
}
