package my.edu.tarc.moneymate.Alarm

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import my.edu.tarc.moneymate.ListStringConverter

@Entity
data class AlarmNotification (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minit: Int,
    val title: String,
    val description: String,
    @TypeConverters(ListStringConverter::class)
    val repeatDay: List<String>
)