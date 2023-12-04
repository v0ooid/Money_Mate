package my.edu.tarc.moneymate.Gamification

import android.content.SharedPreferences
import androidx.core.content.edit

object GamificationHelper {
    fun checkTasksAndLevelUp(sharedPreferences: SharedPreferences) {
        // Retrieve task completion counts
        val loggedInDays = sharedPreferences.getInt("LoggedInDays", 0)
        val recordedIncomes = sharedPreferences.getInt("RecordedIncomes", 0)
        val recordedExpenses = sharedPreferences.getInt("RecordedExpenses", 0)

        // Check if all tasks are completed
        if (loggedInDays >= 5 && recordedIncomes >= 5 && recordedExpenses >= 5) {
            // Level up or award badge
            levelUpOrAwardBadge(sharedPreferences)
        }
    }

    private fun levelUpOrAwardBadge(sharedPreferences: SharedPreferences) {
        var level = sharedPreferences.getInt("Level", 0) // Get the current level
        level++ // Increment the level

        // Reset task completion counts to 0
        sharedPreferences.edit {
            putInt("LoggedInDays", 0)
            putInt("RecordedIncomes", 0)
            putInt("RecordedExpenses", 0)
            putInt("Level", level)
        }
    }
}