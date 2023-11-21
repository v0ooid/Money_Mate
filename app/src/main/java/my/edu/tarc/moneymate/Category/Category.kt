package my.edu.tarc.moneymate.Category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Category")
data class Category (
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    var title:String,
    var image:Int,
    var type: String
)