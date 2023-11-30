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
        val status = when {
            netBalance >= account.accountBalance * 0.3 -> FinancialHealthStatus.HEALTHY
            netBalance > 0 -> FinancialHealthStatus.ATTENTION
            else -> FinancialHealthStatus.DANGER
        }
        val tips = provideFinancialTips(status)
        Log.d("show total income", totalIncome.toString())
        return AccountFinancialHealth(account.accountId, account.accountName, netBalance, status, tips)
    }

    fun analyzeFinancialHealth(totalIncome: Int, totalExpenses: Int): FinancialHealthStatus {
        // Simplified example logic
        val balance = totalIncome - totalExpenses
        return if (balance >= 0) FinancialHealthStatus.HEALTHY else FinancialHealthStatus.DANGER
    }

    fun provideFinancialTips(healthStatus: FinancialHealthStatus): List<String> {
        val tips = when (healthStatus) {
            FinancialHealthStatus.HEALTHY -> listOf("Continue saving 20% of your income", "Consider investing in stocks", "Create an emergency fund","Consider increasing contributions to your retirement savings plan to maximize future financial security.","Start planning for estate management and wills to secure your financial legacy.",
            "Explore diversifying your investment portfolio to spread risk.","Aim for an emergency fund that covers 6-12 months of living expenses.",
            " Save for major purchases like a house or car to avoid high-interest debt.")
            FinancialHealthStatus.ATTENTION -> listOf("Review your budget", "Cut down on discretionary spending", "Focus on essential expenses","Revisit your budget to identify areas where you can cut back."
            ,"Focus on reducing discretionary spending like dining out, entertainment, and luxury items.","Look into refinancing options for any high-interest loans to reduce your interest burden.",
             "Set up automatic transfers to a savings account to consistently build your reserves","Consider consulting a financial advisor to help you navigate this delicate financial situation."
                )
            FinancialHealthStatus.DANGER -> listOf("Reduce unnecessary expenses", "Look for additional income sources", "Focus on paying off high-interest debts","Focus on paying down high-interest debts as quickly as possible."
            ,"Eliminate all non-essential expenses and live as frugally as possible.","Reach out to creditors to negotiate payment plans or lower interest rates.","Get help from financial counseling services to manage debt and plan a route to recovery."
                ,"Develop a strict budget that covers only essential living expenses."
            )
        }
        return listOf(tips.random()) // Return a list containing one randomly selected tip
    }
}

enum class FinancialHealthStatus {
    HEALTHY, ATTENTION ,DANGER
}
data class AccountFinancialHealth(
    val accountId: Long,
    val accountName: String,
    val netBalance: Double,
    val status: FinancialHealthStatus,
    val financialTips: List<String>
)