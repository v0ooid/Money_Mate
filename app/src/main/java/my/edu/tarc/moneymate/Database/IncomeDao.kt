package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Income.Income

@Dao
interface IncomeDao {

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncome(income: Income)

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)
}