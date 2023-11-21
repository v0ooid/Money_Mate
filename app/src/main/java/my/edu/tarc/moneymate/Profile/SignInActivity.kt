package my.edu.tarc.moneymate.Profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.moneymate.MainActivity
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

        auth = Firebase.auth

        binding.tvForgotPassword.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val password = findViewById<EditText>(R.id.tvPassword_SignIn)
        val showPasswordCheckbox = findViewById<CheckBox>(R.id.checkBoxShowPassword2)

        showPasswordCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Show Password
                password.transformationMethod = null // Set transformationMethod to null
            } else {
                // Hide Password
                password.transformationMethod = PasswordTransformationMethod.getInstance() // Hide password
            }
        }

        binding.tvSignUp.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignInSignIn.setOnClickListener{
            performSignIn()
        }
    }

    private fun performSignIn(){
        val email: EditText = findViewById(R.id.tvEmail_SignIn)
        val password: EditText = findViewById(R.id.tvPassword_SignIn)

        if (email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this, "Please fill up the fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val emailInput = email.text.toString()
        val passwordInput = password.text.toString()

        auth.signInWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    saveSharePreference()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(
                        baseContext, "Success",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(baseContext, "Authentication failed. ${it.localizedMessage}",
                    Toast.LENGTH_SHORT).show()
            }

    }

    fun saveSharePreference() {
        val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val userId = auth.currentUser?.uid
        editor.putString("userId", userId)
            .apply()
    }


}