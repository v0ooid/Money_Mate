package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.Income.Income

class IncomeRepository (private val incomeDao: IncomeDao) {
    val getAllIncome: LiveData<MutableList<Income>> = incomeDao.getAllIncome()
    @WorkerThread
    suspend fun addIncome(income: Income){
        incomeDao.insertIncome(income)
    }

    @WorkerThread
    suspend fun deleteIncome(income: Income){
        incomeDao.deleteIncome(income)
    }

    @WorkerThread
    suspend fun updateIncome(income: Income){
        incomeDao.updateIncome(income)
    }

}
