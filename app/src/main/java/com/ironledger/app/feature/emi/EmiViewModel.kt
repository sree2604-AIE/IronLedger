package com.ironledger.app.feature.emi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.EmiRepository
import com.ironledger.app.domain.Emi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EmiViewModel @Inject constructor(
    private val emiRepository: EmiRepository
) : ViewModel() {
    val emis: StateFlow<List<Emi>> = emiRepository.observeEmis()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
