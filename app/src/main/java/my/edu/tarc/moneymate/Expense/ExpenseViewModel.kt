package my.edu.tarc.moneymate.Expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.ExpenseRepository
import my.edu.tarc.moneymate.Database.IncomeRepository
import my.edu.tarc.moneymate.Income.Income
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
    init{
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)
        readAllData = repository.getAllExpense
    }
    fun addExpense(expense: Expense)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addExpense(expense)
        }
    }
    fun deleteExpense(expense:Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
    }
    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.updateExpense(expense)
    }
}