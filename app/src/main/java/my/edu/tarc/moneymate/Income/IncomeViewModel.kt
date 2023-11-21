package my.edu.tarc.moneymate.Income

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.recursiveFetchLongSparseArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.IncomeRepository

class IncomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _result = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    val _selectedAccount = MutableLiveData<String>()
    private val readAllData : LiveData<MutableList<Income>>

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
    init{
        val incomeDao = AppDatabase.getDatabase(application).incomeDao()
        repository = IncomeRepository(incomeDao)
        readAllData = repository.getAllIncome
    }
    fun addIncome(income: Income)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addIncome(income)
        }
    }
}