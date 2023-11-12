package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount

class BudgetRepository(private val budgetDao: BudgetDao) {

    val getAllBudget: LiveData<List<Budget>> = budgetDao.getAllBudget()

    @WorkerThread
    suspend fun addBudget(budget: Budget){
        budgetDao.insertBudget(budget)
    }

    @WorkerThread
    suspend fun deleteBudget(budget: Budget){
        budgetDao.deleteBudget(budget)
    }

    @WorkerThread
    suspend fun updateBudget(budget: Budget){
        budgetDao.updateBudget(budget)
    }
}