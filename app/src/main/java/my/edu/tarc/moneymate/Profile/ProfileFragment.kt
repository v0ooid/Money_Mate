package my.edu.tarc.moneymate.Profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import my.edu.tarc.moneymate.AppLock.AppLock4DigitFragment
import my.edu.tarc.moneymate.AppLock.AppLock6DigitFragment
import my.edu.tarc.moneymate.AppLock.AppLockCustomPasswordFragment
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var ImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val sharedPreferences =
            requireContext().getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)

        val sectionPreferences =
            requireContext().getSharedPreferences("APP_PROFILE_PREFS", Context.MODE_PRIVATE)

        val lockEn = sectionPreferences.getBoolean("Enabled", false)

        val lockStatus = sectionPreferences.getBoolean("Locked", false)

        auth = Firebase.auth


        Log.e("Profile frag", lockStatus.toString())

        if (lockEn) {
            if (lockStatus) {
                val appLockType = sharedPreferences.getString("SECURITY_TYPE_KEY", "")
                if (appLockType == "4Digit") {
                    val fragment = AppLock4DigitFragment()
                    val bundle = Bundle()
                    bundle.putString("FRAGMENT_LOCK", "PROFILE_FRAGMENT")
                    fragment.arguments = bundle

                    val navController = findNavController()
                    navController.navigate(
                        R.id.action_profileFragment_to_appLock4DigitFragment,
                        bundle
                    )
                } else if (appLockType == "6Digit"){
                    val fragment = AppLock6DigitFragment()
                    val bundle = Bundle()
                    bundle.putString("FRAGMENT_LOCK", "PROFILE_FRAGMENT")
                    fragment.arguments = bundle

                    val navController = findNavController()
                    navController.navigate(
                        R.id.action_profileFragment_to_appLock6DigitFragment,
                        bundle
                    )
                } else {
                    val fragment = AppLockCustomPasswordFragment()
                    val bundle = Bundle()
                    bundle.putString("FRAGMENT_LOCK", "PROFILE_FRAGMENT")
                    fragment.arguments = bundle

                    val navController = findNavController()
                    navController.navigate(
                        R.id.action_profileFragment_to_appLockCustomPasswordFragment,
                        bundle
                    )
                }
            } else {
                runRemainingProfileFragmentLogic()
            }
        } else {
            runRemainingProfileFragmentLogic()
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("CommitPrefEdits")
    private fun runRemainingProfileFragmentLogic(){
        val sharedPreferences = requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        val gamePref = requireContext().getSharedPreferences("GamificationPref", Context.MODE_PRIVATE)

        var level = gamePref.getInt("Level", 0)

        binding.tvLevel.text = "LV $level"

        var badge = gamePref.getInt("BadgeEquiped", 0)

        if (badge != null){
            val badgeIcon= binding.ivBadgeIcon
            badgeIcon.visibility = View.VISIBLE
            badgeIcon.setImageResource(badge)
        }

        //Firebase for show data(name and gmail)
        val db = FirebaseFirestore.getInstance()
        var docRef = db.collection("users").document(userId!!)
        docRef.get().addOnSuccessListener {
            if (it != null) {
                val name = it.data?.get("fullname")?.toString()
                val email = it.data?.get("email")?.toString()

                binding.tvNameProfile.text = name
                binding.tvMailProfile.text = email
            }
        }
            .addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }

        val firebase = Firebase.storage
        val myRef =
            firebase.reference.child("profile_image").child(userId)
        val ONE_MEGABYTE: Long = 1024 * 1024 * 1024
        myRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            // Define your desired image width and height
            val reqWidth = 100 // Set your desired width
            val reqHeight = 100 // Set your desired height

            // Calculate the sample size to reduce the image size while preserving aspect ratio
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false

            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            binding.imageViewProfilepic.setImageBitmap(bitmap)
        }

        //Profile picture
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                ImageUri = it
                if (ImageUri.toString() != "null") {
                    binding.imageViewProfilepic.setImageURI(it)
                } else {
                    val firebase = Firebase.storage
                    //Create a reference to the storage
                    val myRef =
                        firebase.reference.child("profile_image").child(userId)
                    val ONE_MEGABYTE: Long = 1024 * 1024 * 1024
                    myRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        binding.imageViewProfilepic.setImageBitmap(bitmap)
                    }
                }

                //Create a reference of a storage
                //val username = share.getString("userName","").toString()
                val myRefImage =
                    FirebaseStorage.getInstance().getReference("profile_image/$userId")

                //Upload File to firebase Storage
                Log.e("testImageUri", ImageUri.toString())
                if (ImageUri.toString() != "null") {
                    Log.e("testImageUriInside", ImageUri.toString())
                    myRefImage.putFile(ImageUri!!).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Upload Done", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnCanceledListener {
                        Toast.makeText(
                            requireContext(),
                            "Upload Canceled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Upload Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        binding.addRemoveIcon.setOnClickListener {
            val imageType = "image/*"
            imagePickerLauncher.launch(imageType)
        }

        //Dialog for edit password
        binding.cardViewEditPassword.setOnClickListener {
            showDialog()
        }

        binding.cardViewBadge.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_profileFragment_to_badgeFragment)
        }

        binding.dataExportCard.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_profileFragment_to_dataExportFragment)
        }

        binding.dataBackup.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_profileFragment_to_dataSyncFragment)
        }

        //Monetary Account Card View
        binding.cardViewMonetaryAccount.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_profileFragment_to_monetaryAccountFragment)
        }

        binding.cardViewAppLock.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_profileFragment_to_appLockFragment)
        }
        binding.cardViewAlarmNotification.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_alarmFirstPageFragment)
        }

        binding.ivAlarmNotificationRight.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_alarmFirstPageFragment)
        }
        binding.cardViewGoal.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_goalFragment)
        }
        binding.cardViewReport.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_reportFragment)
        }
        binding.cardViewFinancial.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_financialAdvisorFragment2)
        }

        binding.accountTerminationCard.setOnClickListener{
            showConfirmationDialog()
        }


        //Logout
        binding.cardVLogout.setOnClickListener {
            val userPref =
                requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

            val userId = userPref.getString("userId", "")

            if (userId != null) {
                clearUserData(userId)
            }

            clearSharedPreferences()

            val navController = findNavController()
            navController.navigate(R.id.action_profileFragment_to_signInActivity)
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Account Deletion")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Delete") { _, _ ->
                // User confirmed deletion, initiate account deletion process
                deleteAccount()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                // User canceled deletion
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            // Show a progress dialog or confirmation message here if needed

            // Delete Firebase Authentication Account
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Account deleted successfully
                        // Proceed with deleting Firestore data
                        deleteFirestoreUserData(userId)
                        clearSharedPreferences()
                        navigateToSignUpActivity()
                    } else {
                        // Account deletion failed
                        // Handle failure
                    }
                }
        }
    }


    private fun deleteFirestoreUserData(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(userId)

        // Delete the user document and its subcollections (if any)
        userDocRef.delete()
            .addOnSuccessListener {
                // Firestore data deleted successfully
                // Show success message or handle accordingly
            }
            .addOnFailureListener { e ->
                // Handle failure to delete Firestore data
            }
    }

    private fun clearSharedPreferences() {
        val userPref = requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val appLockPref = requireContext().getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)
        val gamePref = requireContext().getSharedPreferences("GamificationPref", Context.MODE_PRIVATE)

        val userEditor = userPref.edit()
        val appLockEditor = appLockPref.edit()
        val gameEditor = gamePref.edit()

        userEditor.clear().apply()
        appLockEditor.clear().apply()
        gameEditor.clear().apply()
    }

    private fun navigateToSignUpActivity() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }


    private fun clearUserData(userId: String) {
        val context = requireContext()
        AppDatabase.clearDataForUser(context, userId)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_edit_email_dialog, null)
        val overlayView = layoutInflater.inflate(R.layout.dark_overlay, null)

        val overlayLayout = overlayView.findViewById<FrameLayout>(R.id.overlayLayout)

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)

        val title = dialog.findViewById<TextView>(R.id.dialogTitle)
        title.text = "Edit Password"

        val body = dialog.findViewById<TextView>(R.id.textViewEditBody)
        body.text = "An E-mail will be sent for E-mail change"


        val yesBtn = dialog.findViewById<Button>(R.id.btnConfrimDialog)
        yesBtn.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("email", "")

            if (email != null) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            Toast.makeText(
                                context,
                                "Email sent successfully to rest your password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else{
                            Toast.makeText(
                                context, "Email is not sent, Email not exist",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }

            dialog.dismiss()
            overlayLayout.visibility = View.GONE
        }

        val noBtn = dialog.findViewById<Button>(R.id.btnCancelDialog)
        noBtn.setOnClickListener {
            dialog.dismiss()
            overlayLayout.visibility = View.GONE
        }

        val parentLayout = requireActivity().findViewById<ViewGroup>(android.R.id.content)
        parentLayout.addView(overlayView)

        dialog.show()

        dialog.setOnDismissListener {
            parentLayout.removeView(overlayView)
        }
    }



}