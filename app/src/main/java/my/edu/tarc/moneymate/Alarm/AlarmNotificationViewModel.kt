package my.edu.tarc.moneymate.Alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AlarmNotificationRepository
import my.edu.tarc.moneymate.Database.AppDatabase

class AlarmNotificationViewModel(application: Application) : AndroidViewModel(application) {


    private val repository : AlarmNotificationRepository
    private val _notificaiton = MutableLiveData<List<AlarmNotification>>()
    val notification : LiveData<List<AlarmNotification>> get() = _notificaiton
    var allNotification : LiveData<List<AlarmNotification>>
    fun updateNotification(newNotification: List<AlarmNotification>){
        _notificaiton.value = newNotification
    }

    init {
        val alarmDao = AppDatabase.getDatabase(application).alarmNotificationDao()
        repository = AlarmNotificationRepository(alarmDao)
        allNotification= repository.allAlarms
    }
    fun insert(alarm: AlarmNotification) = viewModelScope.launch {
        repository.insert(alarm)
    }

    fun insertAll(alarm: List<AlarmNotification>) = viewModelScope.launch {
        repository.insertAll(alarm)
    }

    fun delete(alarm: AlarmNotification) = viewModelScope.launch {
        repository.delete(alarm)
    }

    fun getAlarmById(id: Long): LiveData<AlarmNotification> {
        return repository.getAlarmById(id)
    }

    fun updateAlarm(alarm: AlarmNotification) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(alarm)
        }
    }

}