package my.edu.tarc.moneymate.DataExport

import android.accounts.Account
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.CategoryRepository
import my.edu.tarc.moneymate.Database.ExpenseRepository
import my.edu.tarc.moneymate.Database.IncomeRepository
import my.edu.tarc.moneymate.Database.MonetaryAccountRepository
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import java.util.Date


class DataExportViewModel(application: Application) : AndroidViewModel(application) {
    private var mAccountRepo :MonetaryAccountRepository
    private var categoryRepo: CategoryRepository
    private var incomeRepo: IncomeRepository // Replace with your Income Repository
    private var expenseRepo: ExpenseRepository

    var getAllMAccount: LiveData<List<MonetaryAccount>>

    private val _incomeLiveData: MutableLiveData<List<Income>> = MutableLiveData()
    val incomeLiveData: LiveData<List<Income>> = _incomeLiveData

    private val _expenseLiveData: MutableLiveData<List<Expense>> = MutableLiveData()
    val expenseLiveData: LiveData<List<Expense>> = _expenseLiveData

    init {
        val mAccountDao = AppDatabase.getDatabase(application).monetaryAccountDao()
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        val incomeDao = AppDatabase.getDatabase(application).incomeDao() // Replace this with your DAO
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()

        mAccountRepo = MonetaryAccountRepository(mAccountDao)
        categoryRepo = CategoryRepository(categoryDao)
        incomeRepo = IncomeRepository(incomeDao)
        expenseRepo = ExpenseRepository(expenseDao)// Replace this with your Income Repository

        getAllMAccount = mAccountDao.getAllData()
    }

    // Function to get account details by ID

    fun getAccountNameById(accountId: String): LiveData<MonetaryAccount> {
        val mAccountDao = AppDatabase.getDatabase(getApplication()).monetaryAccountDao()// Replace this with your DAO

        return mAccountDao.getAccountById(accountId)
    }

    // Function to fetch income data based on selected criteria
    fun fetchIncomeByCriteria(
        accountId: Long,
        categoryId: Long,
        startDate: Date,
        endDate: Date
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val incomeList = incomeRepo.getIncomeByCriteria(accountId, categoryId, startDate, endDate)
            _incomeLiveData.postValue(incomeList)
        }
    }

    fun fetchExpenseByCriteria(
        accountId: Long,
        categoryId: Long,
        startDate: Date,
        endDate: Date
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val expenseList = expenseRepo.getExpenseByCriteria(accountId, categoryId, startDate, endDate)
            _expenseLiveData.postValue(expenseList)
        }
    }

    fun getAllIncomeCategories(): LiveData<List<Category>> {
        val mutableIncomeCategories: LiveData<MutableList<Category>> = categoryRepo.getIncomeCategory
        val incomeCategories: MutableLiveData<List<Category>> = MutableLiveData()

        mutableIncomeCategories.observeForever { mutableList ->
            incomeCategories.value = mutableList.toList()
        }


        return incomeCategories
    }

    fun getAllExpensesCategories(): LiveData<List<Category>> {
        val mutableExpenseCategories: LiveData<MutableList<Category>> = categoryRepo.getExpenseCategory
        val expenseCategories: MutableLiveData<List<Category>> = MutableLiveData()

        mutableExpenseCategories.observeForever { mutableList ->
            expenseCategories.value = mutableList.toList()
        }

        return expenseCategories
    }


}