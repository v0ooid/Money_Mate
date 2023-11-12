package my.edu.tarc.moneymate.Profile

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
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

        //Shared Pref
        val sharedPreferences = requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        //Firebase for show data(name and gmail)
        val db = FirebaseFirestore.getInstance()
        var docRef = db.collection("users").document(userId!!)
        docRef.get().addOnSuccessListener {
            if (it != null){
                val name = it.data?.get("fullname")?.toString()
                val email = it.data?.get("email")?.toString()

                binding.tvNameProfile.text = name
                binding.tvMailProfile.text = email
            }
        }
            .addOnFailureListener{
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }

        val firebase = Firebase.storage
        val myRef =
            firebase.reference.child("profile_image").child(userId)
        val ONE_MEGABYTE: Long = 1024 * 1024 * 1024
        myRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            binding.imageViewProfilepic.setImageBitmap(bitmap)
        }

        //Profile picture
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            ImageUri = it
            if (ImageUri.toString() != "null") {
                binding.imageViewProfilepic.setImageURI(it)
            }else
            {
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
            val myRefImage = FirebaseStorage.getInstance().getReference("profile_image/$userId")

            //Upload File to firebase Storage
            Log.e("testImageUri",ImageUri.toString())
            if (ImageUri.toString() != "null") {
                Log.e("testImageUriInside",ImageUri.toString())
                myRefImage.putFile(ImageUri!!).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Upload Done", Toast.LENGTH_SHORT).show()
                }.addOnCanceledListener {
                    Toast.makeText(requireContext(), "Upload Canceled", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.addRemoveIcon.setOnClickListener {
            val imageType = "image/*"
            imagePickerLauncher.launch(imageType)
        }

        //Dialog for edit password
        binding.cardViewEditPassword.setOnClickListener{
            showDialog()
        }

        //Monetary Account Card View
        binding.cardViewMonetaryAccount.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_profileFragment_to_monetaryAccountFragment)
        }

        //Logout
        binding.cardVLogout.setOnClickListener{
            val sharedPref = requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("userId", "")
                .apply()

            val navController = findNavController()
            navController.navigate(R.id.action_profileFragment_to_signInActivity)
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_edit_email_dialog)

        val title = dialog.findViewById<TextView>(R.id.dialogTitle)
        title.text = "Edit Password"

        val body = dialog.findViewById<TextView>(R.id.textViewEditBody)
        body.text = "An E-mail will be sent for E-mail change"

        val yesBtn = dialog.findViewById<Button>(R.id.btnConfrimDialog)
        yesBtn.setOnClickListener {
            auth = Firebase.auth

            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email

            auth.sendPasswordResetEmail(userEmail!!)

            dialog.dismiss()
        }

        val noBtn = dialog.findViewById<Button>(R.id.btnCancelDialog)
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }









}