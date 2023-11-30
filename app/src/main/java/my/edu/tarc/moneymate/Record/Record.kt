package my.edu.tarc.moneymate.Record

import androidx.room.Entity
import androidx.room.PrimaryKey
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import java.time.LocalDate


//data class DateSeperator(var date: LocalDate)
//enum class TransactionType{Income,Expense, Transfer}
//
//sealed class TransactionItem {
//    data class Income(val id: String, val amount: Double, val date: LocalDate) : TransactionItem()
//    data class Expense(val id: String, val amount: Double, val date: LocalDate) : TransactionItem()
//    data class Transfer(val id: String, val amount: Double, val date: LocalDate) : TransactionItem()
//    data class DateSeparator(val date: LocalDate) : TransactionItem()
//}
//data class IncomeExpenseCombined(
//    val incomeList: List<Income>,
//    val expenseList: List<Expense>
//)
@Entity(tableName = "Record")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title:String,
    var image: Int,
    var description: String,
    val amount: Int,
    var categoryId: String,
    var accountId: String,
    val type: String,
    var date: String,
)