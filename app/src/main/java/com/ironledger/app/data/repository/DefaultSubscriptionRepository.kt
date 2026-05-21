package com.ironledger.app.data.repository

import com.ironledger.app.data.local.SubscriptionDao
import com.ironledger.app.data.local.SubscriptionEntity
import com.ironledger.app.data.local.toDomain
import com.ironledger.app.domain.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DefaultSubscriptionRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) : SubscriptionRepository {
    override fun observeSubscriptions(): Flow<List<Subscription>> =
        subscriptionDao.observeSubscriptions().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addSubscription(subscription: Subscription) {
        subscriptionDao.insert(
            SubscriptionEntity(
                id = UUID.randomUUID().toString(),
                name = subscription.name,
                amountPaise = subscription.amountPaise,
                billingCycle = subscription.billingCycle,
                nextBillingDateEpochMillis = subscription.nextBillingDateEpochMillis,
                category = subscription.category,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }
}
