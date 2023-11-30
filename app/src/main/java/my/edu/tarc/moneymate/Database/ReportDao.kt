package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Income.Income

@Dao
interface ReportDao {
    @Query("SELECT * FROM Income")
    fun getAllIncome(): LiveData<List<Income>>

    @Query("SELECT * FROM Goals")
    fun getAllGoals(): LiveData<List<Goal>>

    @Query("SELECT * FROM Expense")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM Income WHERE SUBSTR(date, 1, 7) BETWEEN :startMonthYear AND :endMonthYear")
    fun getIncomeInRange(startMonthYear: String, endMonthYear: String): LiveData<List<Income>>

    @Query("SELECT * FROM Expense WHERE SUBSTR(date, 1, 7) BETWEEN :startMonthYear AND :endMonthYear")
    fun getExpensesInRange(startMonthYear: String, endMonthYear: String): LiveData<List<Expense>>
    @Query("SELECT * FROM Goals WHERE SUBSTR(desiredDate, 1, 7) BETWEEN :startMonthYear AND :endMonthYear")
    fun getGoalInRange(startMonthYear: String, endMonthYear: String): LiveData<List<Goal>>
}