package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount

class FinancialAdvisorRepository(private val monetaryAccountDao: MonetaryAccountDao,private val incomeDao: IncomeDao, private val expenseDao: ExpenseDao) {

    fun getAllMonetaryAccounts(): LiveData<List<MonetaryAccount>> = monetaryAccountDao.getAllAccounts()
    fun getIncomesForAccount(accountId: Long): LiveData<List<Income>> = incomeDao.getIncomesForAccount(accountId)
    fun getExpensesForAccount(accountId: Long): LiveData<List<Expense>> = expenseDao.getExpensesForAccount(accountId)
    suspend fun getMonetaryAccounts(): List<MonetaryAccount> = monetaryAccountDao.getAllAccountsData()
    fun getAllIncome() = incomeDao.getAllIncomeRecord()
    fun getAllExpenses() = expenseDao.getAllExpenseRecord()
    suspend fun getMonthlyIncome(year: Int, month: Int): List<Income> {
        val yearMonth = String.format("%04d-%02d", year, month)
        return incomeDao.getIncomeForMonth(yearMonth)
    }

    suspend fun getMonthlyExpenses(year: Int, month: Int): List<Expense> {
        val yearMonth = String.format("%04d-%02d", year, month)
        return expenseDao.getExpensesForMonth(yearMonth)
    }





    // ... other methods ...
}