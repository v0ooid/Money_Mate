package my.edu.tarc.moneymate.Database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import my.edu.tarc.moneymate.Category.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    val getAllCategory: LiveData<List<Category>> = categoryDao.getAllCategory()

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
}