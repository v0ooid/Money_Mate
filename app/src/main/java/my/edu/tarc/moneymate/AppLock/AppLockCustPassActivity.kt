package my.edu.tarc.moneymate.AppLock

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import my.edu.tarc.moneymate.MainActivity
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.ActivityAppLockCustPassBinding
import my.edu.tarc.moneymate.databinding.FragmentAppLockSetupCusPassBinding

class AppLockCustPassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppLockCustPassBinding

    private var enteredPass = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        binding = ActivityAppLockCustPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFinUnlock.setOnClickListener{
            enteredPass = binding.etUnlockPasscode.text.toString()
            if (verifyEnteredPass(enteredPass)){
                handleSuccessfulUnlock()
            } else {
                enteredPass = ""
                Toast.makeText(this, "Password entered is wrong", Toast.LENGTH_SHORT).show()
                binding.etUnlockPasscode.setText("")
            }
        }
    }

    private fun verifyEnteredPass(enteredPin: String): Boolean {
        val sharedPreferences = getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)
        val storedPIN = sharedPreferences.getString("PIN_KEY", "")

        return enteredPin == storedPIN
    }

    private fun handleSuccessfulUnlock() {
        // Perform any necessary actions upon successful unlock (e.g., show the main activity)
        val sharedPreferences = getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("APP_LOCK_UNLOCKED", true).apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Finish the AppLockActivity to prevent returning to it using the back button
    }
}