package my.edu.tarc.moneymate.Report

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.ReportRepository
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Income.Income

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReportRepository // Initialize this with your DAO or data source

    init {
        val reportDao = AppDatabase.getDatabase(application).reportDao()
        repository = ReportRepository(reportDao)

    }

    val incomeItems: LiveData<List<Income>> = repository.getAllIncome()
    val expenseItems: LiveData<List<Expense>> = repository.getAllExpenses()
    val goalItems: LiveData<List<Goal>> = repository.getAllGoals()


    val reportItems: LiveData<List<ReportItem>> = MediatorLiveData<List<ReportItem>>().apply {
        val incomesLiveData = repository.getAllIncome()
        val goalsLiveData = repository.getAllGoals()
        val expensesLiveData = repository.getAllExpenses()

        addSource(incomesLiveData) { incomes ->
            value = combineData(incomes, goalsLiveData.value, expensesLiveData.value)
        }
        addSource(goalsLiveData) { goals ->
            value = combineData(incomesLiveData.value, goals, expensesLiveData.value)
        }
        addSource(expensesLiveData) { expenses ->
            value = combineData(incomesLiveData.value, goalsLiveData.value, expenses)
        }
    }


    private fun combineData(
        incomes: List<Income>?,
        goals: List<Goal>?,
        expenses: List<Expense>?
    ): List<ReportItem> {
        val combinedList = mutableListOf<ReportItem>()
        combinedList.addAll(incomes?.map { ReportItem.IncomeItem(it) } ?: emptyList())
        combinedList.addAll(goals?.map { ReportItem.GoalItem(it) } ?: emptyList())
        combinedList.addAll(expenses?.map { ReportItem.ExpenseItem(it) } ?: emptyList())
        return combinedList
    }

    fun getIncomeInRange(startMonthYear: String, endMonthYear: String): LiveData<List<Income>> {
        return repository.getIncomeInRange(startMonthYear, endMonthYear)
    }

    fun getExpensesInRange(startMonthYear: String, endMonthYear: String): LiveData<List<Expense>> {
        return repository.getExpensesInRange(startMonthYear, endMonthYear)
    }

    fun getFilteredData(startMonthYear: String, endMonthYear: String): LiveData<List<ReportItem>> {
        // Implement logic to filter data based on date range
        // This is a simplified example, replace with actual logic to fetch and filter data
        val filteredData : LiveData<List<ReportItem>> = MediatorLiveData<List<ReportItem>>().apply {
            val incomesLiveData = repository.getIncomeInRange(startMonthYear, endMonthYear)
            Log.d("Report View Model", "Show the example income $incomesLiveData startMonthYear $startMonthYear endMonthYear $endMonthYear")
            val goalsLiveData = repository.getGoalInRange(startMonthYear, endMonthYear)
            val expensesLiveData = repository.getExpensesInRange(startMonthYear, endMonthYear)
            addSource(incomesLiveData) { incomes ->
                value = combineData(incomes, goalsLiveData.value, expensesLiveData.value)
            }
            addSource(goalsLiveData) { goals ->
                value = combineData(incomesLiveData.value, goals, expensesLiveData.value)
            }
            addSource(expensesLiveData) { expenses ->
                value = combineData(incomesLiveData.value, goalsLiveData.value, expenses)
            }
        }
        return filteredData
    }
    fun getFilteredDataIncome(startMonthYear: String, endMonthYear: String): LiveData<List<Income>> {
        val incomesLiveData = repository.getIncomeInRange(startMonthYear, endMonthYear)
        return incomesLiveData
    }
    fun getFilteredDataExpense(startMonthYear: String, endMonthYear: String): LiveData<List<Expense>> {
        val expensesLiveData = repository.getExpensesInRange(startMonthYear, endMonthYear)
        return expensesLiveData
    }
    fun getFilteredDataGoal(startMonthYear: String, endMonthYear: String): LiveData<List<Goal>> {
        val goalsLiveData = repository.getGoalInRange(startMonthYear, endMonthYear)
        return goalsLiveData
    }
}