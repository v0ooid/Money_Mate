package my.edu.tarc.moneymate.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount

@Database(entities = [MonetaryAccount::class, Budget::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun monetaryAccountDao(): MonetaryAccountDao
    abstract fun budgetDao(): BudgetDao

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
                ).build()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}