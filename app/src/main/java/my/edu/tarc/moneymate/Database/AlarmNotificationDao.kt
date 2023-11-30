package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Alarm.AlarmNotification

@Dao
interface AlarmNotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmNotification): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alarms: List<AlarmNotification>)

    @Query("SELECT * FROM AlarmNotification")
    fun getAllAlarms(): LiveData<List<AlarmNotification>>

    @Delete
    suspend fun deleteAlarm(alarm: AlarmNotification)

    @Query("SELECT * FROM AlarmNotification WHERE id = :id")
    fun getAlarmById(id: Long): LiveData<AlarmNotification>

    @Update
    fun updateAlarm(alarm: AlarmNotification)

    // Add any other database operations you need
}