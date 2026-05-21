package com.ironledger.app.feature.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.data.repository.AccountRepository
import com.ironledger.app.data.repository.CategoryRepository
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.domain.Account
import com.ironledger.app.domain.Category
import com.ironledger.app.domain.NewTransaction
import com.ironledger.app.domain.PaymentMethod
import com.ironledger.app.domain.TransactionStatus
import com.ironledger.app.domain.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionEditorState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amountText: String = "",
    val note: String = "",
    val selectedAccountId: String? = null,
    val selectedCategoryId: String? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.UPI,
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TransactionEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    accountRepository: AccountRepository,
    categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val type = when (savedStateHandle.get<String>("type")) {
        "income" -> TransactionType.INCOME
        else -> TransactionType.EXPENSE
    }

    private val _state = MutableStateFlow(TransactionEditorState(type = type))
    val state: StateFlow<TransactionEditorState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.observeAccounts().collect { accounts ->
                _state.update { current ->
                    current.copy(
                        accounts = accounts,
                        selectedAccountId = current.selectedAccountId ?: accounts.firstOrNull()?.id
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryRepository.observeByType(type).collect { categories ->
                _state.update { current ->
                    current.copy(
                        categories = categories,
                        selectedCategoryId = current.selectedCategoryId ?: categories.firstOrNull()?.id
                    )
                }
            }
        }
    }

    fun setAmount(value: String) {
        _state.update { it.copy(amountText = value.filter { char -> char.isDigit() || char == '.' }, error = null) }
    }

    fun setNote(value: String) {
        _state.update { it.copy(note = value, error = null) }
    }

    fun selectAccount(id: String) {
        _state.update { it.copy(selectedAccountId = id, error = null) }
    }

    fun selectCategory(id: String) {
        _state.update { it.copy(selectedCategoryId = id, error = null) }
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _state.update { it.copy(paymentMethod = method, error = null) }
    }

    fun save(onSaved: () -> Unit) {
        val current = _state.value
        val amount = MoneyFormatter.parseToPaise(current.amountText)
        val accountId = current.selectedAccountId
        val categoryId = current.selectedCategoryId
        when {
            amount == null -> _state.update { it.copy(error = "Enter a valid amount") }
            accountId == null -> _state.update { it.copy(error = "Select an account") }
            categoryId == null -> _state.update { it.copy(error = "Select a category") }
            else -> viewModelScope.launch {
                _state.update { it.copy(isSaving = true, error = null) }
                runCatching {
                    transactionRepository.addTransaction(
                        NewTransaction(
                            type = current.type,
                            amountPaise = amount,
                            accountId = accountId,
                            categoryId = categoryId,
                            paymentMethod = current.paymentMethod,
                            note = current.note,
                            occurredAtEpochMillis = System.currentTimeMillis()
                        ),
                        status = TransactionStatus.CONFIRMED
                    )
                }.onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    onSaved()
                }.onFailure { throwable ->
                    _state.update { it.copy(isSaving = false, error = throwable.message ?: "Could not save transaction") }
                }
            }
        }
    }
}
