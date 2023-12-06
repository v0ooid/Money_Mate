package my.edu.tarc.moneymate.Forum

import com.google.firebase.Timestamp
data class Comment(
    var id: String = "",
    var threadId: String = "",
    var authorId: String = "",
    var authorName: String = "", // Add this field to store the user's name
    var content: String = "",
    var timestamp: Timestamp? = null
)