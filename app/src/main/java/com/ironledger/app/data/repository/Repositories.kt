package com.ironledger.app.data.repository

import com.ironledger.app.domain.AccentColor
import com.ironledger.app.domain.Account
import com.ironledger.app.domain.Category
import com.ironledger.app.domain.NewTransaction
import com.ironledger.app.domain.ThemeMode
import com.ironledger.app.domain.TransactionType
import com.ironledger.app.domain.LedgerTransaction
import kotlinx.coroutines.flow.Flow

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.AMOLED,
    val accentColor: AccentColor = AccentColor.EMERALD,
    val hideBalances: Boolean = false,
    val userName: String = ""
)

interface AccountRepository {
    fun observeAccounts(): Flow<List<Account>>
    suspend fun addAccount(account: Account)
    suspend fun deleteAccount(id: String)
    suspend fun recalculateBalances()
}

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    fun observeByType(type: TransactionType): Flow<List<Category>>
}

interface TransactionRepository {
    fun observeTransactions(): Flow<List<LedgerTransaction>>
    suspend fun addTransaction(transaction: NewTransaction, status: com.ironledger.app.domain.TransactionStatus = com.ironledger.app.domain.TransactionStatus.CONFIRMED)
    suspend fun updateTransaction(transaction: LedgerTransaction)
    suspend fun deleteTransaction(id: String)
    suspend fun seedIfEmpty()
}

interface PreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setAccentColor(accentColor: AccentColor)
    suspend fun setHideBalances(hidden: Boolean)
    suspend fun setUserName(name: String)
}

interface VehicleRepository {
    fun observeVehicles(): Flow<List<com.ironledger.app.domain.Vehicle>>
    suspend fun addVehicle(vehicle: com.ironledger.app.domain.Vehicle)
    suspend fun deleteVehicle(id: String)
    suspend fun addFuelLog(vehicleId: String, amountPaise: Long, fuelLitres: Double, odometer: Double, station: String?)
    suspend fun addServiceRecord(vehicleId: String, amountPaise: Long, type: String, description: String, odometer: Double)
}

interface TripRepository {
    fun observeTrips(): Flow<List<com.ironledger.app.domain.Trip>>
    suspend fun addTrip(trip: com.ironledger.app.domain.Trip)
    suspend fun deleteTrip(id: String)
}

interface ReminderRepository {
    fun observeReminders(): Flow<List<com.ironledger.app.domain.Reminder>>
    suspend fun addReminder(reminder: com.ironledger.app.domain.Reminder)
    suspend fun markAsPaid(id: String, paid: Boolean)
}

interface SubscriptionRepository {
    fun observeSubscriptions(): Flow<List<com.ironledger.app.domain.Subscription>>
    suspend fun addSubscription(subscription: com.ironledger.app.domain.Subscription)
}

interface EmiRepository {
    fun observeEmis(): Flow<List<com.ironledger.app.domain.Emi>>
    suspend fun addEmi(emi: com.ironledger.app.domain.Emi)
}

interface SharedWalletRepository {
    fun observeWallets(): Flow<List<com.ironledger.app.domain.SharedWallet>>
    suspend fun addWallet(wallet: com.ironledger.app.domain.SharedWallet)
    suspend fun deleteWallet(id: String)
    suspend fun addExpense(walletId: String, amountPaise: Long, description: String, memberId: String)
    suspend fun settle(walletId: String, fromMemberId: String, toMemberId: String, amountPaise: Long)
}

interface BudgetRepository {
    fun observeBudgets(monthYear: String): Flow<List<com.ironledger.app.data.local.BudgetEntity>>
    suspend fun setBudget(categoryId: String, amountPaise: Long, monthYear: String)
}
