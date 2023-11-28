
package my.edu.tarc.moneymate.Budget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.common.math.DoubleMath
import my.edu.tarc.moneymate.Category.Category

@Entity(
    tableName = "budgets",
    indices = [Index(value = ["categoryId"])],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Budget(
    @PrimaryKey(autoGenerate = true) val budgetId: Long = 0,
    val budgetName: String,
    val budgetLimit: Double,
    val budgetSpent: Double,
    val budgetIcon: Int,

    val  categoryId: Long
)