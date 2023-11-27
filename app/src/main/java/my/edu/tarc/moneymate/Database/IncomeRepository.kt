package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Income.Income
import java.util.Date

class IncomeRepository (private val incomeDao: IncomeDao) {
    val getAllIncome: LiveData<MutableList<Income>> = incomeDao.getAllData()

    suspend fun getIncomeByCriteria(
        accountId: Long,
        categoryId: Long,
        startDate: Date,
        endDate: Date
    ): List<Income> {
        return incomeDao.getIncomeByCriteria(accountId, categoryId, startDate, endDate)
    }

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
