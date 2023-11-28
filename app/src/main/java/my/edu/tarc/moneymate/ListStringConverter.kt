package my.edu.tarc.moneymate

import androidx.room.TypeConverter

class ListStringConverter {

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }
}