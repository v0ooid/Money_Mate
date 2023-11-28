package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.DataExport.IncomeWithAccountName
import my.edu.tarc.moneymate.Income.Income
import java.util.Date

@Dao
interface IncomeDao {

    @Query("DELETE FROM INCOME")
    suspend fun deleteAll()

    @Query("SELECT * FROM INCOME")
    fun getAllIncome(): LiveData<MutableList<Income>>
    @Query("SELECT * FROM INCOME")
    fun getAllIncomeRecord(): LiveData<List<Income>>

    @Query("SELECT * FROM Income WHERE accountId = :accountId")
    fun getIncomesForAccount(accountId: Long): LiveData<List<Income>>

    @Query("SELECT * FROM Income WHERE date BETWEEN :startDate AND :endDate")
    fun getIncomeInRange(startDate: String, endDate: String): LiveData<List<Income>>

    @Query("SELECT * FROM Income WHERE strftime('%Y-%m', date) = :yearMonth")
    suspend fun getIncomeForMonth(yearMonth: String): List<Income>

//
//    fun getAllData(): LiveData<MutableList<Income>>

    @Query("SELECT * FROM INCOME")
    fun getIncomeSync(): List<Income>

    @Query("SELECT Income.*" +
            "FROM Income " +
            "INNER JOIN Category ON Income.categoryId = Category.categoryId " +
            "INNER JOIN monetary_accounts ON Income.accountId = monetary_accounts.accountId " +
            "WHERE Income.accountId = :accountId " +
            "AND Income.categoryId = :categoryId ")
    fun getIncomeByCriteria(accountId: Long, categoryId: Long): List<Income>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<Income>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncome(income: Income)

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

}