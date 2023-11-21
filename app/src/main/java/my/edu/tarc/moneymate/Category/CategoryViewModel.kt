package my.edu.tarc.moneymate.Category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.CategoryRepository
import kotlin.math.exp

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository
    val incomeCategory: LiveData<MutableList<Category>>
    val expenseCategory: LiveData<MutableList<Category>>
    init {
        val dao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(dao)
        incomeCategory = repository.getIncomeCategory
        expenseCategory = repository.getExpenseCategory
    }

    fun addCategoryItem(category: Category)
    {
        viewModelScope.launch (Dispatchers.IO){
            repository.addCategory(category)
        }
    }

}