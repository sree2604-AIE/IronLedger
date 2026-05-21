package com.ironledger.app.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

object LedgerAnalytics {
    fun buildDashboard(
        accounts: List<Account>,
        transactions: List<LedgerTransaction>,
        nowMillis: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): DashboardSummary {
        val totalBalance = accounts.sumOf { it.balancePaise }
        val purseCash = accounts.filter { it.type == AccountType.CASH }.sumOf { it.balancePaise }
        val bankBalance = accounts.filter { it.type == AccountType.BANK }.sumOf { it.balancePaise }
        val nowDate = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val todayStart = nowDate.startMillis(zoneId)
        val weekStart = nowDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).startMillis(zoneId)
        val monthStart = nowDate.withDayOfMonth(1).startMillis(zoneId)
        val yearStart = nowDate.withDayOfYear(1).startMillis(zoneId)
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        val monthlyExpenses = expenses.filter { it.occurredAtEpochMillis >= monthStart }
        val monthlyIncome = transactions
            .filter { it.type == TransactionType.INCOME && it.occurredAtEpochMillis >= monthStart }
            .sumOf { it.amountPaise }
        val monthlyExpenseTotal = monthlyExpenses.sumOf { it.amountPaise }
        val topCategory = monthlyExpenses
            .groupBy { it.category?.name ?: "Other" }
            .maxByOrNull { entry -> entry.value.sumOf { it.amountPaise } }
            ?.key ?: "No spend yet"
        val savingsRatio = savingsRatio(monthlyIncome, monthlyExpenseTotal)
        val health = (55 + savingsRatio).coerceIn(0, 100)

        return DashboardSummary(
            totalBalancePaise = totalBalance,
            purseCashPaise = purseCash,
            bankBalancePaise = bankBalance,
            todayExpensePaise = expenses.filter { it.occurredAtEpochMillis >= todayStart }.sumOf { it.amountPaise },
            weeklyExpensePaise = expenses.filter { it.occurredAtEpochMillis >= weekStart }.sumOf { it.amountPaise },
            monthlyExpensePaise = monthlyExpenseTotal,
            yearlyExpensePaise = expenses.filter { it.occurredAtEpochMillis >= yearStart }.sumOf { it.amountPaise },
            monthlyIncomePaise = monthlyIncome,
            financialHealthScore = health,
            savingsRatioPercent = savingsRatio,
            topCategoryName = topCategory,
            recentTransactions = transactions.take(6),
            insights = buildInsights(monthlyIncome, monthlyExpenseTotal, topCategory, savingsRatio, transactions)
        )
    }

    fun buildAnalytics(
        transactions: List<LedgerTransaction>,
        nowMillis: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): AnalyticsSnapshot {
        val nowDate = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val monthStart = nowDate.withDayOfMonth(1).startMillis(zoneId)
        val monthTransactions = transactions.filter { it.occurredAtEpochMillis >= monthStart }
        val expenses = monthTransactions.filter { it.type == TransactionType.EXPENSE }
        val income = monthTransactions.filter { it.type == TransactionType.INCOME }
        val totalExpense = expenses.sumOf { it.amountPaise }
        val totalIncome = income.sumOf { it.amountPaise }
        val dayCount = nowDate.dayOfMonth.coerceAtLeast(1)
        val daysInMonth = nowDate.lengthOfMonth()
        
        val averageDaily = totalExpense / dayCount
        val predictedMonthEnd = averageDaily * daysInMonth

        return AnalyticsSnapshot(
            totalExpensePaise = totalExpense,
            totalIncomePaise = totalIncome,
            averageDailySpendPaise = averageDaily,
            savingsRatioPercent = savingsRatio(totalIncome, totalExpense),
            predictedMonthEndPaise = predictedMonthEnd,
            topCategories = expenses.groupBy { it.category?.name ?: "Other" }
                .map { (name, items) ->
                    CategoryTotal(
                        categoryName = name,
                        amountPaise = items.sumOf { it.amountPaise },
                        accentHex = items.firstOrNull()?.category?.accentHex ?: "#20D47B"
                    )
                }
                .sortedByDescending { it.amountPaise }
                .take(6),
            paymentMethods = expenses.groupBy { it.paymentMethod }
                .map { (method, items) -> PaymentMethodTotal(method, items.sumOf { it.amountPaise }) }
                .sortedByDescending { it.amountPaise },
            dailyTrend = buildDailyTrend(expenses, nowDate, zoneId)
        )
    }

    private fun buildDailyTrend(
        expenses: List<LedgerTransaction>,
        nowDate: LocalDate,
        zoneId: ZoneId
    ): List<DailySpendPoint> {
        val formatter = DateTimeFormatter.ofPattern("d MMM")
        val start = nowDate.minusDays(6)
        return (0..6).map { index ->
            val date = start.plusDays(index.toLong())
            val startMillis = date.startMillis(zoneId)
            val endMillis = date.plusDays(1).startMillis(zoneId)
            DailySpendPoint(
                label = date.format(formatter),
                amountPaise = expenses
                    .filter { it.occurredAtEpochMillis in startMillis until endMillis }
                    .sumOf { it.amountPaise }
            )
        }
    }

    private fun buildInsights(
        monthlyIncome: Long,
        monthlyExpense: Long,
        topCategory: String,
        savingsRatio: Int,
        transactions: List<LedgerTransaction>
    ): List<FinancialInsight> {
        val burnPercent = if (monthlyIncome == 0L) 0 else ((monthlyExpense * 100) / monthlyIncome).toInt()
        val insights = mutableListOf<FinancialInsight>()
        
        insights.add(FinancialInsight(
            title = "Executive Summary",
            body = if (savingsRatio >= 30) "Exceptional performance. Savings ratio of $savingsRatio% exceeds target. Burn: $burnPercent%." else "Savings ratio is $savingsRatio%. Optimization recommended to hit 30%. Burn: $burnPercent%.",
            tone = if (savingsRatio >= 30) InsightTone.GOOD else InsightTone.WATCH
        ))

        if (topCategory != "No spend yet") {
            insights.add(FinancialInsight(
                title = "Expense Hotspot",
                body = "$topCategory is driving the majority of your outflow this month.",
                tone = InsightTone.INFO
            ))
        }

        // Subscription Waste Detection (Simple Rule)
        val subscriptions = transactions.filter { it.note.contains("Subscription", ignoreCase = true) || it.category?.name == "OTT" }
        if (subscriptions.size > 5) {
            insights.add(FinancialInsight(
                title = "Subscription Proliferation",
                body = "Detected ${subscriptions.size} active subscriptions. Review for potential savings.",
                tone = InsightTone.WATCH
            ))
        }

        // Fuel Analysis
        val fuelExpenses = transactions.filter { it.category?.name?.contains("Fuel", ignoreCase = true) == true }
        if (fuelExpenses.isNotEmpty()) {
            insights.add(FinancialInsight(
                title = "Automotive Efficiency",
                body = "Fuel expenditure is ${fuelExpenses.size} transactions deep this month.",
                tone = InsightTone.INFO
            ))
        }

        return insights.take(3)
    }

    private fun savingsRatio(income: Long, expense: Long): Int {
        if (income <= 0L) return 0
        return (((income - expense).coerceAtLeast(0L) * 100) / income).toInt().coerceIn(0, 100)
    }

    private fun LocalDate.startMillis(zoneId: ZoneId): Long =
        atStartOfDay(zoneId).toInstant().toEpochMilli()
}
