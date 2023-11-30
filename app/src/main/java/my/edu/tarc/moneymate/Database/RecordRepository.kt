package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Record.Record

class RecordRepository(private val recordDao: RecordDao) {

    val records: LiveData<List<Record>> = recordDao.getRecords()

//    val allIncome: LiveData<List<Income>> = recordDao.getAllIncome()
//    val allExpense: LiveData<List<Expense>> = recordDao.getAllExpense()
//
//    val combinedData:LiveData<List<Any>> = MediatorLiveData<List<Any>>().apply {
//        addSource(allIncome){
//            incomes-> value = combineData(incomes,allExpense.value?: emptyList())
//        }
//        addSource(allExpense){
//            expense -> value = combineData(allIncome.value?: emptyList(), expense)
//        }
//    }
//    private fun combineData(incomes: List<Income>, expenses: List<Expense>): List<Any> {
//        return listOf(incomes, expenses).flatten()
//    }
//
//    val getAllData: LiveData<incomeExpenseCombined> = recordDao.getAllIncomeAndExpense()

//    @WorkerThread
//    suspend fun addExpense(expense: Expense){
//        expenseDao.insertExpense(expense)
//    }
//
//    @WorkerThread
//    suspend fun deleteExpense(expense: Expense){
//        expenseDao.deleteExpense(expense)
//    }
//
    @WorkerThread
    suspend fun updateRecord(record: Record){
        recordDao.updateRecord(record)
    }
}