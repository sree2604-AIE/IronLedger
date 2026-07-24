package com.ironledger.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ironledger.app.domain.AccentColor
import com.ironledger.app.domain.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.ironLedgerDataStore by preferencesDataStore(name = "ironledger_preferences")

@Singleton
class DataStorePreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesRepository {
    override val preferences: Flow<UserPreferences> = context.ironLedgerDataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { prefs ->
            UserPreferences(
                themeMode = prefs[Keys.themeMode]?.let(ThemeMode::valueOf) ?: ThemeMode.AMOLED,
                accentColor = prefs[Keys.accentColor]?.let(AccentColor::valueOf) ?: AccentColor.EMERALD,
                hideBalances = prefs[Keys.hideBalances] ?: false,
                userName = prefs[Keys.userName] ?: ""
            )
        }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        context.ironLedgerDataStore.edit { it[Keys.themeMode] = themeMode.name }
    }

    override suspend fun setAccentColor(accentColor: AccentColor) {
        context.ironLedgerDataStore.edit { it[Keys.accentColor] = accentColor.name }
    }

    override suspend fun setHideBalances(hidden: Boolean) {
        context.ironLedgerDataStore.edit { it[Keys.hideBalances] = hidden }
    }

    override suspend fun setUserName(name: String) {
        context.ironLedgerDataStore.edit { it[Keys.userName] = name }
    }

    private object Keys {
        val themeMode = stringPreferencesKey("theme_mode")
        val accentColor = stringPreferencesKey("accent_color")
        val hideBalances = booleanPreferencesKey("hide_balances")
        val userName = stringPreferencesKey("user_name")
    }
}
