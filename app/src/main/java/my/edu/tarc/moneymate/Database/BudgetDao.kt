package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.Budget.BudgetWithExpenses
import my.edu.tarc.moneymate.Income.Income


@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId")
    suspend fun getBudgetByCategory(categoryId: Long): Budget?

    @Transaction
    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId")
    suspend fun getBudgetWithExpenses(categoryId: Long): BudgetWithExpenses?

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()

    @Query("SELECT * FROM budgets")
    fun getAllData(): LiveData<List<Budget>>

    @Query("SELECT * FROM budgets")
    fun getBudgetsSync(): List<Budget>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<Budget>)

    @Insert
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)
}