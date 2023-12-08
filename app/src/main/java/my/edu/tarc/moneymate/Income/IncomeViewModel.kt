package my.edu.tarc.moneymate.Income


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.IncomeRepository
import my.edu.tarc.moneymate.Database.MonetaryAccountRepository
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.Income.Income

class IncomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _result = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    val _selectedAccount = MutableLiveData<String>()
    private val readAllData : LiveData<MutableList<Income>>
    val incomeInRange: LiveData<List<Income>>



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
    private val repository: IncomeRepository
    private val accountRepo: MonetaryAccountRepository
    init{
        val incomeDao = AppDatabase.getDatabase(application).incomeDao()
        val mAccountDao = AppDatabase.getDatabase(application).monetaryAccountDao()

        repository = IncomeRepository(incomeDao)
        accountRepo = MonetaryAccountRepository(mAccountDao)

        readAllData = repository.getAllIncome
        incomeInRange = MutableLiveData()
    }
    fun addIncome(income: Income)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addIncome(income)

            val accountId = income.accountId

            val account : MonetaryAccount = accountRepo.getAccountbyId2(accountId.toString())

            if (account != null) {
                // Increase the account balance by the income amount
                account.accountBalance += income.amount

                // Update the account balance in the repository
                accountRepo.updateAccount(account)
            }
        }
    }

    fun deleteIncome(income:Income) = viewModelScope.launch {
        val deletedIncome = repository.getIncomeById(income.incomeId)

        repository.deleteIncome(income)

        val incomeDifference = -deletedIncome.amount

        // Update the monetary account balance
        updateMonetaryAccountBalance(income.accountId, incomeDifference)

    }

    fun updateIncome(income: Income) = viewModelScope.launch {
        // Get the old income record
        val oldIncome = repository.getIncomeById(income.incomeId)

        // Update the income record
        repository.updateIncome(income)

        // Calculate the difference in income amounts
        val incomeDifference = income.amount - oldIncome.amount

        // Update the monetary account balance
        updateMonetaryAccountBalance(income.accountId, incomeDifference)
    }

    private suspend fun updateMonetaryAccountBalance(accountId: Long, amountDifference: Int) {
        viewModelScope.launch {
            val account = accountRepo.getAccountbyId2(accountId.toString())
            account?.let {
                it.accountBalance += amountDifference
                // Update the account in the repository or database
                accountRepo.updateAccount(it)
            }
        }
    }

    fun getIncomeForDateRange(startDate: String, endDate: String) {
        (incomeInRange as MutableLiveData<List<Income>>).value = repository.getIncomeInRange(startDate, endDate).value
    }
}