    package my.edu.tarc.moneymate.DataSync

    import SyncWorker
    import android.content.Context
    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.navigation.fragment.findNavController
    import androidx.work.Constraints
    import androidx.work.OneTimeWorkRequestBuilder
    import androidx.work.WorkManager
    import androidx.work.WorkerParameters
    import com.google.firebase.firestore.FirebaseFirestore
    import my.edu.tarc.moneymate.Database.FirestoreHelper
    import my.edu.tarc.moneymate.databinding.FragmentDataSyncBinding

    class DataSyncFragment : Fragment() {

        private var _binding: FragmentDataSyncBinding? = null

        private val binding get() = _binding!!

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment

            _binding = FragmentDataSyncBinding.inflate(inflater, container, false)


            val navController = findNavController()
            binding.btnBackDataSync.setOnClickListener{
                navController.popBackStack()

            }

            binding.btnBackup.setOnClickListener{
                uploadDataToFirebase()
            }

            binding.btnRestore.setOnClickListener{
                restoreDataFromFirebase()
            }

            return binding.root
        }

        private fun uploadDataToFirebase() {
            val constraints = Constraints.Builder().build()

            val myWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            // Enqueue the WorkRequest to start the SyncWorker
            WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
        }

        private fun restoreDataFromFirebase(){
            val sharedPreferences = requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("userId", null)


            val firestoreHelper = FirestoreHelper(FirebaseFirestore.getInstance(), requireContext())
            if (userId != null) {
                firestoreHelper.restoreDataFromFirebase(userId)
            }
        }

    }