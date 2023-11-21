package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Income.Income

class CategoryRepository (private val categoryDao: CategoryDao){

    val getIncomeCategory: LiveData<MutableList<Category>> = categoryDao.getIncomeCategory()
    val getExpenseCategory: LiveData<MutableList<Category>> = categoryDao.getExpenseCategory()
    @WorkerThread
    suspend fun addCategory(category: Category){
        categoryDao.insertCategory(category)
    }

    @WorkerThread
    suspend fun deleteCategory(category: Category){
        categoryDao.deleteCategory(category)
    }

    @WorkerThread
    suspend fun updateCategory(category: Category){
        categoryDao.updateCategory(category)
    }
//    @WorkerThread
//    suspend fun getCategoryList():LiveData<MutableList<Category>>
//    {
//        return categoryDao.getIncomeCategory()
//    }

}