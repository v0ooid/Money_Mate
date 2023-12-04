package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Transfer.Transfer

@Dao
interface TransferDao {

    @Query("SELECT * FROM Transfer")
    fun getAllTransferRecord(): LiveData<List<Transfer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<Transfer>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransfer(transfer: Transfer)

    @Update
    suspend fun updateTransfer(transfer: Transfer)

    @Delete
    suspend fun deleteTransfer(transfer: Transfer)

    @Query("SELECT * FROM transfer WHERE transferId = :id")
    fun getTransferById(id: Long): Transfer?

}