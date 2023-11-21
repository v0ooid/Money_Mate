package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income

class ExpenseRepository (private val expenseDao: ExpenseDao) {
    val getAllExpense: LiveData<MutableList<Expense>> = expenseDao.getAllExpense()
    @WorkerThread
    suspend fun addExpense(expense: Expense){
        expenseDao.insertExpense(expense)
    }

    @WorkerThread
    suspend fun deleteExpense(expense: Expense){
        expenseDao.deleteExpense(expense)
    }

    @WorkerThread
    suspend fun updateExpense(expense: Expense){
        expenseDao.updateExpense(expense)
    }

}