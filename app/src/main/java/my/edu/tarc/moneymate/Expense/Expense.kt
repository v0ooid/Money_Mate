package my.edu.tarc.moneymate.Expense

import android.accounts.Account
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import java.util.Date

@Entity(
    tableName = "Expense",

    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MonetaryAccount::class,
            parentColumns = ["accountId"],
            childColumns = ["accountId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Expense(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Long = 0,
    var expense_title: String,
    var expense_icon_image: Int,
    var description: String,
    var amount: Int,
    val date: Date,

    var categoryId: Long,
    var accountId: Long
)