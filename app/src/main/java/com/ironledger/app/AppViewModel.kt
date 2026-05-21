package com.ironledger.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.PreferencesRepository
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.data.repository.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {
    val preferences = preferencesRepository.preferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences()
    )

    init {
        viewModelScope.launch { transactionRepository.seedIfEmpty() }
    }

    fun setHideBalances(hidden: Boolean) {
        viewModelScope.launch { preferencesRepository.setHideBalances(hidden) }
    }
}
