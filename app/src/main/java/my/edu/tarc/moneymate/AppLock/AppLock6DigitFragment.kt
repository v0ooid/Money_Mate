package my.edu.tarc.moneymate.AppLock

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.ActivityAppLock6Binding


class AppLock6DigitFragment : Fragment() {

    private var _binding: ActivityAppLock6Binding? = null

    private val binding get() = _binding!!

    private var enteredPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityAppLock6Binding.inflate(inflater, container, false)

        val digitButtons = mutableListOf<Button>()
        for (i in 0..9) {
            val buttonId = resources.getIdentifier("buttonLock6$i", "id", requireContext().packageName)
            val button = binding.root.findViewById<Button>(buttonId)
            digitButtons.add(button)
            button.setOnClickListener {
                onDigitClick(i)
            }
        }
        binding.iV6Back.setOnClickListener {
            onBackspaceClick()
        }

        return binding.root
    }

    private fun onDigitClick(digit: Int) {
        // Append the pressed digit to the entered PIN
        if (enteredPin.length < 6) {
            enteredPin += digit
            updatePinDisplay() // Update the PIN display on the screen
        }

        if (enteredPin.length == 6) {
            if (verifyEnteredPin(enteredPin)) {

                handleSuccessfulUnlock()

            } else {
                enteredPin = ""
                Toast.makeText(requireContext(), "Passcode entered is wrong", Toast.LENGTH_SHORT)
                    .show()
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
        val sharedPreferences = requireContext().getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)
        val storedPIN = sharedPreferences.getString("PIN_KEY", "")

        return enteredPin == storedPIN
    }

    private fun handleSuccessfulUnlock() {
        val fragmentLock = requireArguments().getString("FRAGMENT_LOCK")

        Log.e("arg", fragmentLock.toString())

        when (fragmentLock){
            "PROFILE_FRAGMENT" -> {
                val sharedPreferences = requireContext().getSharedPreferences("APP_PROFILE_PREFS", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("Locked", false).apply()
                val navController = findNavController()
                navController.navigate(R.id.action_appLock6DigitFragment_to_profileFragment)
                Log.e("TAG1", "This working")
            }
        }
    }
}