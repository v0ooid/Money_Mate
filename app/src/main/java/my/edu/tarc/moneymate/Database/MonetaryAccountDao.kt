package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount

@Dao
interface MonetaryAccountDao {

    @Query("SELECT * FROM monetary_accounts")
    fun getAllAccounts(): LiveData<List<MonetaryAccount>>

    @Insert
    suspend fun insertAccount(monetaryAccount: MonetaryAccount)

    @Update
    suspend fun updateAccount(account: MonetaryAccount)

    @Delete
    suspend fun deleteAccount(account: MonetaryAccount)


}