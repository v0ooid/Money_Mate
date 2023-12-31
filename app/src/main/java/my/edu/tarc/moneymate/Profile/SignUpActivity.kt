package my.edu.tarc.moneymate.Profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.tvSignInSignUp.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val password = findViewById<EditText>(R.id.tvPassword_SignUp)
        val showPasswordCheckbox = findViewById<CheckBox>(R.id.checkBoxShowPassword)

        showPasswordCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Show Password
                password.transformationMethod = null // Set transformationMethod to null
            } else {
                // Hide Password
                password.transformationMethod = PasswordTransformationMethod.getInstance() // Hide password
            }
        }

        binding.btnSignUpSignUp.setOnClickListener{
            performSignUp()
        }

    }

    private fun performSignUp() {
        val email = findViewById<EditText>(R.id.tvEmail_SignUp)
        val password = findViewById<EditText>(R.id.tvPassword_SignUp)
        val fullname = findViewById<EditText>(R.id.tvName_SignUp)

        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()
        val inputName = fullname.text.toString()


        if (inputEmail.isEmpty() || inputPassword.isEmpty() || inputName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        auth.fetchSignInMethodsForEmail(inputEmail)
            .addOnSuccessListener { task ->
                val signInMethods = task?.signInMethods ?: emptyList<String>()

                if (signInMethods.isNotEmpty()) {
                    Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val currentUser = FirebaseAuth.getInstance().currentUser

                                currentUser?.sendEmailVerification()
                                    ?.addOnCompleteListener { verificationTask ->

                                        if (verificationTask.isSuccessful) {

                                            val userID = currentUser?.uid
                                            val db = FirebaseFirestore.getInstance()
                                            val usersCollection = db.collection("users")


                                            if (userID != null) {
                                                usersCollection.document(userID).set(
                                                    hashMapOf(
                                                        "fullname" to inputName,
                                                        "email" to inputEmail,
                                                        "password" to inputPassword
                                                    )
                                                )
                                            }


                                            val intent = Intent(this, SignInActivity::class.java)
                                            startActivity(intent)

                                            Toast.makeText(
                                                baseContext,
                                                "Your account is registered. Verification email sent.",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(
                                                baseContext, "Sign Up failed.",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    }

                                    ?.addOnFailureListener {
                                        Toast.makeText(
                                            this,
                                            "Error occurred ${it.localizedMessage}",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                            }

                        }.addOnFailureListener { exception ->
                            // Handle failure in fetching sign-in methods
                            Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
    }
}