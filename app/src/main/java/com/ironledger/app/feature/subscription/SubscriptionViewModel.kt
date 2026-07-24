package com.ironledger.app.feature.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.SubscriptionRepository
import com.ironledger.app.domain.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    val subscriptions: StateFlow<List<Subscription>> = subscriptionRepository.observeSubscriptions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addSubscription(name: String, amountPaise: Long, billingCycle: String, category: String) {
        viewModelScope.launch {
            val nextBilling = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
            subscriptionRepository.addSubscription(
                Subscription(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    amountPaise = amountPaise,
                    billingCycle = billingCycle,
                    nextBillingDateEpochMillis = nextBilling,
                    category = category
                )
            )
        }
    }
}
