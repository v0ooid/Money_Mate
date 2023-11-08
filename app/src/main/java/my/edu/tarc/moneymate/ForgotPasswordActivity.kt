package my.edu.tarc.moneymate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.moneymate.databinding.ActivityForgotPasswordBinding
import my.edu.tarc.moneymate.databinding.ActivitySignInBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.tvSignInForgotPass.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.btnSendForgotPass.setOnClickListener {
            val email: String = binding.tvEmailForgotPass.text.toString().trim() { it <= ' ' }
            if (email.isEmpty()) {
                Toast.makeText(
                    baseContext,
                    "Please Enter Email Address",
                    Toast.LENGTH_SHORT
                ).show()
            } else{
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            Toast.makeText(
                                baseContext,
                                "Email sent successfully to rest your password",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }else{
                            Toast.makeText(
                                baseContext, "Email is not sent, Email not exist",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }
    }
}
