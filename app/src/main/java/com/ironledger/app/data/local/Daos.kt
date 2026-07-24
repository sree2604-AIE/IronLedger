package com.ironledger.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ironledger.app.domain.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY sortOrder ASC, name ASC")
    fun observeAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY sortOrder ASC, name ASC")
    suspend fun getAccounts(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AccountEntity>)

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun count(): Int

    @Query("UPDATE accounts SET balancePaise = balancePaise + :deltaPaise, updatedAtEpochMillis = :updatedAt WHERE id = :accountId")
    suspend fun adjustBalance(accountId: String, deltaPaise: Long, updatedAt: Long)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE transactionType = :type ORDER BY sortOrder ASC, name ASC")
    fun observeByType(type: TransactionType): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE transactionType = :type ORDER BY sortOrder ASC, name ASC")
    suspend fun getByType(type: TransactionType): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}

@Dao
interface TransactionDao {
    @Transaction
    @Query("SELECT * FROM transactions ORDER BY occurredAtEpochMillis DESC, createdAtEpochMillis DESC")
    fun observeTransactions(): Flow<List<TransactionWithRelations>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY occurredAtEpochMillis DESC, createdAtEpochMillis DESC")
    suspend fun getTransactions(): List<TransactionWithRelations>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun count(): Int

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY createdAtEpochMillis DESC")
    fun observeVehicles(): Flow<List<VehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM transactions WHERE vehicleId = :vehicleId")
    fun observeVehicleTransactions(vehicleId: String): Flow<List<TransactionEntity>>
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startDateEpochMillis DESC")
    fun observeTrips(): Flow<List<TripEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM transactions WHERE tripId = :tripId")
    fun observeTripTransactions(tripId: String): Flow<List<TransactionEntity>>
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY dueAtEpochMillis ASC")
    fun observeReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isPaid = :paid WHERE id = :id")
    suspend fun markAsPaid(id: String, paid: Boolean)
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY nextBillingDateEpochMillis ASC")
    fun observeSubscriptions(): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: SubscriptionEntity)
}

@Dao
interface EmiDao {
    @Query("SELECT * FROM emis ORDER BY createdAtEpochMillis DESC")
    fun observeEmis(): Flow<List<EmiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(emi: EmiEntity)
}

@Dao
interface SharedWalletDao {
    @Query("SELECT * FROM shared_wallets ORDER BY createdAtEpochMillis DESC")
    fun observeWallets(): Flow<List<SharedWalletEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet: SharedWalletEntity)

    @Query("DELETE FROM shared_wallets WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface TripMemberDao {
    @Query("SELECT * FROM trip_members WHERE tripId = :tripId")
    fun observeMembers(tripId: String): Flow<List<TripMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<TripMemberEntity>)
}

@Dao
interface FuelLogDao {
    @Query("SELECT * FROM fuel_logs WHERE vehicleId = :vehicleId ORDER BY dateEpochMillis DESC")
    fun observeFuelLogs(vehicleId: String): Flow<List<FuelLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fuelLog: FuelLogEntity)
}

@Dao
interface ServiceRecordDao {
    @Query("SELECT * FROM service_records WHERE vehicleId = :vehicleId ORDER BY dateEpochMillis DESC")
    fun observeServiceRecords(vehicleId: String): Flow<List<ServiceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ServiceRecordEntity)
}

@Dao
interface WalletExpenseDao {
    @Query("SELECT * FROM wallet_expenses WHERE walletId = :walletId ORDER BY dateEpochMillis DESC")
    fun observeExpenses(walletId: String): Flow<List<WalletExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: WalletExpenseEntity)
}

@Dao
interface WalletSettlementDao {
    @Query("SELECT * FROM wallet_settlements WHERE walletId = :walletId ORDER BY dateEpochMillis DESC")
    fun observeSettlements(walletId: String): Flow<List<WalletSettlementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settlement: WalletSettlementEntity)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun observeBudgets(monthYear: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity)
}
