package my.edu.tarc.moneymate.DataExport

import java.util.Date

data class IncomeWithAccountName(
    val incomeTitle: String,
    val description: String,
    val amount: Int,
    val date: String,
    val accountName: String,
    val categoryName: String
)
