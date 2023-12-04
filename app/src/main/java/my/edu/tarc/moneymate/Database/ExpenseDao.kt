package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.DataExport.ExpenseWithAccountName
import my.edu.tarc.moneymate.DataExport.IncomeWithAccountName
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import java.util.Date


@Dao
interface ExpenseDao {


    @Query("SELECT * FROM expense WHERE expenseId = :expenseId")
    suspend fun getExpenseById(expenseId: Long): Expense

    @Query("DELETE FROM Expense")
    suspend fun deleteAll()

    @Query("SELECT * FROM Expense")
    fun getAllExpense(): LiveData<MutableList<Expense>>
//
//    fun getAllData(): LiveData<MutableList<Expense>>

    @Query("SELECT * FROM Expense")
    fun getExpenseSync(): List<Expense>

    @Query(
        "SELECT Expense.expense_title, Expense.description, Expense.amount, Expense.date, monetary_accounts.accountName, Category.title as categoryName " +
                "FROM Expense " +
                "INNER JOIN monetary_accounts ON Expense.accountId = monetary_accounts.accountId " +
                "INNER JOIN Category ON Expense.categoryId = Category.categoryId " +
                "WHERE Expense.accountId = :accountId " +
                "AND Expense.categoryId = :categoryId " +
                "AND Expense.date BETWEEN :startDate AND :endDate"
    )
    fun getExpenseByCriteria(
        accountId: Long,
        categoryId: Long,
        startDate: String,
        endDate: String
    ): List<ExpenseWithAccountName>

    @Query("SELECT * FROM Expense")
    fun getAllExpenseRecord(): LiveData<List<Expense>>

    @Query("SELECT * FROM Expense WHERE accountId = :accountId")
    fun getExpensesForAccount(accountId: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM Expense WHERE strftime('%Y-%m', date) = :yearMonth")
    suspend fun getExpensesForMonth(yearMonth: String): List<Expense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<Expense>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}