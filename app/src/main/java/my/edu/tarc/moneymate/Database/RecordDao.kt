package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Record.Record

//data class incomeExpenseCombined(
//    val incomeData : List<Income>,
//    val expenseData: List<Expense>
//)
@Dao
interface RecordDao {
//    @Query("SELECT * FROM Income")
//    fun getAllIncome():LiveData<List<Income>>
//    @Query("SELECT * FROM Expense")
//    fun getAllExpense():LiveData<List<Expense>>
//    @Query("SELECT * FROM Income")
//    fun getAllIncomeAndExpense(): LiveData<incomeExpenseCombined>

    @Query("SELECT incomeId as id, incomeTitle as title,image,description,amount, categoryId,accountId,'income' AS type,date " +
            "FROM income UNION SELECT expenseId as id, expense_title as title,expense_icon_image,description,amount, categoryId,accountId, 'expense' AS type,date " +
            "FROM expense")
    fun getRecords(): LiveData<List<Record>>

    @Update
    suspend fun updateRecord(record: Record)

}