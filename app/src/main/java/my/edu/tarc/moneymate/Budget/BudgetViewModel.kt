package my.edu.tarc.moneymate.Budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.BudgetRepository
import my.edu.tarc.moneymate.Database.CategoryRepository


class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private var repositoryBudget: BudgetRepository
    private var repositoryCategory: CategoryRepository

    var getAllBudget: LiveData<List<Budget>>
    var getExpenseCategory: LiveData<MutableList<Category>>


    init {
        val budgetDao = AppDatabase.getDatabase(application).budgetDao()
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()

        repositoryBudget = BudgetRepository(budgetDao)
        repositoryCategory = CategoryRepository(categoryDao)

        getAllBudget = repositoryBudget.getAllBudget
        getExpenseCategory = repositoryCategory.getExpenseCategory

    }

    fun addBudget(budget: Budget) = viewModelScope.launch {
        repositoryBudget.addBudget(budget)
    }

    fun updateBudget(budget: Budget) = viewModelScope.launch {
        repositoryBudget.updateBudget(budget)
    }

    fun deleteBudget(budget: Budget) = viewModelScope.launch {
        repositoryBudget.deleteBudget(budget)
    }
}