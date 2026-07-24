package com.ironledger.app.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.ironledger.app.domain.AccountType
import com.ironledger.app.domain.CategoryGroup
import com.ironledger.app.domain.PaymentMethod
import com.ironledger.app.domain.TransactionSource
import com.ironledger.app.domain.TransactionStatus
import com.ironledger.app.domain.TransactionType

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: AccountType,
    val balancePaise: Long,
    val institution: String? = null,
    val mask: String? = null,
    val sortOrder: Int = 0,
    val updatedAtEpochMillis: Long
)

@Entity(
    tableName = "categories",
    indices = [Index(value = ["transactionType", "groupName"])]
)
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val groupName: CategoryGroup,
    val transactionType: TransactionType,
    val accentHex: String,
    val iconKey: String,
    val sortOrder: Int = 0
)

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["type"]),
        Index(value = ["accountId"]),
        Index(value = ["toAccountId"]),
        Index(value = ["categoryId"]),
        Index(value = ["vehicleId"]),
        Index(value = ["tripId"]),
        Index(value = ["occurredAtEpochMillis"])
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: TransactionType,
    val amountPaise: Long,
    val accountId: String,
    val toAccountId: String? = null,
    val categoryId: String? = null,
    val vehicleId: String? = null,
    val tripId: String? = null,
    val odometerValue: Double? = null,
    val fuelQuantity: Double? = null,
    val paidByMemberId: String? = null,
    val paymentMethod: PaymentMethod,
    val note: String,
    val occurredAtEpochMillis: Long,
    val createdAtEpochMillis: Long,
    val source: TransactionSource,
    val status: TransactionStatus
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // CAR, BIKE, SCOOTER
    val brandModel: String,
    val year: String? = null,
    val colorHex: String? = null,
    val registrationNumber: String?,
    val currentMileage: Double = 0.0,
    val createdAtEpochMillis: Long
)

@Entity(
    tableName = "fuel_logs",
    indices = [Index(value = ["vehicleId"])]
)
data class FuelLogEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val dateEpochMillis: Long,
    val odometerValue: Double,
    val fuelQuantityLitres: Double,
    val amountPaise: Long,
    val stationName: String? = null,
    val fullTank: Boolean = true
)

@Entity(
    tableName = "service_records",
    indices = [Index(value = ["vehicleId"])]
)
data class ServiceRecordEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val dateEpochMillis: Long,
    val odometerValue: Double,
    val type: String, // SERVICE, REPAIR, TYRE, etc.
    val description: String,
    val amountPaise: Long,
    val facilityName: String? = null
)

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val name: String,
    val budgetPaise: Long,
    val startDateEpochMillis: Long,
    val endDateEpochMillis: Long?,
    val isActive: Boolean = true,
    val createdAtEpochMillis: Long
)

@Entity(
    tableName = "trip_members",
    indices = [Index(value = ["tripId"])]
)
data class TripMemberEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val name: String,
    val isOwner: Boolean = false
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // EB_BILL, EMI, INSURANCE, etc.
    val amountPaise: Long?,
    val dueAtEpochMillis: Long,
    val repeatIntervalDays: Int?,
    val isPaid: Boolean = false,
    val createdAtEpochMillis: Long
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val amountPaise: Long,
    val billingCycle: String, // MONTHLY, YEARLY
    val nextBillingDateEpochMillis: Long,
    val category: String,
    val createdAtEpochMillis: Long
)

@Entity(tableName = "emis")
data class EmiEntity(
    @PrimaryKey val id: String,
    val loanName: String,
    val monthlyAmountPaise: Long,
    val totalTenureMonths: Int,
    val remainingTenureMonths: Int,
    val startDateEpochMillis: Long,
    val createdAtEpochMillis: Long
)

@Entity(tableName = "shared_wallets")
data class SharedWalletEntity(
    @PrimaryKey val id: String,
    val name: String,
    val totalBalancePaise: Long,
    val createdAtEpochMillis: Long
)

@Entity(
    tableName = "shared_wallet_members",
    indices = [Index(value = ["walletId"])]
)
data class SharedWalletMemberEntity(
    @PrimaryKey val id: String,
    val walletId: String,
    val name: String,
    val email: String? = null,
    val joinedAtEpochMillis: Long
)

@Entity(
    tableName = "wallet_expenses",
    indices = [Index(value = ["walletId"]), Index(value = ["paidByMemberId"])]
)
data class WalletExpenseEntity(
    @PrimaryKey val id: String,
    val walletId: String,
    val amountPaise: Long,
    val description: String,
    val dateEpochMillis: Long,
    val paidByMemberId: String,
    val categoryId: String? = null
)

@Entity(
    tableName = "wallet_settlements",
    indices = [Index(value = ["walletId"]), Index(value = ["fromMemberId"]), Index(value = ["toMemberId"])]
)
data class WalletSettlementEntity(
    @PrimaryKey val id: String,
    val walletId: String,
    val fromMemberId: String,
    val toMemberId: String,
    val amountPaise: Long,
    val dateEpochMillis: Long,
    val status: String // PENDING, SETTLED
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val amountPaise: Long,
    val monthYear: String, // MM-YYYY
    val warningThresholdPercent: Int = 80
)

data class TransactionWithRelations(
    @Embedded val transaction: TransactionEntity,
    @Relation(parentColumn = "accountId", entityColumn = "id")
    val account: AccountEntity?,
    @Relation(parentColumn = "toAccountId", entityColumn = "id")
    val toAccount: AccountEntity?,
    @Relation(parentColumn = "categoryId", entityColumn = "id")
    val category: CategoryEntity?
)
