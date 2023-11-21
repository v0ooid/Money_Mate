package my.edu.tarc.moneymate.MonetaryAccount

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.MonetaryAccountRepository

class MonetaryAccountViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: MonetaryAccountRepository
    var getAllmAccount: LiveData<List<MonetaryAccount>>
    var totalAmount: LiveData<Double>

    init {
        val mAccountDao = AppDatabase.getDatabase(application).monetaryAccountDao()
        repository = MonetaryAccountRepository(mAccountDao)
        getAllmAccount = repository.getAllAccount
        totalAmount = repository.getTotalAmount()
    }

    fun addAccount(monetaryAccount: MonetaryAccount) = viewModelScope.launch {
        repository.addAccount(monetaryAccount)
    }

    fun updateAccount(monetaryAccount: MonetaryAccount) = viewModelScope.launch {
        repository.updateAccount(monetaryAccount)
    }

    fun deleteAccount(monetaryAccount: MonetaryAccount) = viewModelScope.launch {
        repository.deleteAccount(monetaryAccount)
    }

}