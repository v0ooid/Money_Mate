package my.edu.tarc.moneymate.AppLock

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentAppLockSetup6DigitBinding


class AppLockSetup6DigitFragment : Fragment() {

    private var _binding: FragmentAppLockSetup6DigitBinding? = null
    private val binding get() = _binding!!

    private var enteredPin = ""
    private var confirmPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppLockSetup6DigitBinding.inflate(inflater, container, false)

        binding.btnBackLockSet6.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_appLockSetup4DigitFragment_to_appLockFragment)
        }

        val digitButtons = mutableListOf<Button>()
        for (i in 0..9) {
            val buttonId =
                resources.getIdentifier("buttonLock$i", "id", requireContext().packageName)
            val button = binding.root.findViewById<Button>(buttonId)
            digitButtons.add(button)
            button.setOnClickListener {
                onDigitClick(i)
            }
        }

        binding.iVBack.setOnClickListener {
            onBackspaceClick()
        }

        return binding.root
    }

    private fun onDigitClick(digit: Int) {
        if (enteredPin.length < 6) {
            enteredPin += digit
            updatePinDisplay()

            if (enteredPin.length == 6) {
                if (confirmPin.isEmpty()) {
                    confirmPin = enteredPin
                    enteredPin = ""
                    binding.tvSet6DigitPin.setText("Re-enter PIN")
                    updatePinDisplay()
                } else {
                    if (confirmPin == enteredPin) {
                        savePin(confirmPin)
                        val navController = findNavController()
                        navController.navigate(R.id.action_appLockSetup6DigitFragment_to_appLockFragment)

                    } else {
                        // Clear entered PINs for re-entry
                        enteredPin = ""
                        confirmPin = ""
                        binding.tvSet6DigitPin.setText("PIN mismatch. Please try again.")
                        updatePinDisplay()
                    }
                }
            }
        }
    }

    private fun onBackspaceClick(){
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1) // Remove the last character from the entered PIN
            updatePinDisplay() // Update the PIN display after backspace
        } else if (confirmPin.isNotEmpty()) {
            // Clear the confirmation PIN if backspace is pressed during re-entry
            confirmPin = ""
            binding.tvSet6DigitPin.setText("Enter PIN")
            updatePinDisplay()
        }
    }

    private fun updatePinDisplay() {
        val dotViews = listOf(
            binding.dot4DigitSetup61,
            binding.dot4DigitSetup62,
            binding.dot4DigitSetup63,
            binding.dot4DigitSetup64,
            binding.dot4DigitSetup65,
            binding.dot4DigitSetup66
            )

        for (i in 0 until dotViews.size) {
            if (i < enteredPin.length) {
                dotViews[i].setBackgroundResource(R.drawable.bd_view_white_oval)

            } else {
                dotViews[i].setBackgroundResource(R.drawable.bd_view_grey_oval)
            }
        }
    }

    private fun savePin(pin: String) {
        // Implement your logic to save the PIN to SharedPreferences or another storage mechanism
        val sharedPreferences = requireContext().getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("PIN_KEY", pin)
            .putString("SECURITY_TYPE_KEY", "6Digit")
            .putBoolean("APP_LOCK_ENABLED_KEY", true)
            .apply()

        // Move to the next screen or perform the required actions

        Toast.makeText(requireContext(), "PIN saved successfully", Toast.LENGTH_SHORT).show()

    }

}