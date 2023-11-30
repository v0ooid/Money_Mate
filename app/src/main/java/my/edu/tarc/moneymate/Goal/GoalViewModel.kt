package my.edu.tarc.moneymate.Goal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Alarm.AlarmNotification
import my.edu.tarc.moneymate.Database.GoalRepository

class GoalViewModel(private val repository: GoalRepository) : ViewModel() {

    val allGoals: LiveData<List<Goal>> = repository.allGoals
    private val _goal = MutableLiveData<List<Goal>>()
    val goal : LiveData<List<Goal>> get() = _goal

    fun updateGoal(goal: List<Goal>){
        _goal.value = goal
    }

    fun insert(goal: Goal) = viewModelScope.launch {
        repository.insert(goal)
    }

    fun update(goal: Goal) = viewModelScope.launch {
        repository.update(goal)
        Log.d("view model", "got run update viewmodel")
    }

    fun delete(goal: Goal) = viewModelScope.launch {
        repository.delete(goal)
    }

    fun getGoalById(goalId: Long): LiveData<Goal> {
        return repository.getGoalById(goalId)
    }

    class GoalViewModelFactory(private val repository: GoalRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GoalViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GoalViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}