package my.edu.tarc.moneymate.Record

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.ExpenseRepository
import my.edu.tarc.moneymate.Database.IncomeRepository
import my.edu.tarc.moneymate.Database.MonetaryAccountDao
import my.edu.tarc.moneymate.Database.MonetaryAccountRepository
import my.edu.tarc.moneymate.Database.RecordDao
import my.edu.tarc.moneymate.Database.RecordRepository
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//import my.edu.tarc.moneymate.Database.incomeExpenseCombined


class RecordViewModel(application: Application) : AndroidViewModel(application) {




    var repository: RecordRepository
    var monetaryAccountRepository: MonetaryAccountRepository
    var getAllRecord: LiveData<List<Record>>
    val monetaryAccountDao: MonetaryAccountDao
    init {
        val recordDao = AppDatabase.getDatabase(application).recordDao()
        monetaryAccountDao = AppDatabase.getDatabase(application).monetaryAccountDao()
        monetaryAccountRepository = MonetaryAccountRepository(monetaryAccountDao)
        repository = RecordRepository(recordDao)
        getAllRecord = repository.records
    }

    val records: LiveData<List<Record>> = repository.records

    val allTitle: LiveData<List<String>> = getAllRecord.map { dataList ->
        dataList.map { data ->
            data.title
        }
    }
    val allAmount: LiveData<List<String>> = getAllRecord.map { dataList ->
        dataList.map { data ->
            data.title
        }
    }
    fun updateRecord(record: Record) = viewModelScope.launch {
        repository.updateRecord(record)
    }

    fun getAccountName(accountId: Long): LiveData<String?> {
        return liveData {
            val accountName = monetaryAccountRepository.fetchAccountName(accountId.toString())
            emit(accountName)
        }
    }
    fun getAccountNameForRecord(accountId: Long): LiveData<String?> {
        return repository.getAccountNameForRecord(accountId)
    }


}