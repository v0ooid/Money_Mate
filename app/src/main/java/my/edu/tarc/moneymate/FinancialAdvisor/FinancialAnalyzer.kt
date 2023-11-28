package my.edu.tarc.moneymate.FinancialAdvisor

import android.util.Log
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount

class FinancialAnalyzer {
    fun analyzeAccountFinancialHealth(account: MonetaryAccount, incomes: List<Income>, expenses: List<Expense>): AccountFinancialHealth {
        val totalIncome = incomes.sumOf { it.amount }
        val totalExpenses = expenses.sumOf { it.amount }
        val netBalance = account.accountBalance + totalIncome - totalExpenses
        val status = if (netBalance >= 0) FinancialHealthStatus.HEALTHY else FinancialHealthStatus.DANGER
        val tips = provideFinancialTips(status)
        Log.d("show total income", totalIncome.toString())
        return AccountFinancialHealth(account.accountId, account.accountName,netBalance, status, tips)
    }

    fun analyzeFinancialHealth(totalIncome: Int, totalExpenses: Int): FinancialHealthStatus {
        // Simplified example logic
        val balance = totalIncome - totalExpenses
        return if (balance >= 0) FinancialHealthStatus.HEALTHY else FinancialHealthStatus.DANGER
    }

    fun provideFinancialTips(healthStatus: FinancialHealthStatus): List<String> {
        // Example tips based on financial status
        return when (healthStatus) {
            FinancialHealthStatus.HEALTHY -> listOf("Continue saving 20% of your income", "Consider investing in stocks","Continue saving 20% of your income",
                "Consider investing in stocks",
                "Create an emergency fund")
            FinancialHealthStatus.DANGER -> listOf("Reduce unnecessary expenses", "Look for additional income sources","Review your spending habits",
                "Consider a side hustle for extra income",
                "Focus on paying off high-interest debts")
        }
    }
}

enum class FinancialHealthStatus {
    HEALTHY, DANGER
}
data class AccountFinancialHealth(
    val accountId: Long,
    val accountName: String,
    val netBalance: Double,
    val status: FinancialHealthStatus,
    val financialTips: List<String>
)