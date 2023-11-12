package my.edu.tarc.moneymate.Budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.BudgetRepository



class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: BudgetRepository
    var getAllBudget: LiveData<List<Budget>>

    init {
        val budgetDao = AppDatabase.getDatabase(application).budgetDao()
        repository = BudgetRepository(budgetDao)
        getAllBudget = repository.getAllBudget
    }

    fun addBudget(budget: Budget) = viewModelScope.launch {
        repository.addBudget(budget)
    }

    fun updateCategory(budget: Budget) = viewModelScope.launch {
        repository.updateBudget(budget)
    }

    fun deleteAccount(budget: Budget) = viewModelScope.launch {
        repository.deleteBudget(budget)
    }
}