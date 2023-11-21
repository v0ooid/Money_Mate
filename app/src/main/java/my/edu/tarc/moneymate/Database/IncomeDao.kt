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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncome(income: Income)

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)
}