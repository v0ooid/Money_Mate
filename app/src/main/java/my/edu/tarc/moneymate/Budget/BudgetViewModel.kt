package my.edu.tarc.moneymate.Budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.BudgetRepository
import my.edu.tarc.moneymate.Database.CategoryRepository
import my.edu.tarc.moneymate.Database.ExpenseRepository
import my.edu.tarc.moneymate.Expense.Expense


class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private var repositoryBudget: BudgetRepository
    private var repositoryCategory: CategoryRepository
    private var repositoryExpense: ExpenseRepository

    var getAllBudget: LiveData<List<Budget>>
    var getExpenseCategory: LiveData<MutableList<Category>>


    init {
        val budgetDao = AppDatabase.getDatabase(application).budgetDao()
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()

        repositoryBudget = BudgetRepository(budgetDao)
        repositoryCategory = CategoryRepository(categoryDao)
        repositoryExpense = ExpenseRepository(expenseDao)


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

    fun updateBudgetWithExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            val previousExpense = repositoryExpense.getExpenseById(expense.expenseId)
            val budget = repositoryBudget.getBudgetByCategory(expense.categoryId)

            if (budget != null) {
                val previousAmount = previousExpense?.amount ?: 0
                val expenseDifference = expense.amount - previousAmount

                // Update the budget spent by adding the expense difference
                val newSpent = budget.budgetSpent + expenseDifference
                budget.budgetSpent = newSpent.coerceAtLeast(0.0) // Ensure it doesn't go below zero

                // Save the updated budget in the repository
                repositoryBudget.updateBudget(budget)
            }
        }
    }

    fun deleteExpenseWithBudget(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            val previousExpense = repositoryExpense.getExpenseById(expense.expenseId)
            val budget = repositoryBudget.getBudgetByCategory(expense.categoryId)

            if (budget != null) {
                val previousAmount = previousExpense?.amount ?: 0
                val expenseDifference = previousAmount  // Using previous amount before deletion

                // Update the budget spent by subtracting the expense amount
                val newSpent = budget.budgetSpent - expenseDifference
                budget.budgetSpent = newSpent.coerceAtLeast(0.0) // Ensure it doesn't go below zero

                // Save the updated budget in the repository
                repositoryBudget.updateBudget(budget)
            }
        }
    }
}