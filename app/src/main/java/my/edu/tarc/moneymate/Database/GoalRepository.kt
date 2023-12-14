package my.edu.tarc.moneymate.Database

import android.util.Log
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Goal.SavedAmount

class GoalRepository(private val goalDao: GoalDao) {

    val allGoals: LiveData<List<Goal>> = goalDao.getAllGoals()

    suspend fun insert(goal: Goal) {
        goalDao.insertGoal(goal)
    }

    suspend fun update(goal: Goal) {
        goalDao.updateGoal(goal)
        Log.d("repository", goal.toString())

    }

    suspend fun delete(goal: Goal) {
        goalDao.deleteGoal(goal)
    }

    fun getGoalById(goalId: Long): LiveData<Goal> {
        return goalDao.getGoalById(goalId)
    }
    fun getSavedAmountsByGoalId(goalId: Long): LiveData<List<SavedAmount>> = goalDao.getSavedAmountsByGoalId(goalId)

    suspend fun insertSavedAmount(savedAmount: SavedAmount) = goalDao.insert(savedAmount)
}