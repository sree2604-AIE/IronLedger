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
        TripEntity::class,
        ReminderEntity::class,
        SubscriptionEntity::class,
        EmiEntity::class,
        TripMemberEntity::class,
        SharedWalletEntity::class
    ],
    version = 3,
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
}
