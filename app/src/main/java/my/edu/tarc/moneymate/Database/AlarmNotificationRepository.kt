package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Alarm.AlarmNotification

class AlarmNotificationRepository(private val alarmNotificationDao: AlarmNotificationDao) {

    val allAlarms: LiveData<List<AlarmNotification>> = alarmNotificationDao.getAllAlarms()

    suspend fun insert(alarm: AlarmNotification) {
        alarmNotificationDao.insertAlarm(alarm)
    }

    suspend fun insertAll(alarms: List<AlarmNotification>) {
        alarmNotificationDao.insertAll(alarms)
    }
    suspend fun delete(alarm: AlarmNotification) {
        alarmNotificationDao.deleteAlarm(alarm)
    }

    fun getAlarmById(id: Long): LiveData<AlarmNotification> {
        return alarmNotificationDao.getAlarmById(id)
    }

    suspend fun update(alarm: AlarmNotification) {
        alarmNotificationDao.updateAlarm(alarm)
    }

    // Add any other repository methods you need
}