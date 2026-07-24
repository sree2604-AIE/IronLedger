package com.ironledger.app.feature.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.domain.LedgerTransaction
import com.ironledger.app.domain.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ActivityFilter(val label: String) {
    ALL("All"),
    EXPENSE("Expense"),
    INCOME("Income"),
    TRANSFER("Transfer")
}

data class ActivityUiState(
    val query: String = "",
    val filter: ActivityFilter = ActivityFilter.ALL,
    val transactions: List<LedgerTransaction> = emptyList(),
    val pendingTransactions: List<LedgerTransaction> = emptyList()
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(ActivityFilter.ALL)

    val uiState = combine(
        transactionRepository.observeTransactions(),
        query,
        filter
    ) { allTransactions, queryValue, filterValue ->
        val pending = allTransactions.filter { it.status == com.ironledger.app.domain.TransactionStatus.PENDING_REVIEW }
        val confirmed = allTransactions.filter { it.status == com.ironledger.app.domain.TransactionStatus.CONFIRMED }
        
        val filtered = confirmed
            .filter { transaction ->
                when (filterValue) {
                    ActivityFilter.ALL -> true
                    ActivityFilter.EXPENSE -> transaction.type == TransactionType.EXPENSE
                    ActivityFilter.INCOME -> transaction.type == TransactionType.INCOME
                    ActivityFilter.TRANSFER -> transaction.type == TransactionType.TRANSFER
                }
            }
            .filter { transaction ->
                queryValue.isBlank() ||
                    transaction.note.contains(queryValue, ignoreCase = true) ||
                    transaction.category?.name?.contains(queryValue, ignoreCase = true) == true ||
                    transaction.account?.name?.contains(queryValue, ignoreCase = true) == true
            }
        ActivityUiState(queryValue, filterValue, filtered, pending)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ActivityUiState()
    )

    fun setQuery(value: String) {
        query.value = value
    }

    fun setFilter(value: ActivityFilter) {
        filter.update { value }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(id)
        }
    }

    fun clearAllPending() {
        viewModelScope.launch {
            uiState.value.pendingTransactions.forEach { t ->
                transactionRepository.deleteTransaction(t.id)
            }
        }
    }
}
