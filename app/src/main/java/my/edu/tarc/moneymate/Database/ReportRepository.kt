package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Income.Income

class ReportRepository(private val reportDao: ReportDao) {
    fun getAllIncome(): LiveData<List<Income>> = reportDao.getAllIncome()
    fun getAllExpenses(): LiveData<List<Expense>> = reportDao.getAllExpenses()
    fun getAllGoals(): LiveData<List<Goal>> = reportDao.getAllGoals()

    fun getIncomeInRange(startMonthYear: String, endMonthYear: String): LiveData<List<Income>> {
        return reportDao.getIncomeInRange(startMonthYear, endMonthYear)
    }

    fun getExpensesInRange(startMonthYear: String, endMonthYear: String): LiveData<List<Expense>> {
        return reportDao.getExpensesInRange(startMonthYear, endMonthYear)
    }

    fun getGoalInRange(startMonthYear: String, endMonthYear: String): LiveData<List<Goal>> {
        return reportDao.getGoalInRange(startMonthYear, endMonthYear)
    }
    // Add other necessary repository methods here
}