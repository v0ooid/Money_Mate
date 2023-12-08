package my.edu.tarc.moneymate.Transfer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.MonetaryAccountDao
import my.edu.tarc.moneymate.Database.MonetaryAccountRepository
import my.edu.tarc.moneymate.Database.TransferDao
import my.edu.tarc.moneymate.Database.TransferRepository
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount

class TransferViewModel(application: Application) : AndroidViewModel(application) {
    private var mAccountRepo : MonetaryAccountRepository
    private var monetaryAccountDao: MonetaryAccountDao

    private var transferRepo : TransferRepository
    private var transferDDao: TransferDao

    var getAllMAccount: LiveData<List<MonetaryAccount>>

    var getAllTransfer : LiveData<List<Transfer>>

    init {
        val mAccountDao = AppDatabase.getDatabase(application).monetaryAccountDao()
        mAccountRepo = MonetaryAccountRepository(mAccountDao)

        val transferDao = AppDatabase.getDatabase(application).transferDao()
        transferRepo = TransferRepository(transferDao)

        getAllMAccount = mAccountDao.getAllData()
        getAllTransfer = transferDao.getAllTransferRecord()

        monetaryAccountDao = mAccountDao
        transferDDao = transferDao

    }

    fun getAccountNameById(accountId: Long): LiveData<MonetaryAccountDetails> {
        // Use DAO method to retrieve account name
        return monetaryAccountDao.getAccountDetailsById(accountId)
    }

    fun addTransfer(transfer: Transfer, sourceAccountId: Long, destinationAccountId: Long, transferAmount: Int) = viewModelScope.launch {
        val sourceAccount = withContext(Dispatchers.IO) {
            monetaryAccountDao.getAccountbyId2(sourceAccountId.toString())
        }
        val destinationAccount = withContext(Dispatchers.IO) {
            monetaryAccountDao.getAccountbyId2(destinationAccountId.toString())
        }

        if (sourceAccount != null && destinationAccount != null) {
            // Deduct transfer amount from the source account
            val newSourceBalance = sourceAccount.accountBalance - transferAmount

            // Add transfer amount to the destination account
            val newDestinationBalance = destinationAccount.accountBalance + transferAmount

            // Update balances in the database
            sourceAccount.accountBalance = newSourceBalance
            destinationAccount.accountBalance = newDestinationBalance

            // Update the accounts in the database
            withContext(Dispatchers.IO) {
                monetaryAccountDao.updateAccount(sourceAccount)
                monetaryAccountDao.updateAccount(destinationAccount)
            }

            // Insert the transfer record into the database
            withContext(Dispatchers.IO) {
                transferDDao.insertTransfer(transfer)
            }
        }
    }

    fun deleteTransfer(transfer: Transfer) = viewModelScope.launch {
        val existingTransfer = withContext(Dispatchers.IO) {
            transferDDao.getTransferById(transfer.transferId)
        }

        existingTransfer?.let {
            val transferAmount = existingTransfer.transferAmount

            // Delete the transfer record from the database
            transferRepo.deleteTransfer(transfer)

            // Fetch accounts involved in the transfer
            val sourceAccount: MonetaryAccount? = withContext(Dispatchers.IO) {
                monetaryAccountDao.getAccountbyId2(transfer.sourceAccountId.toString())
            }
            val destinationAccount: MonetaryAccount? = withContext(Dispatchers.IO) {
                monetaryAccountDao.getAccountbyId2(transfer.destinationAccountId.toString())
            }

            if (sourceAccount != null && destinationAccount != null) {

                Log.e("sourceAccount", sourceAccount.toString())
                Log.e("destinationAccount", destinationAccount.toString())

                sourceAccount.accountBalance += transferAmount
                destinationAccount.accountBalance -= transferAmount

                Log.e("sourceAccount", sourceAccount.toString())
                Log.e("destinationAccount", destinationAccount.toString())

                // Update account balances in the database
                monetaryAccountDao.updateAccount(sourceAccount)
                monetaryAccountDao.updateAccount(destinationAccount)
            }
        }
    }



    fun updateTransfer(transfer: Transfer) = viewModelScope.launch {
        val existingTransfer = withContext(Dispatchers.IO) {
            transferDDao.getTransferById(transfer.transferId)
        }

        if (existingTransfer != null) {
            // Calculate the differences in transfer amounts
            val amountDifference = transfer.transferAmount - existingTransfer.transferAmount

            // Update transfer details in the database
            withContext(Dispatchers.IO) {
                transferDDao.updateTransfer(transfer)
            }

            // Fetch accounts involved in the transfer
            val sourceAccount : MonetaryAccount = withContext(Dispatchers.IO) {
                monetaryAccountDao.getAccountbyId2(transfer.sourceAccountId.toString())
            }
            val destinationAccount : MonetaryAccount = withContext(Dispatchers.IO) {
                monetaryAccountDao.getAccountbyId2(transfer.destinationAccountId.toString())
            }

            // Update account balances
            if (sourceAccount != null && destinationAccount != null) {
                sourceAccount.accountBalance -= amountDifference
                destinationAccount.accountBalance += amountDifference

                // Update account balances in the database
                monetaryAccountDao.updateAccount(sourceAccount)
                monetaryAccountDao.updateAccount(destinationAccount)
            }
        }
    }





}
