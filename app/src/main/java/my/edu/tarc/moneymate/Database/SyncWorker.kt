import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.FirestoreHelper
class SyncWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
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

            // Upload data to Firestore
            firestoreHelper.uploadDataToFirestore(userId, monetaryAccountData, "MonetaryAccount")
            firestoreHelper.uploadDataToFirestore(userId, categoryData, "Category")
            firestoreHelper.uploadDataToFirestore(userId, budgetData, "Budget")
            firestoreHelper.uploadDataToFirestore(userId, incomeData, "Income")
            firestoreHelper.uploadDataToFirestore(userId, expenseData, "Expense")
        }

        return Result.success()
    }


    fun getUserId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)
    }
}