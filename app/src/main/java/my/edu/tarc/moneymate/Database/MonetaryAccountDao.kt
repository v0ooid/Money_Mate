package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.Transfer.MonetaryAccountDetails

@Dao
interface MonetaryAccountDao {

    @Query("SELECT * FROM monetary_accounts WHERE accountId = :accountId")
    fun getAccountById(accountId: String): LiveData<MonetaryAccount>

    @Query("SELECT * FROM monetary_accounts WHERE accountId = :accountId")
    suspend fun getAccountbyId2(accountId: String): MonetaryAccount

    @Query("SELECT accountName, accountIcon FROM monetary_accounts WHERE accountId = :accountId")
    fun getAccountDetailsById(accountId: Long): LiveData<MonetaryAccountDetails>

    @Query("DELETE FROM monetary_accounts")
    suspend fun deleteAll()

    @Query("SELECT * FROM monetary_accounts")
    fun getAllData(): LiveData<List<MonetaryAccount>>

    @Query("SELECT * FROM monetary_accounts")
    fun getAllAccounts(): LiveData<List<MonetaryAccount>>

    @Query("SELECT * FROM monetary_accounts")
    fun getMAccountSync(): List<MonetaryAccount>

    @Query("SELECT * FROM monetary_accounts")
    fun getAllAccountsData(): List<MonetaryAccount>

    @Query("SELECT SUM(accountBalance) FROM monetary_accounts")
    fun getTotalAmount(): LiveData<Double>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<MonetaryAccount>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccount(monetaryAccount: MonetaryAccount)

    @Update
    suspend fun updateAccount(account: MonetaryAccount)

    @Delete
    suspend fun deleteAccount(account: MonetaryAccount)


}
