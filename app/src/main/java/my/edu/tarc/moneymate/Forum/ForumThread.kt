package my.edu.tarc.moneymate.Forum

import com.google.firebase.Timestamp

data class ForumThread(
    var id: String = "",
    var title: String = "",
    var content: String = "",
    var authorId: String = "",
    var timestamp: Timestamp? = null
)