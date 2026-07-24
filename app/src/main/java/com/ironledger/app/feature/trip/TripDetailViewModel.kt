package com.ironledger.app.feature.trip

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.TripRepository
import com.ironledger.app.domain.Trip
import com.ironledger.app.domain.TripMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TripDetailState(
    val trip: Trip? = null,
    val members: List<TripMember> = emptyList()
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository
) : ViewModel() {
    private val tripId: String = savedStateHandle.get<String>("tripId") ?: ""

    val state: StateFlow<TripDetailState> = tripRepository.observeTrips()
        .map { trips ->
            TripDetailState(
                trip = trips.firstOrNull { it.id == tripId },
                members = emptyList() // Extended when TripMember repository is available
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TripDetailState()
        )
}
