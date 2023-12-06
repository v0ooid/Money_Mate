package my.edu.tarc.moneymate.Goal

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "Goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title:String,
    var description: String,
    var targetAmount: Int,
    var savedAmount: Int,
    var desiredDate: String,
    var alarmTime: Long? = null // Store the time for the alarm
)
