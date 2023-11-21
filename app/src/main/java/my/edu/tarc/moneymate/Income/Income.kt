package my.edu.tarc.moneymate.Income

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Income")
data class Income (
    @PrimaryKey(autoGenerate = true)
    val incomeId: Long = 0,
    var title:String,
    var image: Int,
    var description: String,
    var amount: Int,
    var categoryId: String,
    var accountId: String,
)