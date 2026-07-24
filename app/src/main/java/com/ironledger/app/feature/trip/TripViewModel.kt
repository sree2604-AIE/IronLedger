package com.ironledger.app.feature.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.TripRepository
import com.ironledger.app.domain.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {
    val trips: StateFlow<List<Trip>> = tripRepository.observeTrips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addTrip(name: String, budget: Double) {
        viewModelScope.launch {
            tripRepository.addTrip(
                Trip(
                    id = "",
                    name = name,
                    budgetPaise = (budget * 100).toLong(),
                    startDateEpochMillis = System.currentTimeMillis(),
                    endDateEpochMillis = null,
                    isActive = true
                )
            )
        }
    }
}
