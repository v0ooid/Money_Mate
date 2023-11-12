package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount


@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets")
    fun getAllBudget(): LiveData<List<Budget>>

    @Insert
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)
}