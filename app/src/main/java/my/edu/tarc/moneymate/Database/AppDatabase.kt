package my.edu.tarc.moneymate.Database

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Alarm.AlarmNotification
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.ListStringConverter
import my.edu.tarc.moneymate.Record.Record
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.R
import java.util.Calendar
import java.util.Date

@Database(entities = [MonetaryAccount::class, Budget::class, Income::class, Category::class, Expense::class, Record::class, AlarmNotification::class, Goal::class] , version = 2, exportSchema = false)
@TypeConverters(ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun monetaryAccountDao(): MonetaryAccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao():ExpenseDao
    abstract fun recordDao():RecordDao
    abstract fun alarmNotificationDao():AlarmNotificationDao
    abstract fun GoalDao():GoalDao
    abstract fun reportDao():ReportDao
    private class DatabaseCallback(private val scope: CoroutineScope,private val context: Context) : RoomDatabase.Callback() {
        @SuppressLint("DiscouragedApi")
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Insert initial categories here
            scope.launch {
                val categoryDao = INSTANCE?.categoryDao()
                val monetaryAccountDao = INSTANCE?.monetaryAccountDao()
                val incomeDao = INSTANCE?.incomeDao()
                val expenseDao = INSTANCE?.expenseDao()

                monetaryAccountDao?.insertAccount(MonetaryAccount(1, "Cash", 1000.0,context.resources.getIdentifier("round_money_24", "drawable", context.packageName)))
                categoryDao?.insertCategory(Category(0, "Salary", context.resources.getIdentifier("baseline_description_24", "drawable", context.packageName), "income"))
                categoryDao?.insertCategory(Category(1, "Payment",context.resources.getIdentifier("round_auto_awesome_24", "drawable", context.packageName), "income"))
                categoryDao?.insertCategory(Category(2, "Invest",context.resources.getIdentifier("round_line_axis_24", "drawable", context.packageName), "income"))
                categoryDao?.insertCategory(Category(3, "Pay", context.resources.getIdentifier("round_money_24", "drawable", context.packageName), "income"))
                categoryDao?.insertCategory(Category(4, "Allowance", context.resources.getIdentifier("round_attach_money_24", "drawable", context.packageName), "income"))

                categoryDao?.insertCategory(Category(5, "Bills", context.resources.getIdentifier("round_electric_bolt_24", "drawable", context.packageName), "expense"))
                categoryDao?.insertCategory(Category(6, "Installment",context.resources.getIdentifier("round_tv_24", "drawable", context.packageName), "expense"))
                categoryDao?.insertCategory(Category(7, "Sales", context.resources.getIdentifier("round_point_of_sale_24", "drawable", context.packageName), "expense"))
                categoryDao?.insertCategory(Category(8, "Drink",context.resources.getIdentifier("round_local_drink_24", "drawable", context.packageName), "expense"))
                categoryDao?.insertCategory(Category(9, "Food", context.resources.getIdentifier("round_free_breakfast_24", "drawable", context.packageName), "expense"))



                incomeDao?.insertIncome(Income(1, "Salary", context.resources.getIdentifier("round_auto_awesome_24", "drawable", context.packageName), "July Salary",100,1,1,"2023-10-16 11:55"))
                incomeDao?.insertIncome(Income(2, "Investment", context.resources.getIdentifier("round_line_axis_24", "drawable", context.packageName), "July Investment",200,2,1,"2023-11-16 11:55"))
                incomeDao?.insertIncome(Income(3, "Payback", context.resources.getIdentifier("round_money_24", "drawable", context.packageName), "July Payback",300,3,1,"2023-09-16 11:55"))
                incomeDao?.insertIncome(Income(4, "Allowance",context.resources.getIdentifier("round_attach_money_24", "drawable", context.packageName), "July Allowance",400,4,1,"2023-01-16 11:55"))

                expenseDao?.insertExpense(Expense(1, "Pay", context.resources.getIdentifier("round_electric_bolt_24", "drawable", context.packageName), "July Pay",400,5,1,"2023-12-16 11:55"))
                expenseDao?.insertExpense(Expense(2, "Installment",context.resources.getIdentifier("round_tv_24", "drawable", context.packageName), "July Installment",300,6,1,"2023-10-16 11:55"))
                expenseDao?.insertExpense(Expense(3, "Drink", context.resources.getIdentifier("round_point_of_sale_24", "drawable", context.packageName), "July Drink",200,7,1,"2023-11-16 11:55"))
                expenseDao?.insertExpense(Expense(4, "Food", context.resources.getIdentifier("round_local_drink_24", "drawable", context.packageName), "July Food",100,8,1,"2023-12-16 11:55"))


            }
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
                    .addCallback(DatabaseCallback(GlobalScope,context))
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
