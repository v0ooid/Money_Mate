package my.edu.tarc.moneymate.AppLock

import android.content.Context
import android.content.Intent
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
import my.edu.tarc.moneymate.databinding.ActivityAppLockBinding

class AppLock4DigitFragment : Fragment() {

    private var _binding: ActivityAppLockBinding? = null
    private val binding get() = _binding!!

    private var enteredPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityAppLockBinding.inflate(inflater, container, false)

        val digitButtons = mutableListOf<Button>()
        for (i in 0..9) {
            val buttonId = resources.getIdentifier("buttonLock$i", "id", requireContext().packageName)
            val button = binding.root.findViewById<Button>(buttonId)
            digitButtons.add(button)
            button.setOnClickListener {
                onDigitClick(i)
            }
        }
        binding.iVBack.setOnClickListener{
            onBackspaceClick()
        }

        return binding.root
    }

    private fun onDigitClick(digit: Int) {
        // Append the pressed digit to the entered PIN
        if (enteredPin.length < 4) {
            enteredPin += digit
            updatePinDisplay() // Update the PIN display on the screen
        }

        if (enteredPin.length == 4) {
            if (verifyEnteredPin(enteredPin)){

                handleSuccessfulUnlock()

            } else {
                enteredPin = ""
                Toast.makeText(requireContext(), "Passcode entered is wrong", Toast.LENGTH_SHORT).show()
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
                navController.navigate(R.id.action_appLock4DigitFragment_to_profileFragment)
                Log.e("TAG1", "This working")
            }
            "BUDGET_FRAGMENT" -> {
                val sharedPreferences = requireContext().getSharedPreferences("APP_BUDGET_PREFS", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("Locked", false).apply()
                val navController = findNavController()
                navController.navigate(R.id.action_appLock4DigitFragment_to_budgetFragment)
                Log.e("TAG1", "This working")
            }
            "FORUM_FRAGMENT" -> {
                val sharedPreferences = requireContext().getSharedPreferences("APP_FORUM_PREFS", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("Locked", false).apply()
                val navController = findNavController()
                navController.navigate(R.id.action_appLock4DigitFragment_to_forumFragment)
                Log.e("TAG1", "This working")
            }
        }
    }

}