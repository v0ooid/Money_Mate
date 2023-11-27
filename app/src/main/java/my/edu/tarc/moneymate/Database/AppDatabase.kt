package my.edu.tarc.moneymate.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.R
import java.util.Calendar
import java.util.Date

@Database(entities = [MonetaryAccount::class, Budget::class, Income::class, Category::class, Expense::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun monetaryAccountDao(): MonetaryAccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao():ExpenseDao


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
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Not ideal to insert data here, consider using it for other purposes
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Perform actions when the database is opened
                            // Insert initial sample data when the database is opened
                            GlobalScope.launch(Dispatchers.IO) {
                                val database = getDatabase(context)

                                val mAccountDao = database.monetaryAccountDao()
                                val categoryDao = database.categoryDao()
                                val incomeDao = database.incomeDao()
                                val expensesDao = database.expenseDao()


//                                mAccountDao.insertAccount(MonetaryAccount(1, "Wallet", 1200.00, R.drawable.json_svgrepo_com))

//                                categoryDao.insertCategory(Category(1, "Food", R.drawable.baseline_arrow_downward_24, "Expenses"))
//                                categoryDao.insertCategory(Category(2, "Part time", R.drawable.baseline_compare_arrows_24, "Income"))
//
//                                val date = Date(2023 - 1900, Calendar.NOVEMBER, 23) // Year is based on 1900, Month starts from 0
//
//                                incomeDao.insertIncome(Income(1, "Hotel waiter job", R.drawable.json_svgrepo_com, "Shashumga", 150, date, 2, 1))
//                                incomeDao.insertIncome(Income(2, "Promoter", R.drawable.json_svgrepo_com, "Shashumga", 150, date, 2, 1))
                                // Insert more data as needed
                            }
                        }
                    })

                    .build()
                INSTANCE = newInstance
                return newInstance
            }

        }

        fun clearDataForUser(context: Context, userId: String) {
            val database = getDatabase(context)

            val monetaryAccountDao = database.monetaryAccountDao()
            val budgetDao = database.budgetDao()
            val incomeDao = database.incomeDao()
            val categoryDao = database.categoryDao()
            val expenseDao = database.expenseDao()

            // Delete data related to the provided userId from each table
            GlobalScope.launch(Dispatchers.IO) {
                incomeDao.deleteAll()
                expenseDao.deleteAll()
                budgetDao.deleteAll()
                categoryDao.deleteAll()
                monetaryAccountDao.deleteAll()
            }
        }
    }
}
