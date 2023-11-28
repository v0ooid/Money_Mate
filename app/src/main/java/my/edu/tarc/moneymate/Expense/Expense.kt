package my.edu.tarc.moneymate.Expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import java.time.LocalDate

@Entity(tableName = "Expense",
    foreignKeys = [ForeignKey(entity = MonetaryAccount::class,
        parentColumns = ["accountId"],
        childColumns = ["accountId"],
        onDelete = ForeignKey.CASCADE)])
data class Expense (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String,
    var image: Int,
    var description: String,
    var amount: Int,
    var categoryId: String,
    @ColumnInfo(index = true) // Creates an index on accountId for performance
    var accountId: String,
    var date: String,
)