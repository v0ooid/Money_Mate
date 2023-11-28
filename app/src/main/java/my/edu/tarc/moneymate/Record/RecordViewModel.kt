package my.edu.tarc.moneymate.Record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.ExpenseRepository
import my.edu.tarc.moneymate.Database.RecordDao
import my.edu.tarc.moneymate.Database.RecordRepository

//import my.edu.tarc.moneymate.Database.incomeExpenseCombined


class RecordViewModel(application: Application) : AndroidViewModel(application) {
    var repository: RecordRepository
    var getAllRecord: LiveData<List<Record>>

    init {
        val recordDao = AppDatabase.getDatabase(application).recordDao()
        repository = RecordRepository(recordDao)
        getAllRecord = repository.records
    }

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
//    val record:LiveData<List<Record>> = recordRepository.records

//    val combinedData: LiveData<incomeExpenseCombined> = recordDao.getAllIncomeAndExpense()
}