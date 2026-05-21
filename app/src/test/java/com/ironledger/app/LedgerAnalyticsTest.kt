package com.ironledger.app

import com.ironledger.app.domain.Account
import com.ironledger.app.domain.AccountType
import com.ironledger.app.domain.Category
import com.ironledger.app.domain.CategoryGroup
import com.ironledger.app.domain.LedgerAnalytics
import com.ironledger.app.domain.LedgerTransaction
import com.ironledger.app.domain.PaymentMethod
import com.ironledger.app.domain.TransactionSource
import com.ironledger.app.domain.TransactionStatus
import com.ironledger.app.domain.TransactionType
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class LedgerAnalyticsTest {
    private val zone = ZoneId.of("Asia/Kolkata")
    private val now = LocalDate.of(2026, 5, 21).atTime(LocalTime.NOON).atZone(zone).toInstant().toEpochMilli()
    private val food = Category("food", "Food", CategoryGroup.DAILY, TransactionType.EXPENSE, "#20D47B", "food")
    private val salary = Category("salary", "Salary", CategoryGroup.INCOME, TransactionType.INCOME, "#20D47B", "income")
    private val cash = Account("cash", "Purse", AccountType.CASH, 2_000_00)
    private val bank = Account("bank", "Bank", AccountType.BANK, 10_000_00)

    @Test
    fun dashboardTotals_areCalculatedByPeriod() {
        val transactions = listOf(
            transaction("income", TransactionType.INCOME, 20_000_00, salary, now),
            transaction("food", TransactionType.EXPENSE, 3_000_00, food, now),
            transaction("old", TransactionType.EXPENSE, 1_000_00, food, LocalDate.of(2026, 4, 1).startMillis())
        )

        val summary = LedgerAnalytics.buildDashboard(listOf(cash, bank), transactions, now, zone)

        assertEquals(12_000_00L, summary.totalBalancePaise)
        assertEquals(3_000_00L, summary.todayExpensePaise)
        assertEquals(3_000_00L, summary.monthlyExpensePaise)
        assertEquals(20_000_00L, summary.monthlyIncomePaise)
        assertEquals(85, summary.savingsRatioPercent)
        assertEquals("Food", summary.topCategoryName)
    }

    @Test
    fun analyticsSnapshot_returnsTopCategoriesAndSavingsRatio() {
        val transactions = listOf(
            transaction("income", TransactionType.INCOME, 10_000_00, salary, now),
            transaction("food", TransactionType.EXPENSE, 2_500_00, food, now)
        )

        val snapshot = LedgerAnalytics.buildAnalytics(transactions, now, zone)

        assertEquals(2_500_00L, snapshot.totalExpensePaise)
        assertEquals(10_000_00L, snapshot.totalIncomePaise)
        assertEquals(75, snapshot.savingsRatioPercent)
        assertEquals("Food", snapshot.topCategories.first().categoryName)
    }

    private fun transaction(
        id: String,
        type: TransactionType,
        amount: Long,
        category: Category,
        occurredAt: Long
    ) = LedgerTransaction(
        id = id,
        type = type,
        amountPaise = amount,
        account = if (type == TransactionType.INCOME) bank else cash,
        toAccount = null,
        category = category,
        paymentMethod = PaymentMethod.UPI,
        note = id,
        occurredAtEpochMillis = occurredAt,
        createdAtEpochMillis = occurredAt,
        source = TransactionSource.MANUAL,
        status = TransactionStatus.CONFIRMED
    )

    private fun LocalDate.startMillis(): Long = atStartOfDay(zone).toInstant().toEpochMilli()
}
