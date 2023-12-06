import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.FirestoreHelper
class SyncWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val gamePref =  context.getSharedPreferences("GamificationPref", Context.MODE_PRIVATE)

    override fun doWork(): Result {
        val firestoreHelper = FirestoreHelper(FirebaseFirestore.getInstance(), applicationContext)
        val userId = getUserId(applicationContext)

        if (userId != null) {
            val database = AppDatabase.getDatabase(applicationContext)

            // Retrieve data from DAO
            val monetaryAccountData = database.monetaryAccountDao().getMAccountSync()
            val categoryData = database.categoryDao().getCatSync()
            val budgetData = database.budgetDao().getBudgetsSync()
            val incomeData = database.incomeDao().getIncomeSync()
            val expenseData = database.expenseDao().getExpenseSync()
            val goalData = database.GoalDao().getGoalSync()


            val level = gamePref.getInt("Level", 0)
            val badgeEquipped = gamePref.getInt("BadgeEquiped",0)
            val loggedInDays = gamePref.getInt("LoggedInDays", 0)
            val recordedIncomes = gamePref.getInt("RecordedIncomes", 0)
            val recordedExpenses = gamePref.getInt("RecordedExpenses", 0)


            // Upload data to Firestore
            firestoreHelper.uploadDataToFirestore(userId, monetaryAccountData, "MonetaryAccount")
            firestoreHelper.uploadDataToFirestore(userId, categoryData, "Category")
            firestoreHelper.uploadDataToFirestore(userId, budgetData, "Budget")
            firestoreHelper.uploadDataToFirestore(userId, incomeData, "Income")
            firestoreHelper.uploadDataToFirestore(userId, expenseData, "Expense")
            firestoreHelper.uploadDataToFirestore(userId, goalData, "Goal")

            firestoreHelper.uploadGamePrefsToFirestore(userId, level, badgeEquipped, loggedInDays, recordedIncomes, recordedExpenses)

        }

        return Result.success()
    }

    fun getUserId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)
    }
}