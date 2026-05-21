package com.ironledger.app.data.local

import androidx.room.TypeConverter
import com.ironledger.app.domain.AccountType
import com.ironledger.app.domain.CategoryGroup
import com.ironledger.app.domain.PaymentMethod
import com.ironledger.app.domain.TransactionSource
import com.ironledger.app.domain.TransactionStatus
import com.ironledger.app.domain.TransactionType

class Converters {
    @TypeConverter fun toAccountType(value: String?): AccountType? = value?.let(AccountType::valueOf)
    @TypeConverter fun fromAccountType(value: AccountType?): String? = value?.name

    @TypeConverter fun toTransactionType(value: String?): TransactionType? = value?.let(TransactionType::valueOf)
    @TypeConverter fun fromTransactionType(value: TransactionType?): String? = value?.name

    @TypeConverter fun toPaymentMethod(value: String?): PaymentMethod? = value?.let(PaymentMethod::valueOf)
    @TypeConverter fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name

    @TypeConverter fun toCategoryGroup(value: String?): CategoryGroup? = value?.let(CategoryGroup::valueOf)
    @TypeConverter fun fromCategoryGroup(value: CategoryGroup?): String? = value?.name

    @TypeConverter fun toSource(value: String?): TransactionSource? = value?.let(TransactionSource::valueOf)
    @TypeConverter fun fromSource(value: TransactionSource?): String? = value?.name

    @TypeConverter fun toStatus(value: String?): TransactionStatus? = value?.let(TransactionStatus::valueOf)
    @TypeConverter fun fromStatus(value: TransactionStatus?): String? = value?.name
}
