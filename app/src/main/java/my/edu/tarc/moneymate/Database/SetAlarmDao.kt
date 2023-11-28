package my.edu.tarc.moneymate.Database

import androidx.room.Dao
import androidx.room.Insert
import my.edu.tarc.moneymate.Alarm.AlarmNotification

@Dao
interface SetAlarmDao {
    @Insert
    fun insertAlarm(alarmNotification: AlarmNotification)
}