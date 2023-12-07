package my.edu.tarc.moneymate.Forum

import CommentAdapter
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import my.edu.tarc.moneymate.AppLock.AppLock4DigitFragment
import my.edu.tarc.moneymate.AppLock.AppLock6DigitFragment
import my.edu.tarc.moneymate.AppLock.AppLockCustomPasswordFragment
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Report.ReportViewModel
import my.edu.tarc.moneymate.databinding.FragmentForumBinding

class ForumFragment : Fragment(),ForumThreadAdapter.CommentPostListener {

    companion object {
        fun newInstance() = ForumFragment()
    }

    private lateinit var reportViewModel: ReportViewModel
    private lateinit var firestore: FirebaseFirestore
    private lateinit var threadAdapter: ForumThreadAdapter
    private val threadsList = mutableListOf<ForumThread>()
    private lateinit var viewModel: ForumViewModel
    private var _binding: FragmentForumBinding? = null
    private lateinit var commentAdapter: CommentAdapter
    private val commentsList = mutableListOf<Comment>()
    private val binding get() = _binding!!
    private var currentThreadId: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString("userId", "")

        val sharedPreferences =
            requireContext().getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)

        val sectionPreferences =
            requireContext().getSharedPreferences("APP_FORUM_PREFS", Context.MODE_PRIVATE)

        val lockEn = sectionPreferences.getBoolean("Enabled", false)

        val lockStatus = sectionPreferences.getBoolean("Locked", false)

        Log.e("lockEn", lockEn.toString())
        Log.e("lockStatus", lockStatus.toString())


        if (lockEn){
            if (lockStatus) {
                val appLockType = sharedPreferences.getString("SECURITY_TYPE_KEY", "")
                if (appLockType == "4Digit") {
                    val fragment = AppLock4DigitFragment()
                    val bundle = Bundle()
                    bundle.putString("FRAGMENT_LOCK", "FORUM_FRAGMENT")
                    fragment.arguments = bundle

                    val navController = findNavController()
                    navController.navigate(
                        R.id.action_forumFragment_to_appLock4DigitFragment,
                        bundle
                    )
                } else if (appLockType == "6Digit") {
                    val fragment = AppLock6DigitFragment()
                    val bundle = Bundle()
                    bundle.putString("FRAGMENT_LOCK", "FORUM_FRAGMENT")
                    fragment.arguments = bundle

                    val navController = findNavController()
                    navController.navigate(
                        R.id.action_forumFragment_to_appLock6DigitFragment,
                        bundle
                    )
                } else {
                    val fragment = AppLockCustomPasswordFragment()
                    val bundle = Bundle()
                    bundle.putString("FRAGMENT_LOCK", "FORUM_FRAGMENT")
                    fragment.arguments = bundle

                    val navController = findNavController()
                    navController.navigate(
                        R.id.action_forumFragment_to_appLockCustomPasswordFragment,
                        bundle
                    )
                }
            } else{
                runRemainingForumFragmentLogic()
            }
        } else {
            runRemainingForumFragmentLogic()
        }
    }

    private fun runRemainingForumFragmentLogic(){
        setupSharedPreferences()
        setupRecyclerView()
        fetchThreads()

        binding.addPostButton.setOnClickListener {
            showAddPostDialog()
        }
    }

    private fun setupSharedPreferences() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString("userId", "")
    }

    private fun setupRecyclerView() {
        threadAdapter = ForumThreadAdapter(threadsList, currentUserId ?: "", this,requireContext())
        binding.rvForumPost.adapter = threadAdapter
        binding.rvForumPost.layoutManager = LinearLayoutManager(context)
    }


    private fun fetchThreads() {
        FirebaseFirestore.getInstance().collection("threads")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                threadsList.clear()
                for (document in documents) {
                    val thread = document.toObject(ForumThread::class.java).apply { id = document.id }
                    threadsList.add(thread)
                }
                threadAdapter.updateThreads(threadsList)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents: ", e)
            }
    }



    private fun createThread(thread: ForumThread, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance().collection("threads")

        threadsList.add(thread)
        val threadMap = hashMapOf(
            "title" to thread.title,
            "content" to thread.content,
            "authorId" to thread.authorId,
            "timestamp" to FieldValue.serverTimestamp() // Server timestamp
        )

        db.add(threadMap)
            .addOnSuccessListener { documentReference ->
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    private fun showAddPostDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.forum_add_post, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()
        val sharedPreferences = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val accountId = sharedPreferences.getString("userId","")
        val currentDateTime =
        dialogView.findViewById<AppCompatButton>(R.id.postThreadButton).setOnClickListener {
            val title = dialogView.findViewById<EditText>(R.id.etThreadTitle).text.toString().trim()
            val content = dialogView.findViewById<EditText>(R.id.etThreadContent).text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                // Assume 'authorId' is obtained from the authenticated user
                val newThread = ForumThread(title = title, content = content, authorId = accountId.toString())
                createThread(newThread,
                    onSuccess = {
                        dialogBuilder.dismiss()
                        fetchThreads() // Refresh the threads list
                    },
                    onFailure = { exception ->
                        // Handle failure, e.g., show error message
                    }
                )
            } else {
                Toast.makeText(context, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBuilder.show()
    }

    //Comment


    override fun onCommentPost(threadId: String, comment: String) {
         postUserComment(threadId, comment)

    }
    private fun postUserComment(threadId: String, comment: String) {
        // Prepare the comment data
        val commentData = hashMapOf(
            "threadId" to threadId,
            "authorId" to currentUserId,
            "content" to comment,
            "timestamp" to FieldValue.serverTimestamp()
        )
        Log.d("inside fragment", commentData.toString())

        FirebaseFirestore.getInstance().collection("comments")
            .add(commentData)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->
                // Handle failure (e.g., show an error message)
            }
    }

}


