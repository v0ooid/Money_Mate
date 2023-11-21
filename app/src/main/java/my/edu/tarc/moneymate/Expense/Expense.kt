package my.edu.tarc.moneymate.Expense

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Expense")
data class Expense (
    @PrimaryKey(autoGenerate = true)
    val expenseId: Int = 0,
    var expense_title:String,
    var expense_icon_image: Int,
    var description:String,
    var amount: Int,
    var categoryId: String,
    var accountId: String
)