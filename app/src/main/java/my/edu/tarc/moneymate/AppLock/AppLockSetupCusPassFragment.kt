package my.edu.tarc.moneymate.AppLock

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentAppLockSetup6DigitBinding
import my.edu.tarc.moneymate.databinding.FragmentAppLockSetupCusPassBinding

class AppLockSetupCusPassFragment : Fragment() {

    private var _binding: FragmentAppLockSetupCusPassBinding? = null
    private val binding get() = _binding!!

    private var enteredPass = ""
    private var confirmPass = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppLockSetupCusPassBinding.inflate(inflater, container, false)

        binding.btnFinSetup.setOnClickListener {
            enteredPass = binding.etSetupCusPass.text.toString()
            if (confirmPass.isEmpty()) {
                confirmPass = enteredPass
                enteredPass = ""
                binding.etSetupCusPass.setText("")
                binding.tvSet6DigitPin2.setText("Re-enter Password")
            } else {
                if (confirmPass == enteredPass) {
                    savePin(confirmPass)
                    val navController = findNavController()
                    navController.navigate(R.id.action_appLockSetupCusPassFragment_to_appLockFragment)
                } else {
                    enteredPass = ""
                    confirmPass = ""
                    binding.tvSet6DigitPin2.setText("PIN mismatch. Please try again.")
                    binding.etSetupCusPass.setText("")
                }
            }
        }
        return binding.root
    }

    private fun savePin(pin: String) {
        // Implement your logic to save the PIN to SharedPreferences or another storage mechanism
        val sharedPreferences = requireContext().getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("PIN_KEY", pin)
            .putString("SECURITY_TYPE_KEY", "CustPass")
            .putBoolean("APP_LOCK_ENABLED_KEY", true)
            .apply()

        // Move to the next screen or perform the required actions

        Toast.makeText(requireContext(), "PIN saved successfully", Toast.LENGTH_SHORT).show()

    }

}