package my.edu.tarc.moneymate.AppLock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import my.edu.tarc.moneymate.MainActivity
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.ActivityAppLockBinding

class AppLock4Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAppLockBinding

    private var enteredPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        binding = ActivityAppLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val digitButtons = mutableListOf<Button>()
        for (i in 0..9) {
            val buttonId = resources.getIdentifier("buttonLock$i", "id", packageName)
            val button = findViewById<Button>(buttonId)
            digitButtons.add(button)
            button.setOnClickListener {
                onDigitClick(i)
            }
        }

        binding.iVBack.setOnClickListener {
            onBackspaceClick()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Leave this empty to disable the back button
    }

    private fun onDigitClick(digit: Int) {
        // Append the pressed digit to the entered PIN
        if (enteredPin.length < 4) {
            enteredPin += digit
            updatePinDisplay() // Update the PIN display on the screen
        }

        if (enteredPin.length == 4) {
            if (verifyEnteredPin(enteredPin)) {

                handleSuccessfulUnlock()

            } else {
                enteredPin = ""
                Toast.makeText(this, "Passcode entered is wrong", Toast.LENGTH_SHORT).show()
                updatePinDisplay()
            }
        }
    }

    private fun onBackspaceClick() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1) // Remove the last character from the entered PIN
            updatePinDisplay() // Update the PIN display after backspace
        }
    }

    private fun updatePinDisplay() {
        val dotViews = listOf(
            binding.dot1,
            binding.dot2,
            binding.dot3,
            binding.dot4
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

        val sharedPreferences = getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("APP_LOCK_UNLOCKED", true).apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }
}