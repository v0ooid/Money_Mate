package my.edu.tarc.moneymate.Income


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount

@Entity(tableName = "Income",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MonetaryAccount::class,
            parentColumns = ["accountId"],
            childColumns = ["accountId"],

            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Income (
    @PrimaryKey(autoGenerate = true)
    val incomeId: Long = 0,
    var incomeTitle:String,
    var image: Int,
    var description: String,
    var amount: Int,
    var categoryId: Long,
    @ColumnInfo(index = true) // Creates an index on accountId for performance
    var accountId: Long,
    var date: String,
)