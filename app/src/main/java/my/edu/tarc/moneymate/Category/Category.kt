package my.edu.tarc.moneymate.Category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category (
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    val iconResId: Int,
    val title: String
)