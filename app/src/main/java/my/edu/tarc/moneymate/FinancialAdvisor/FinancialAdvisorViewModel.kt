package my.edu.tarc.moneymate.FinancialAdvisor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.FinancialAdvisorRepository
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
class FinancialAdvisorViewModel(
    application: Application,
    private val repository: FinancialAdvisorRepository,
    private val financialAnalyzer: FinancialAnalyzer
) : AndroidViewModel(application) {


    //
//    private val _totalIncome = MutableLiveData<Int>()
//    val totalIncome: LiveData<Int> = _totalIncome
//
//    private val _totalExpenses = MutableLiveData<Int>()
//    val totalExpenses: LiveData<Int> = _totalExpenses
//
//    private val _financialHealthStatus = MutableLiveData<FinancialHealthStatus>()
//    val financialHealthStatus: LiveData<FinancialHealthStatus> = _financialHealthStatus
//
//    private val _financialTips = MutableLiveData<List<String>>()
//    val financialTips: LiveData<List<String>> = _financialTips
//
//    private val _accountsFinancialHealth = MediatorLiveData<List<AccountFinancialHealth>>()
//    val accountsFinancialHealth: LiveData<List<AccountFinancialHealth>> = _accountsFinancialHealth
//
//    init {
//        analyzeFinancialHealth()
//        analyzeAllAccountsFinancialHealth()
//
//        val accountsLiveData = repository.getAllMonetaryAccounts()
//        _accountsFinancialHealth.addSource(accountsLiveData) { accounts ->
//            accounts.forEach { account ->
//                val incomesLiveData = repository.getIncomesForAccount(account.accountId)
//                val expensesLiveData = repository.getExpensesForAccount(account.accountId)
//
//                _accountsFinancialHealth.addSource(incomesLiveData) { incomes ->
//                    _accountsFinancialHealth.addSource(expensesLiveData) { expenses ->
//                        val accountHealth = financialAnalyzer.analyzeAccountFinancialHealth(
//                            account,
//                            incomes,
//                            expenses
//                        )
//                        // Update _accountsFinancialHealth with the new accountHealth
//                    }
//                }
//            }
//        }
//    }
//
//    private fun analyzeFinancialHealth() {
//        repository.getAllIncome().observeForever { incomeList ->
//            val totalIncome = incomeList.sumOf { it.amount }
//            _totalIncome.value = totalIncome
//            updateFinancialHealth()
//        }
//
//        repository.getAllExpenses().observeForever { expenseList ->
//            val totalExpenses = expenseList.sumOf { it.amount }
//            _totalExpenses.value = totalExpenses
//            updateFinancialHealth()
//        }
//    }
//
//    private fun analyzeAllAccountsFinancialHealth() {
//        viewModelScope.launch {
//            val accounts = repository.getAllMonetaryAccounts()
//            val accountsHealth = accounts.map { account ->
//                val incomes = repository.getIncomesForAccount(account.accountId)
//                val expenses = repository.getExpensesForAccount(account.accountId)
//                financialAnalyzer.analyzeAccountFinancialHealth(account, incomes, expenses)
//            }
//            _accountsFinancialHealth.value = accountsHealth
//        }
//    }
//
//
//    private fun updateFinancialHealth() {
//        val income = _totalIncome.value ?: 0
//        val expenses = _totalExpenses.value ?: 0
//        val healthStatus = financialAnalyzer.analyzeFinancialHealth(income, expenses)
//        _financialHealthStatus.value = healthStatus
//        _financialTips.value = financialAnalyzer.provideFinancialTips(healthStatus)
//    }


    private val _accountsFinancialHealth = MutableLiveData<List<AccountFinancialHealth>>()
    val accountsFinancialHealth: LiveData<List<AccountFinancialHealth>> = _accountsFinancialHealth

    init {
        fetchAndAnalyzeFinancialData()
    }

    private fun fetchAndAnalyzeFinancialData() {
        repository.getAllMonetaryAccounts().observeForever { accounts ->
            val healthList = mutableListOf<AccountFinancialHealth>() // Create a mutable list to accumulate results
            accounts.forEach { account ->
                viewModelScope.launch {
                    repository.getIncomesForAccount(account.accountId).observeForever { incomes ->
                        Log.d("IncomeData", incomes.toString())
                        repository.getExpensesForAccount(account.accountId).observeForever { expenses ->
                            val accountHealth = financialAnalyzer.analyzeAccountFinancialHealth(account, incomes, expenses)
                            healthList.add(accountHealth) // Add the result to the list
                            _accountsFinancialHealth.postValue(healthList) // Post the entire list
                        }
                    }
                }
            }
        }
    }
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FinancialAdvisorViewModel::class.java)) {
                val database = AppDatabase.getDatabase(application) // Replace with your actual database access method
                val monetaryAccountDao = database.monetaryAccountDao()
                val incomeDao = database.incomeDao()
                val expenseDao = database.expenseDao()
                @Suppress("UNCHECKED_CAST")
                return FinancialAdvisorViewModel(
                    application,
                    FinancialAdvisorRepository(monetaryAccountDao,incomeDao,expenseDao),  // Assuming default constructor, replace with actual initialization
                    FinancialAnalyzer()  // Assuming default constructor, replace with actual initialization
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
