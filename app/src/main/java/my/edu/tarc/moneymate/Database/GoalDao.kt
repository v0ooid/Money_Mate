package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Goal.SavedAmount
import my.edu.tarc.moneymate.Income.Income

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals")
    fun getGoalSync(): List<Goal>

    @Query("SELECT * FROM goals")
    fun getAllGoals(): LiveData<List<Goal>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM Goals WHERE id = :goalId")
    fun getGoalById(goalId: Long): LiveData<Goal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<Goal>)

    // Insert a new saved amount record
    @Insert
    suspend fun insert(savedAmount: SavedAmount)

    // Get all saved amounts for a specific goal
    @Query("SELECT * FROM saved_amounts WHERE goalId = :goalId")
    fun getSavedAmountsByGoalId(goalId: Long): LiveData<List<SavedAmount>>

    // Update a saved amount record
    @Update
    suspend fun update(savedAmount: SavedAmount)

    // Delete a saved amount record
    @Delete
    suspend fun delete(savedAmount: SavedAmount)
}