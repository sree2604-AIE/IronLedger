package com.ironledger.app.di

import android.content.Context
import androidx.room.Room
import com.ironledger.app.data.local.AccountDao
import com.ironledger.app.data.local.CategoryDao
import com.ironledger.app.data.local.EmiDao
import com.ironledger.app.data.local.IronLedgerDatabase
import com.ironledger.app.data.local.ReminderDao
import com.ironledger.app.data.local.SharedWalletDao
import com.ironledger.app.data.local.SubscriptionDao
import com.ironledger.app.data.local.TransactionDao
import com.ironledger.app.data.local.TripDao
import com.ironledger.app.data.local.TripMemberDao
import com.ironledger.app.data.local.VehicleDao
import com.ironledger.app.data.repository.AccountRepository
import com.ironledger.app.data.repository.CategoryRepository
import com.ironledger.app.data.repository.DataStorePreferencesRepository
import com.ironledger.app.data.repository.DefaultAccountRepository
import com.ironledger.app.data.repository.DefaultCategoryRepository
import com.ironledger.app.data.repository.DefaultEmiRepository
import com.ironledger.app.data.repository.DefaultReminderRepository
import com.ironledger.app.data.repository.DefaultSharedWalletRepository
import com.ironledger.app.data.repository.DefaultSubscriptionRepository
import com.ironledger.app.data.repository.DefaultTransactionRepository
import com.ironledger.app.data.repository.DefaultTripRepository
import com.ironledger.app.data.repository.DefaultVehicleRepository
import com.ironledger.app.data.repository.EmiRepository
import com.ironledger.app.data.repository.PreferencesRepository
import com.ironledger.app.data.repository.ReminderRepository
import com.ironledger.app.data.repository.SharedWalletRepository
import com.ironledger.app.data.repository.SubscriptionRepository
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.data.repository.TripRepository
import com.ironledger.app.data.repository.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IronLedgerDatabase =
        Room.databaseBuilder(context, IronLedgerDatabase::class.java, "ironledger.db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides fun provideAccountDao(database: IronLedgerDatabase): AccountDao = database.accountDao()
    @Provides fun provideCategoryDao(database: IronLedgerDatabase): CategoryDao = database.categoryDao()
    @Provides fun provideTransactionDao(database: IronLedgerDatabase): TransactionDao = database.transactionDao()
    @Provides fun provideVehicleDao(database: IronLedgerDatabase): VehicleDao = database.vehicleDao()
    @Provides fun provideTripDao(database: IronLedgerDatabase): TripDao = database.tripDao()
    @Provides fun provideReminderDao(database: IronLedgerDatabase): ReminderDao = database.reminderDao()
    @Provides fun provideSubscriptionDao(database: IronLedgerDatabase): SubscriptionDao = database.subscriptionDao()
    @Provides fun provideEmiDao(database: IronLedgerDatabase): EmiDao = database.emiDao()
    @Provides fun provideSharedWalletDao(database: IronLedgerDatabase): SharedWalletDao = database.sharedWalletDao()
    @Provides fun provideTripMemberDao(database: IronLedgerDatabase): TripMemberDao = database.tripMemberDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindAccountRepository(repository: DefaultAccountRepository): AccountRepository
    @Binds abstract fun bindCategoryRepository(repository: DefaultCategoryRepository): CategoryRepository
    @Binds abstract fun bindTransactionRepository(repository: DefaultTransactionRepository): TransactionRepository
    @Binds abstract fun bindPreferencesRepository(repository: DataStorePreferencesRepository): PreferencesRepository
    @Binds abstract fun bindVehicleRepository(repository: DefaultVehicleRepository): VehicleRepository
    @Binds abstract fun bindTripRepository(repository: DefaultTripRepository): TripRepository
    @Binds abstract fun bindReminderRepository(repository: DefaultReminderRepository): ReminderRepository
    @Binds abstract fun bindSubscriptionRepository(repository: DefaultSubscriptionRepository): SubscriptionRepository
    @Binds abstract fun bindEmiRepository(repository: DefaultEmiRepository): EmiRepository
    @Binds abstract fun bindSharedWalletRepository(repository: DefaultSharedWalletRepository): SharedWalletRepository
}
