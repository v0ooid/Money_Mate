package my.edu.tarc.moneymate.Budget

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.common.math.DoubleMath

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val budgetId: Long = 0,
    val budgetName: String,
    val budgetLimit: Double,
    val budgetSpent: Double,
    val budgetIcon: String
)