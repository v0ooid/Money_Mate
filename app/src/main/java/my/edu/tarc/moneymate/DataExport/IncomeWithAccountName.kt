package my.edu.tarc.moneymate.DataExport

import java.util.Date

data class IncomeWithAccountName(
    val incomeId: Long,
    var incomeTitle: String,
    var image: Int,
    var description: String,
    var amount: Int,
    val date: Date,
    var categoryId: Long,
    var accountId: Long,
    val accountName: String,
    val title: String // Add the missing 'title' field if it's returned by the query
)