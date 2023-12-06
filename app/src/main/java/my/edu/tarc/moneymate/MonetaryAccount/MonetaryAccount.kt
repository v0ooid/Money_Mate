package my.edu.tarc.moneymate.MonetaryAccount

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "monetary_accounts")
data class MonetaryAccount(
    @PrimaryKey(autoGenerate = true) val accountId: Long = 0,
    val accountName: String,
    var accountBalance: Double,
    val accountIcon: Int
)
{
    // Empty constructor required by Room
    constructor() : this(0, "", 0.0, 0)
}