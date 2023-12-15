package my.edu.tarc.moneymate.Goal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_amounts")
data class SavedAmount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val amount: Int,
    val date: Long
)