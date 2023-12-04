package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount

class MonetaryAccountRepository(private val monetaryAccountDao: MonetaryAccountDao) {

    val getAllAccount: LiveData<List<MonetaryAccount>> = monetaryAccountDao.getAllData()

    @WorkerThread
    suspend fun addAccount(monetaryAccount: MonetaryAccount){
        monetaryAccountDao.insertAccount(monetaryAccount)
    }

    @WorkerThread
    suspend fun deleteAccount(monetaryAccount: MonetaryAccount){
        monetaryAccountDao.deleteAccount(monetaryAccount)
    }

    @WorkerThread
    suspend fun updateAccount(monetaryAccount: MonetaryAccount){
        monetaryAccountDao.updateAccount(monetaryAccount)
    }

    fun getTotalAmount(): LiveData<Double> {
        return monetaryAccountDao.getTotalAmount()
    }

    suspend fun getAccountbyId2(accountId: String): MonetaryAccount{
        return monetaryAccountDao.getAccountbyId2(accountId)
    }
}
