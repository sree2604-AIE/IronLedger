package com.ironledger.app.feature.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.PreferencesRepository
import com.ironledger.app.data.repository.UserPreferences
import com.ironledger.app.domain.AccentColor
import com.ironledger.app.domain.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val preferences = preferencesRepository.preferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences()
    )

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(themeMode) }
    }

    fun setAccent(accentColor: AccentColor) {
        viewModelScope.launch { preferencesRepository.setAccentColor(accentColor) }
    }

    fun setHidden(hidden: Boolean) {
        viewModelScope.launch { preferencesRepository.setHideBalances(hidden) }
    }
}
