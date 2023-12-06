

package my.edu.tarc.moneymate.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.moneymate.Category.Category

@Dao
interface CategoryDao {

    @Query("DELETE FROM Category")
    suspend fun deleteAll()

    @Query("SELECT * FROM Category")
    fun getAllCategories(): LiveData<MutableList<Category>>

    @Query("SELECT * FROM Category")
    fun getCatSync(): List<Category>

    @Query("SELECT * FROM Category WHERE type = 'income'")
    fun getIncomeCategory(): LiveData<MutableList<Category>>

    @Query("SELECT * FROM Category WHERE type = 'expense'")
    fun getExpenseCategory(): LiveData<MutableList<Category>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<Category>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)



}