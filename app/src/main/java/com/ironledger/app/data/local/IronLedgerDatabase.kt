package com.ironledger.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        VehicleEntity::class,
        FuelLogEntity::class,
        ServiceRecordEntity::class,
        TripEntity::class,
        TripMemberEntity::class,
        ReminderEntity::class,
        SubscriptionEntity::class,
        EmiEntity::class,
        SharedWalletEntity::class,
        SharedWalletMemberEntity::class,
        WalletExpenseEntity::class,
        WalletSettlementEntity::class,
        BudgetEntity::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class IronLedgerDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun tripDao(): TripDao
    abstract fun reminderDao(): ReminderDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun emiDao(): EmiDao
    abstract fun sharedWalletDao(): SharedWalletDao
    abstract fun tripMemberDao(): TripMemberDao
    abstract fun fuelLogDao(): FuelLogDao
    abstract fun serviceRecordDao(): ServiceRecordDao
    abstract fun walletExpenseDao(): WalletExpenseDao
    abstract fun walletSettlementDao(): WalletSettlementDao
    abstract fun budgetDao(): BudgetDao
}
