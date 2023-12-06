import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Forum.Comment
import my.edu.tarc.moneymate.R
import java.text.SimpleDateFormat
import java.util.Locale

class CommentAdapter(private var comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forum_comment_item_layout, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val authorTextView: TextView = itemView.findViewById(R.id.tvAuthorName)
        private val commentTextView: TextView = itemView.findViewById(R.id.tvComment)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvCommentDate)

        fun bind(comment: Comment) {
            authorTextView.text = comment.authorName
            commentTextView.text = comment.content
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            dateTextView.text = comment.timestamp?.toDate()?.let { dateFormat.format(it) } ?: "Date loading..."
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateComments(newComments: List<Comment>) {
        comments = newComments
        notifyDataSetChanged()
        Log.d("comment data", newComments.toString())

    }
}
