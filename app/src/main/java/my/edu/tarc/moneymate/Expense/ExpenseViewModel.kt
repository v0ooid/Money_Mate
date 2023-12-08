package my.edu.tarc.moneymate.Expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Budget.BudgetViewModel
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.BudgetRepository
import my.edu.tarc.moneymate.Database.ExpenseRepository
import my.edu.tarc.moneymate.Database.IncomeRepository
import my.edu.tarc.moneymate.Database.MonetaryAccountRepository
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import kotlin.math.exp

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val _result = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    val _selectedAccount = MutableLiveData<String>()
    private val readAllData : LiveData<MutableList<Expense>>

    val result: LiveData<String> get() = _result
    val title: LiveData<String> get() = _title
    val selectedAccount: LiveData<String> get() = _selectedAccount
    fun updateData(newData: String)
    {
        _result.value = newData
    }
    fun updateTitleData(newData: String)
    {
        _title.value = newData
    }
    fun updateSelectedAccountData(newData: String)
    {
        _selectedAccount.value = newData
    }



    private val repository: ExpenseRepository
    private val accountRepo: MonetaryAccountRepository
    private val repositoryBudget: BudgetRepository

    init{
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        val mAccountDao = AppDatabase.getDatabase(application).monetaryAccountDao()
        val budgetDao = AppDatabase.getDatabase(application).budgetDao()


        repository = ExpenseRepository(expenseDao)
        accountRepo = MonetaryAccountRepository(mAccountDao)
        repositoryBudget = BudgetRepository(budgetDao)


        readAllData = repository.getAllExpense
    }


    fun addExpense(expense: Expense)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addExpense(expense)

            val accountId = expense.accountId

            val account : MonetaryAccount = accountRepo.getAccountbyId2(accountId.toString())

            if (account != null){
                account.accountBalance -= expense.amount

                // Update the account balance in the repository
                accountRepo.updateAccount(account)
            }
        }
    }



    fun deleteExpense(expense:Expense) = viewModelScope.launch {
        val deletedExpense = repository.getExpenseById(expense.expenseId)

        repository.deleteExpense(expense)

        val expenseDifference = -deletedExpense.amount

        updateMonetaryAccountBalance(expense.accountId, expenseDifference)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        val oldIncome = repository.getExpenseById(expense.expenseId)

        repository.updateExpense(expense)

        val expenseDifference = expense.amount - oldIncome.amount

        updateMonetaryAccountBalance(expense.accountId, expenseDifference)
    }

    private suspend fun updateMonetaryAccountBalance(accountId: Long, amountDifference: Int) {
        viewModelScope.launch {
            val account = accountRepo.getAccountbyId2(accountId.toString())
            account?.let {
                it.accountBalance -= amountDifference
                // Update the account in the repository or database
                accountRepo.updateAccount(it)
            }
        }
    }
}