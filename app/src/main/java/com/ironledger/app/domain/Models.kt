package com.ironledger.app.domain

enum class AccountType { CASH, BANK, WALLET, SAVINGS, INVESTMENT }

enum class TransactionType { EXPENSE, INCOME, TRANSFER }

enum class PaymentMethod { UPI, CASH, CARD, NET_BANKING, WALLET }

enum class CategoryGroup {
    DAILY,
    BILLS,
    TRANSPORT,
    VEHICLE,
    FINANCE,
    ENTERTAINMENT,
    SHOPPING,
    HEALTH,
    INCOME,
    TRANSFER
}

enum class TransactionSource { MANUAL, SMS_REVIEW, VOICE_REVIEW, SEED }

enum class TransactionStatus { CONFIRMED, PENDING_REVIEW }

enum class ThemeMode { DARK, AMOLED, GLASS }

enum class AccentColor { EMERALD, BLUE, GOLD, TITANIUM_RED }

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val balancePaise: Long,
    val institution: String? = null,
    val mask: String? = null
)

data class Category(
    val id: String,
    val name: String,
    val group: CategoryGroup,
    val transactionType: TransactionType,
    val accentHex: String,
    val iconKey: String
)

data class LedgerTransaction(
    val id: String,
    val type: TransactionType,
    val amountPaise: Long,
    val account: Account?,
    val toAccount: Account?,
    val category: Category?,
    val paymentMethod: PaymentMethod,
    val note: String,
    val occurredAtEpochMillis: Long,
    val createdAtEpochMillis: Long,
    val source: TransactionSource,
    val status: TransactionStatus
)

data class NewTransaction(
    val type: TransactionType,
    val amountPaise: Long,
    val accountId: String,
    val toAccountId: String? = null,
    val categoryId: String? = null,
    val paymentMethod: PaymentMethod,
    val note: String,
    val occurredAtEpochMillis: Long
)

data class DashboardSummary(
    val totalBalancePaise: Long = 0,
    val purseCashPaise: Long = 0,
    val bankBalancePaise: Long = 0,
    val todayExpensePaise: Long = 0,
    val weeklyExpensePaise: Long = 0,
    val monthlyExpensePaise: Long = 0,
    val yearlyExpensePaise: Long = 0,
    val monthlyIncomePaise: Long = 0,
    val financialHealthScore: Int = 0,
    val savingsRatioPercent: Int = 0,
    val topCategoryName: String = "No spend yet",
    val recentTransactions: List<LedgerTransaction> = emptyList(),
    val insights: List<FinancialInsight> = emptyList()
)

data class FinancialInsight(
    val title: String,
    val body: String,
    val tone: InsightTone
)

enum class InsightTone { GOOD, WATCH, INFO }

data class CategoryTotal(
    val categoryName: String,
    val amountPaise: Long,
    val accentHex: String
)

data class PaymentMethodTotal(
    val paymentMethod: PaymentMethod,
    val amountPaise: Long
)

data class DailySpendPoint(
    val label: String,
    val amountPaise: Long
)

data class AnalyticsSnapshot(
    val totalExpensePaise: Long,
    val totalIncomePaise: Long,
    val averageDailySpendPaise: Long,
    val savingsRatioPercent: Int,
    val predictedMonthEndPaise: Long = 0,
    val topCategories: List<CategoryTotal>,
    val paymentMethods: List<PaymentMethodTotal>,
    val dailyTrend: List<DailySpendPoint>
)

data class Vehicle(
    val id: String,
    val name: String,
    val type: VehicleType,
    val brandModel: String,
    val registrationNumber: String?,
    val currentMileage: Double,
    val totalExpensesPaise: Long = 0
)

enum class VehicleType { CAR, BIKE, SCOOTER }

data class Trip(
    val id: String,
    val name: String,
    val budgetPaise: Long,
    val startDateEpochMillis: Long,
    val endDateEpochMillis: Long?,
    val isActive: Boolean,
    val totalSpentPaise: Long = 0
)

data class Reminder(
    val id: String,
    val title: String,
    val type: ReminderType,
    val amountPaise: Long?,
    val dueAtEpochMillis: Long,
    val isPaid: Boolean
)

enum class ReminderType { EB_BILL, MOBILE, INSURANCE, EMI, SERVICE, CREDIT_CARD, SUBSCRIPTION }

data class Subscription(
    val id: String,
    val name: String,
    val amountPaise: Long,
    val billingCycle: String,
    val nextBillingDateEpochMillis: Long,
    val category: String
)

data class Emi(
    val id: String,
    val loanName: String,
    val monthlyAmountPaise: Long,
    val totalTenureMonths: Int,
    val remainingTenureMonths: Int,
    val startDateEpochMillis: Long
)

data class SharedWallet(
    val id: String,
    val name: String,
    val totalBalancePaise: Long,
    val members: List<String> = emptyList()
)

data class TripMember(
    val id: String,
    val tripId: String,
    val name: String,
    val isOwner: Boolean
)
