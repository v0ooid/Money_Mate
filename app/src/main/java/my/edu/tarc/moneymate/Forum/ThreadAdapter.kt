package my.edu.tarc.moneymate.Forum

import CommentAdapter
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import my.edu.tarc.moneymate.R
import java.text.SimpleDateFormat
import java.util.Locale

class ForumThreadAdapter(
    var threads: List<ForumThread>,
    private val currentUserId: String,
    private val commentPostListener: CommentPostListener,
    private val context: Context
) : RecyclerView.Adapter<ForumThreadAdapter.ForumThreadViewHolder>() {

    private val commentAdapter: CommentAdapter = CommentAdapter(emptyList())

    interface CommentPostListener {
        fun onCommentPost(threadId: String, comment: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumThreadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forum_post_item_layout, parent, false)
        return ForumThreadViewHolder(view, this ,commentPostListener)
    }

    override fun onBindViewHolder(holder: ForumThreadViewHolder, position: Int) {
        val thread = threads[position]
        holder.bind(thread, currentUserId)
    }

    override fun getItemCount() = threads.size

    class ForumThreadViewHolder(itemView: View, private val adapter:ForumThreadAdapter,private val commentPostListener: CommentPostListener) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.thread_title)
        private val contentTextView: TextView = itemView.findViewById(R.id.thread_content)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvThread_date)
        private val etComment: TextView = itemView.findViewById(R.id.etComment)
        private val buttonComment: Button = itemView.findViewById(R.id.buttonComment)
        private val commentRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_post_comment)
        private val commentAdapter: CommentAdapter = CommentAdapter(emptyList())
        private val ivEdit:ImageView = itemView.findViewById(R.id.thread_edit)
        private val ivDelete:ImageView = itemView.findViewById(R.id.thread_delete)
        init {
            commentRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            commentRecyclerView.adapter = commentAdapter
        }
        fun bind(thread: ForumThread, currentUserId: String) {

            titleTextView.text = thread.title
            contentTextView.text = thread.content
            dateTextView.text = thread.timestamp?.let {
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it.toDate())
            } ?: "Timestamp loading..."

            etComment.visibility = if (thread.authorId == currentUserId) View.GONE else View.VISIBLE
            buttonComment.visibility = etComment.visibility
            ivEdit.visibility = if (thread.authorId == currentUserId) View.VISIBLE else View.GONE
            ivDelete.visibility = ivEdit.visibility

            fetchComments(thread.id)

            ivEdit.setOnClickListener {
               showEditDialog(thread)
            }

            ivDelete.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context)
                builder.apply {
                    setMessage("Are you sure you want to delete this thread?")
                    setPositiveButton("OK") { dialog, _ ->
                        deleteThread(thread)
                        dialog.dismiss()
                    }
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                val dialog = builder.create()
                dialog.show()
            }
            buttonComment.setOnClickListener {
                val comment = etComment.text.toString().trim()
                if (comment.isNotEmpty()) {
                    val comment = etComment.text.toString().trim()
                    if (comment.isNotEmpty())
                    {
                        adapter.postComment(thread.id,comment,this)
                        etComment.text = ""

                    }
//                    commentPostListener.onCommentPost(thread.id, comment)
                    Log.d("comment Inside thread", comment.toString())
                    Log.d("ForumAdapter", "Binding thread: ${thread.id}, Timestamp: ${thread.timestamp}")
                    Log.d("thread inside", thread.toString())
                }

            }




        }

        private fun deleteThread(thread: ForumThread)
        {
            // Delete the thread
            FirebaseFirestore.getInstance().collection("threads")
                .document(thread.id)
                .delete()
                .addOnSuccessListener {
                    // Handle success, such as updating the adapter's dataset and notifying changes
                    adapter.threads = adapter.threads.filterNot { it.id == thread.id }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }

        private fun showEditDialog(thread: ForumThread) {
            val dialogView = LayoutInflater.from(adapter.context).inflate(R.layout.forum_add_post, null)
            val titleEditText: EditText = dialogView.findViewById(R.id.etThreadTitle)
            val contentEditText: EditText = dialogView.findViewById(R.id.etThreadContent)
            val saveButton: Button = dialogView.findViewById(R.id.postThreadButton)

            titleEditText.setText(thread.title)
            contentEditText.setText(thread.content)

            val dialogBuilder = AlertDialog.Builder(adapter.context)
                .setView(dialogView)
                .create()

            saveButton.setOnClickListener {
                val updatedTitle = titleEditText.text.toString().trim()
                val updatedContent = contentEditText.text.toString().trim()
                updateThread(thread.id, updatedTitle, updatedContent)
                dialogBuilder.dismiss()
            }

            dialogBuilder.show()
        }
        private fun updateThread(threadId: String, title: String, content: String) {
            val updatedThreadMap = mapOf(
                "title" to title,
                "content" to content
            )

            FirebaseFirestore.getInstance().collection("threads")
                .document(threadId)
                .update(updatedThreadMap)
                .addOnSuccessListener {
                    // Assuming threads is a mutable list in the adapter
                    val updatedThreadIndex = adapter.threads.indexOfFirst { it.id == threadId }
                    if(updatedThreadIndex != -1) {
                        adapter.threads[updatedThreadIndex].title = title
                        adapter.threads[updatedThreadIndex].content = content
                        adapter.notifyItemChanged(updatedThreadIndex)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(itemView.context, "Failed to update thread: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        fun fetchComments(threadId: String) {
            FirebaseFirestore.getInstance().collection("comments")
                .whereEqualTo("threadId", threadId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    val fetchedComments = documents.map { document ->
                        val comment = document.toObject(Comment::class.java).apply { id = document.id }
                        Log.d("FirestoreData", "Document Data: ${document.data}")
                        Log.d("MappedComment", "Comment: $comment")
                        Log.d("CommentData", "Comment: $comment, Timestamp: ${comment.timestamp}")
                        Log.d("test comment at fetch commetn", comment.toString())

                        fetchUserName(comment)
                    }
                     Tasks.whenAllSuccess<Comment>(fetchedComments).addOnSuccessListener { commentsWithNames ->
                        commentAdapter.updateComments(commentsWithNames)
                    }

                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error fetching comments: ${e.message}")
                }
        }




        private fun fetchUserName(comment: Comment): Task<Comment> {
            val taskCompletionSource = TaskCompletionSource<Comment>()
            FirebaseFirestore.getInstance().collection("users") // Adjust to your users collection
                .document(comment.authorId)
                .get()
                .addOnSuccessListener { userDocument ->
                    if (userDocument.exists()) {
                        comment.authorName = userDocument.getString("fullname") ?: "Unknown User" // Adjust field name as per your database
                    }
                    taskCompletionSource.setResult(comment)
                }
                .addOnFailureListener { e ->
                    taskCompletionSource.setException(e)
                }
            return taskCompletionSource.task
        }

    }


    fun updateThreads(newThreads: List<ForumThread>) {
        threads = newThreads
        notifyDataSetChanged()
    }

    fun postComment(threadId: String, comment: String,viewHolder: ForumThreadViewHolder) {
        val commentData = hashMapOf(
            "threadId" to threadId,
            "authorId" to currentUserId,
            "content" to comment,
            "timestamp" to FieldValue.serverTimestamp()
        )

        FirebaseFirestore.getInstance().collection("comments")
            .add(commentData)
            .addOnSuccessListener {
                viewHolder.fetchComments(threadId)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to post comment: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
