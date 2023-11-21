package my.edu.tarc.moneymate.Record

import java.time.LocalDate

data class Record(var amount:Int, val date: LocalDate)
data class DateSeperator(var date: LocalDate)
enum class TransactionType{Income,Expense, Transfer}

sealed class TransactionItem {
    data class Income(val id: String, val amount: Double, val date: LocalDate) : TransactionItem()
    data class Expense(val id: String, val amount: Double, val date: LocalDate) : TransactionItem()
    data class Transfer(val id: String, val amount: Double, val date: LocalDate) : TransactionItem()
    data class DateSeparator(val date: LocalDate) : TransactionItem()
}