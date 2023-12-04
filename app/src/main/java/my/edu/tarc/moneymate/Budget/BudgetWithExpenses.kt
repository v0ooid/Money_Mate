package my.edu.tarc.moneymate.Budget

import androidx.room.Embedded
import androidx.room.Relation
import my.edu.tarc.moneymate.Expense.Expense

data class BudgetWithExpenses(
    @Embedded val budget: Budget,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val expenses: List<Expense>
)
