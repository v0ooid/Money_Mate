package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Budget.Budget


@Dao
interface BudgetDao {

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()

    @Query("SELECT * FROM budgets")
    fun getAllData(): LiveData<List<Budget>>

    @Query("SELECT * FROM budgets")
    fun getBudgetsSync(): List<Budget>

    @Insert
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)
}