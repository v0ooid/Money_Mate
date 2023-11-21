package my.edu.tarc.moneymate.AppLock

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import my.edu.tarc.moneymate.MainActivity
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.ActivityAppLock6Binding
import my.edu.tarc.moneymate.databinding.ActivityAppLockBinding

class AppLock6Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAppLock6Binding

    private var enteredPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        binding = ActivityAppLock6Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val digitButtons = mutableListOf<Button>()
        for (i in 0..9) {
            val buttonId = resources.getIdentifier("buttonLock6$i", "id", packageName)
            val button = findViewById<Button>(buttonId)
            digitButtons.add(button)
            button.setOnClickListener {
                onDigitClick(i)
            }
        }

        binding.iV6Back.setOnClickListener{
            onBackspaceClick()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Leave this empty to disable the back button
    }

    private fun onDigitClick(digit: Int) {
        // Append the pressed digit to the entered PIN
        if (enteredPin.length < 6) {
            enteredPin += digit
            updatePinDisplay() // Update the PIN display on the screen
        }

        if (enteredPin.length == 6) {
            if (verifyEnteredPin(enteredPin)){
                handleSuccessfulUnlock()
            } else {
                enteredPin = ""
                Toast.makeText(this, "Passcode entered is wrong", Toast.LENGTH_SHORT).show()
                updatePinDisplay()
            }
        }
    }

    private fun onBackspaceClick(){
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1) // Remove the last character from the entered PIN
            updatePinDisplay() // Update the PIN display after backspace
        }
    }

    private fun updatePinDisplay() {
        val dotViews = listOf(
            binding.dot4Digit61,
            binding.dot4Digit62,
            binding.dot4Digit63,
            binding.dot4Digit64,
            binding.dot4Digit65,
            binding.dot4Digit66
            )

        for (i in 0 until dotViews.size) {
            if (i < enteredPin.length) {
                dotViews[i].setBackgroundResource(R.drawable.bd_view_white_oval)
            } else {
                dotViews[i].setBackgroundResource(R.drawable.bd_view_grey_oval)
            }
        }
    }

    private fun verifyEnteredPin(enteredPin: String): Boolean {
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