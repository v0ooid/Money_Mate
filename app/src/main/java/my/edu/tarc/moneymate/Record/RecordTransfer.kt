package my.edu.tarc.moneymate.Record

import androidx.room.Entity
import androidx.room.PrimaryKey

data class RecordTransfer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title:String,
    var image: Int,
    var description: String,
    val amount: Int,
    var categoryId: String,
    var accountId: String,
    val type: String,
    var date: String,
)