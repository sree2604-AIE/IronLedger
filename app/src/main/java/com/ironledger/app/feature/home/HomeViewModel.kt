package com.ironledger.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.AccountRepository
import com.ironledger.app.data.repository.ReminderRepository
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.domain.Account
import com.ironledger.app.domain.DashboardSummary
import com.ironledger.app.domain.LedgerAnalytics
import com.ironledger.app.domain.LedgerTransaction
import com.ironledger.app.domain.Reminder
import com.ironledger.app.domain.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val summary: DashboardSummary = DashboardSummary(),
    val accounts: List<Account> = emptyList(),
    val upcomingReminders: List<Reminder> = emptyList(),
    /** Positive = net worth grew, negative = shrank, null = insufficient history */
    val netWorthChangePct: Double? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    accountRepository: AccountRepository,
    transactionRepository: TransactionRepository,
    reminderRepository: ReminderRepository
) : ViewModel() {
    val uiState = combine(
        accountRepository.observeAccounts(),
        transactionRepository.observeTransactions(),
        reminderRepository.observeReminders()
    ) { accounts, transactions, reminders ->
        val now = System.currentTimeMillis()
        val summary = LedgerAnalytics.buildDashboard(accounts, transactions)
        HomeUiState(
            summary = summary,
            accounts = accounts,
            upcomingReminders = reminders
                .filter { !it.isPaid && it.dueAtEpochMillis > now }
                .sortedBy { it.dueAtEpochMillis }
                .take(5),
            netWorthChangePct = computeNetWorthChangePct(accounts, transactions, now)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    /**
     * Estimates month-over-month net worth change as:
     *  (this month's net savings) vs (last month's net savings)
     * Returns null when there isn't enough data from both months.
     */
    private fun computeNetWorthChangePct(
        accounts: List<Account>,
        transactions: List<LedgerTransaction>,
        nowMillis: Long
    ): Double? {
        val zone = ZoneId.systemDefault()
        val nowDate = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()

        val thisMonthStart = nowDate.withDayOfMonth(1)
            .atStartOfDay(zone).toInstant().toEpochMilli()
        val lastMonthStart = nowDate.minusMonths(1).withDayOfMonth(1)
            .atStartOfDay(zone).toInstant().toEpochMilli()

        fun net(from: Long, to: Long): Long {
            val slice = transactions.filter { it.occurredAtEpochMillis in from until to }
            val income = slice.filter { it.type == TransactionType.INCOME }.sumOf { it.amountPaise }
            val expense = slice.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountPaise }
            return income - expense
        }

        val thisMonthNet = net(thisMonthStart, nowMillis)
        val lastMonthNet = net(lastMonthStart, thisMonthStart)

        if (lastMonthNet == 0L) return null
        return ((thisMonthNet - lastMonthNet).toDouble() / Math.abs(lastMonthNet)) * 100.0
    }
}
