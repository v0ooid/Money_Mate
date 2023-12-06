package my.edu.tarc.moneymate.FinancialAdvisor

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.FinancialAdvisorRepository
import my.edu.tarc.moneymate.R

class FinancialAdvisorViewModel(
    application: Application,
    private val repository: FinancialAdvisorRepository,
    private val financialAnalyzer: FinancialAnalyzer
) : AndroidViewModel(application) {


    private val _accountsFinancialHealth = MutableLiveData<List<AccountFinancialHealth>>()
    val accountsFinancialHealth: LiveData<List<AccountFinancialHealth>> = _accountsFinancialHealth

    init {
        fetchAndAnalyzeFinancialData()
    }

    private fun fetchAndAnalyzeFinancialData() {
        repository.getAllMonetaryAccounts().observeForever { accounts ->
            val healthList = mutableListOf<AccountFinancialHealth>() // Create a mutable list to accumulate results
            accounts.forEach { account ->
                viewModelScope.launch {
                    repository.getIncomesForAccount(account.accountId).observeForever { incomes ->
                        Log.d("IncomeData", incomes.toString())
                        repository.getExpensesForAccount(account.accountId).observeForever { expenses ->
                            val accountHealth = financialAnalyzer.analyzeAccountFinancialHealth(account, incomes, expenses)
                            healthList.add(accountHealth) // Add the result to the list
                            _accountsFinancialHealth.postValue(healthList) // Post the entire list
                        }
                    }
                }
            }
        }
    }
    fun checkAndNotifyAccountStatus(accountId: Long) {
        val accountHealth = accountsFinancialHealth.value?.find { it.accountId == accountId }
        Log.e("Financial View Model","Outside of check status $accountHealth")
        accountHealth?.let {
            if (it.status == FinancialHealthStatus.ATTENTION || it.status == FinancialHealthStatus.DANGER) {
                showNotification("Financial Health Alert", "Your account ${it.accountName} needs attention.")
            }
            Log.e("Financial View Model", "Check and NotifyAccount Status Trigged")
        }
    }


     fun showNotification(title: String, message: String) {
        val notificationManager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("financial_health_channel", "Financial Health", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(getApplication(), "financial_health_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.round_check_24) // Replace with your notification icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FinancialAdvisorViewModel::class.java)) {
                val database = AppDatabase.getDatabase(application) // Replace with your actual database access method
                val monetaryAccountDao = database.monetaryAccountDao()
                val incomeDao = database.incomeDao()
                val expenseDao = database.expenseDao()
                @Suppress("UNCHECKED_CAST")
                return FinancialAdvisorViewModel(
                    application,
                    FinancialAdvisorRepository(monetaryAccountDao,incomeDao,expenseDao),  // Assuming default constructor, replace with actual initialization
                    FinancialAnalyzer()  // Assuming default constructor, replace with actual initialization
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
