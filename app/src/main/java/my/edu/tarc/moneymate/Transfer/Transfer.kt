package my.edu.tarc.moneymate.Transfer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount

@Entity(
    tableName = "Transfer",
    foreignKeys = [
        ForeignKey(
            entity = MonetaryAccount::class,
            parentColumns = ["accountId"],
            childColumns = ["sourceAccountId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MonetaryAccount::class,
            parentColumns = ["accountId"],
            childColumns = ["destinationAccountId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Transfer (
    @PrimaryKey(autoGenerate = true)
    val transferId: Long = 0,
    var transferTitle:String,
    var transferDescription: String,
    var transferAmount: Int,
    @ColumnInfo(index = true) // Creates an index on accountId for performance
    var sourceAccountId: Long, // Foreign key referencing the source account
    var destinationAccountId: Long, // Foreign key referencing the destination account
    var transferDate: String
)