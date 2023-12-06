package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Record.Record
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RecordRepository(private val recordDao: RecordDao) {

    val records: LiveData<List<Record>> = recordDao.getRecords()

    fun getTransactionsForMonth(date: Date): LiveData<List<Record>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = date

        // Setting to the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = dateFormat.format(calendar.time)

        // Setting to the last day of the month
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endDate = dateFormat.format(calendar.time)

        return recordDao.getRecordsForMonth(startDate, endDate)
    }
    @WorkerThread
    suspend fun updateRecord(record: Record){
        recordDao.updateRecord(record)
    }


    fun getAccountNameForRecord(accountId: Long): LiveData<String?> {
        return recordDao.getAccountNameForRecord(accountId)
    }
    fun getRecordsForMonth(startDate: String, endDate: String): LiveData<List<Record>> {
        return recordDao.getRecordsForMonth(startDate, endDate)
    }
}