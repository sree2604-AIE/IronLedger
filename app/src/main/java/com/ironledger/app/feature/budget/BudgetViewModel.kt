package com.ironledger.app.feature.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.BudgetRepository
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.domain.LedgerAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class BudgetUiItem(
    val categoryId: String,
    val categoryName: String,
    val budgetPaise: Long,
    val spentPaise: Long,
    val accentHex: String
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    private val currentMonthYear: String
        get() = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-yyyy"))

    val budgets = transactionRepository.observeTransactions().map { transactions ->
        val analytics = LedgerAnalytics.buildAnalytics(transactions)
        analytics.topCategories.map { cat ->
            BudgetUiItem(
                categoryId = cat.categoryName,
                categoryName = cat.categoryName,
                budgetPaise = 20_000_00L, // Default budget for demo; in production this comes from BudgetRepository
                spentPaise = cat.amountPaise,
                accentHex = cat.accentHex
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun addBudget(categoryName: String, amountPaise: Long) {
        viewModelScope.launch {
            budgetRepository.setBudget(
                categoryId = categoryName.lowercase().replace(" ", "_"),
                amountPaise = amountPaise,
                monthYear = currentMonthYear
            )
        }
    }
}
