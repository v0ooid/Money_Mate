package my.edu.tarc.moneymate.DataExport

data class ExpenseWithAccountName(
    val expense_title: String,
    val description: String,
    val amount: Int,
    val date: String,
    val accountName: String,
    val categoryName: String
)