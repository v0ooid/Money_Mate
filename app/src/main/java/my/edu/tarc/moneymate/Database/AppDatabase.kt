package my.edu.tarc.moneymate.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.R

@Database(entities = [MonetaryAccount::class, Budget::class, Income::class, Category::class, Expense::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun monetaryAccountDao(): MonetaryAccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao():ExpenseDao


    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    prepopulateDatabase(database.categoryDao())
                }
            }
        }

        suspend fun prepopulateDatabase(categoryDao: CategoryDao) {
            // Insert your initial data for YourEntity here
            categoryDao.insertCategory(
                Category(
                    iconResId = R.drawable.baseline_fastfood_24,
                    title = "Food"
                )
            )
            categoryDao.insertCategory(
                Category(
                    iconResId = R.drawable.baseline_directions_bus_24,
                    title = "Transportation"
                )
            )
            categoryDao.insertCategory(
                Category(
                    iconResId = R.drawable.baseline_home_24,
                    title = "Housing"
                )
            )
            categoryDao.insertCategory(
                Category(
                    iconResId = R.drawable.baseline_gamepad_24,
                    title = "Entertainment"
                )
            )
            categoryDao.insertCategory(
                Category(
                    iconResId = R.drawable.baseline_health_and_safety_24,
                    title = "Health"
                )
            )
            categoryDao.insertCategory(
                Category(
                    iconResId = R.drawable.baseline_person_24,
                    title = "Personal Care"
                )
            )
            categoryDao.insertCategory(
                Category(
                    iconResId = R.drawable.baseline_collections_bookmark_24,
                    title = "Education"
                )
            )
        }
    }


    // Singleton pattern to ensure only one instance of the database is created
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "MoneyMate_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}