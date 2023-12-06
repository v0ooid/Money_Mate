package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Expense.Expense


@Dao
interface ExpenseDao {

    @Query("DELETE FROM Expense")
    suspend fun deleteAll()

    @Query("SELECT * FROM Expense")
    fun getAllExpense(): LiveData<MutableList<Expense>>

    @Query("SELECT * FROM Expense")
    fun getExpenseSync(): List<Expense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<Expense>)
    @Query("SELECT * FROM Expense")
    fun getAllExpenseRecord(): LiveData<List<Expense>>

    @Query("SELECT * FROM Expense WHERE accountId = :accountId")
    fun getExpensesForAccount(accountId: Long): LiveData<List<Expense>>

    @Query("SELECT * FROM Expense WHERE strftime('%Y-%m', date) = :yearMonth")
    suspend fun getExpensesForMonth(yearMonth: String): List<Expense>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}