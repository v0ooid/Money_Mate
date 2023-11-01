package my.edu.tarc.moneymate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import my.edu.tarc.moneymate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Check login state
        if (!isLoggedIn()) {
            // User is not logged in, navigate to the login screen
            navigateToLogin()
        } else {
            // User is logged in, proceed to the main content
            // You may load the main content or navigate to the appropriate fragment here
        }
    }

    private fun isLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", "")
        return userId?.isNotEmpty() ?: false
    }

    private fun navigateToLogin() {
        // Start your LoginActivity or navigate to the login screen here
        // Example: Launch a new LoginActivity
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish() // Optional: Finish the MainActivity to prevent going back to it after logging in
    }
}