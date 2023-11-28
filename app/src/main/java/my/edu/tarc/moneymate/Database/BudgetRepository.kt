package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Budget.Budget

class BudgetRepository(private val budgetDao: BudgetDao) {

    val getAllBudget: LiveData<List<Budget>> = budgetDao.getAllData()

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