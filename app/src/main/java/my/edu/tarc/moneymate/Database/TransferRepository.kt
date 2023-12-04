package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.Transfer.Transfer

class TransferRepository (private val transferDao: TransferDao) {

    @WorkerThread
    suspend fun addTransfer(transfer: Transfer){
        transferDao.insertTransfer(transfer)
    }

    @WorkerThread
    suspend fun deleteTransfer(transfer: Transfer){
        transferDao.deleteTransfer(transfer)
    }



}