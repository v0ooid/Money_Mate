package my.edu.tarc.moneymate.Gamification

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import my.edu.tarc.moneymate.R

object GamificationHelper {

    fun checkTasksAndLevelUp(sharedPreferences: SharedPreferences, context: Context) {
        // Retrieve task completion counts
        val loggedInDays = sharedPreferences.getInt("LoggedInDays", 0)
        val recordedIncomes = sharedPreferences.getInt("RecordedIncomes", 0)
        val recordedExpenses = sharedPreferences.getInt("RecordedExpenses", 0)

        // Check if all tasks are completed
        if (loggedInDays >= 5 && recordedIncomes >= 5 && recordedExpenses >= 5) {
            // Level up or award badge
            levelUpOrAwardBadge(sharedPreferences, context)
        }
    }

    private fun levelUpOrAwardBadge(sharedPreferences: SharedPreferences, context: Context) {
        var level = sharedPreferences.getInt("Level", 0) // Get the current level
        level++

        Toast.makeText(context.applicationContext, "Congratulations!\nYou've reached Level $level!",
            Toast.LENGTH_LONG).show()

        // Reset task completion counts to 0
        sharedPreferences.edit {
            putInt("LoggedInDays", 0)
            putInt("RecordedIncomes", 0)
            putInt("RecordedExpenses", 0)
            putInt("Level", level)
        }
    }
}