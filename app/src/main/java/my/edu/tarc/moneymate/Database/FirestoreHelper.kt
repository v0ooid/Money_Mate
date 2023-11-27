package my.edu.tarc.moneymate.Database

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import my.edu.tarc.moneymate.Budget.Budget
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import java.util.Date

class FirestoreHelper(private val db: FirebaseFirestore, private val context: Context) {

    fun convertMAccountToFirestoreFormat(mAccount: MonetaryAccount): Map<String, Any> {
        val dataMap = mutableMapOf<String, Any>()

        // Assuming 'MonetaryAccount' has fields like 'name', 'balance', 'currency', etc.
        dataMap["accountId"] = mAccount.accountId
        dataMap["accountName"] = mAccount.accountName
        dataMap["accountBalance"] = mAccount.accountBalance
        dataMap["accountIcon"] = mAccount.accountIcon

        return dataMap
    }

    fun convertCategoryToFirestoreFormat(category: Category): Map<String, Any> {
        val dataMap = mutableMapOf<String, Any>()

        // Assuming 'MonetaryAccount' has fields like 'name', 'balance', 'currency', etc.
        dataMap["categoryId"] = category.categoryId
        dataMap["image"] = category.image
        dataMap["title"] = category.title
        dataMap["type"] = category.type

        return dataMap
    }

    fun convertBudgetToFirestoreFormat(budget: Budget): Map<String, Any> {
        val dataMap = mutableMapOf<String, Any>()

        // Assuming 'MonetaryAccount' has fields like 'name', 'balance', 'currency', etc.
        dataMap["budgetId"] = budget.budgetId
        dataMap["budgetName"] = budget.budgetName
        dataMap["budgetLimit"] = budget.budgetLimit
        dataMap["budgetSpent"] = budget.budgetSpent
        dataMap["budgetIcon"] = budget.budgetIcon
        dataMap["categoryId"] = budget.categoryId

        return dataMap
    }

    fun convertIncomeToFirestoreFormat(income: Income): Map<String, Any> {
        val dataMap = mutableMapOf<String, Any>()

        // Assuming 'MonetaryAccount' has fields like 'name', 'balance', 'currency', etc.
        dataMap["incomeId"] = income.incomeId
        dataMap["title"] = income.title
        dataMap["description"] = income.description
        dataMap["amount"] = income.amount
        dataMap["date"] = income.date
        dataMap["image"] = income.image
        dataMap["accountId"] = income.accountId
        dataMap["categoryId"] = income.categoryId


        return dataMap
    }

    fun convertExpenseToFirestoreFormat(expense: Expense): Map<String, Any> {
        val dataMap = mutableMapOf<String, Any>()

        // Assuming 'MonetaryAccount' has fields like 'name', 'balance', 'currency', etc.
        dataMap["expenseId"] = expense.expenseId
        dataMap["expense_title"] = expense.expense_title
        dataMap["description"] = expense.description
        dataMap["amount"] = expense.amount
        dataMap["date"] = expense.date
        dataMap["expense_icon_image"] = expense.expense_icon_image
        dataMap["accountId"] = expense.accountId
        dataMap["categoryId"] = expense.categoryId

        return dataMap
    }

    fun convertToFirestoreFormat(data: Any): Map<String, Any> {
        return when (data) {
            is MonetaryAccount -> convertMAccountToFirestoreFormat(data)
            is Category -> convertCategoryToFirestoreFormat(data)
            is Budget -> convertBudgetToFirestoreFormat(data)
            is Income -> convertIncomeToFirestoreFormat(data)
            is Expense -> convertExpenseToFirestoreFormat(data)
            else -> throw IllegalArgumentException("Unsupported data type")
        }
    }

    fun uploadDataToFirestore(userId: String, dataList: List<Any>, dataType: String) {
        val batch = db.batch()

        dataList.forEachIndexed { index, data ->
            val documentRef = db.collection("users").document(userId)
                .collection("$dataType").document("$index")

            batch.set(documentRef, convertToFirestoreFormat(data))
        }

        batch.commit()
            .addOnSuccessListener {
                // Batch committed successfully
            }
            .addOnFailureListener { e ->
                // Handle batch commit failure
            }
    }

    fun restoreDataFromFirebase(userId: String){
        restoremAccountFromFirebase(userId)
        restoreCategoryFromFirebase(userId)
        restoreBudgetFromFirebase(userId)
        restoreIncomeFromFirebase(userId)
        restoreExpenseFromFirebase(userId)
    }

    fun restoreExpenseFromFirebase(userId: String) {
        val dataList = mutableListOf<Income>()

        db.collection("users")
            .document(userId)
            .collection("Expense")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data
                    println("Raw data: $data")

                    data?.let {
                        val income = Income(
                            incomeId = (it["incomeId"] as? Long) ?: 0L,
                            title = it["title"] as? String ?: "",
                            image = (it["image"] as? Int) ?: 0,
                            description = it["description"] as? String ?: "",
                            amount = (it["amount"] as? Int) ?: 0,
                            date =  it["date"] as? Date ?: Date(),
                            categoryId = (it["categoryId"] as? Long) ?: 0L,
                            accountId = (it["accountId"] as? Long) ?: 0L
                        )
                        dataList.add(income)
                    }
                }

                GlobalScope.launch {
                    AppDatabase.getDatabase(context)

                }
            }

            .addOnFailureListener { e ->
                println("Error fetching MonetaryAccount data: ${e.message}")
            }
    }

    fun restoreIncomeFromFirebase(userId: String) {
        val dataList = mutableListOf<Income>()

        db.collection("users")
            .document(userId)
            .collection("Income")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data
                    println("Raw data: $data")

                    data?.let {
                        val income = Income(
                            incomeId = (it["incomeId"] as? Long) ?: 0L,
                            title = it["title"] as? String ?: "",
                            image = (it["image"] as? Int) ?: 0,
                            description = it["description"] as? String ?: "",
                            amount = (it["amount"] as? Int) ?: 0,
                            date =  it["date"] as? Date ?: Date(),
                            categoryId = (it["categoryId"] as? Long) ?: 0L,
                            accountId = (it["accountId"] as? Long) ?: 0L
                        )
                        dataList.add(income)
                    }
                }

                GlobalScope.launch {
                    AppDatabase.getDatabase(context)

                }
            }

            .addOnFailureListener { e ->
                println("Error fetching MonetaryAccount data: ${e.message}")
            }
    }

    fun restoreBudgetFromFirebase(userId: String) {
        val dataList = mutableListOf<Budget>()

        db.collection("users")
            .document(userId)
            .collection("Budget")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data
                    println("Raw data: $data")

                    data?.let {
                        val budget = Budget(
                            budgetId = (it["budgetId"] as? Long) ?: 0L,
                            budgetName = it["budgetName"] as? String ?: "",
                            budgetLimit = (it["budgetLimit"] as? Double) ?: 0.0,
                            budgetSpent = (it["budgetSpent"] as? Double) ?: 0.0,
                            budgetIcon = (it["budgetIcon"] as? Int) ?: 0,
                            categoryId = (it["categoryId"] as? Long) ?: 0L
                        )
                        dataList.add(budget)
                    }
                }

                GlobalScope.launch {
                    AppDatabase.getDatabase(context)

                }
            }

            .addOnFailureListener { e ->
                println("Error fetching MonetaryAccount data: ${e.message}")
            }
    }

    fun restoreCategoryFromFirebase(userId: String) {
        val dataList = mutableListOf<Category>()

        db.collection("users")
            .document(userId)
            .collection("Category")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data
                    println("Raw data: $data")

                    data?.let {
                        val category = Category(

                            categoryId = (it["categoryId"] as? Long) ?: 0L ,
                            title = it["title"] as? String ?: "",
                            image = (it["image"] as? Int) ?: 0,
                            type = it["type"] as? String ?: "",
                        )
                        dataList.add(category)
                    }
                }

                GlobalScope.launch {
                    AppDatabase.getDatabase(context).categoryDao().insertAll(dataList)

                }
            }

            .addOnFailureListener { e ->
                println("Error fetching MonetaryAccount data: ${e.message}")
            }
    }

    fun restoremAccountFromFirebase(userId: String) {
        val dataList = mutableListOf<MonetaryAccount>()

        db.collection("users")
            .document(userId)
            .collection("MonetaryAccount")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data
                    println("Raw data: $data")

                    data?.let {
                        val monetaryAccount = MonetaryAccount(
                            accountId = (it["accountId"] as? Long) ?: 0L ,
                            accountName = it["accountName"] as? String ?: "",
                            accountBalance = (it["accountBalance"] as? Double) ?: 0.0,
                            accountIcon = (it["accountIcon"] as? Int) ?: 0
                        )
                        dataList.add(monetaryAccount)
                    }
                }

                GlobalScope.launch {
                    AppDatabase.getDatabase(context).monetaryAccountDao().insertAll(dataList)

                }
            }


            .addOnFailureListener { e ->
                println("Error fetching MonetaryAccount data: ${e.message}")
            }
    }


}

