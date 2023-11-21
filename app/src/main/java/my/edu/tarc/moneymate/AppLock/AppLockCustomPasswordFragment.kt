package my.edu.tarc.moneymate.AppLock

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentAppLockCustPassBinding
import my.edu.tarc.moneymate.databinding.FragmentAppLockCustomPasswordBinding

class AppLockCustomPasswordFragment : Fragment() {

    private var _binding: FragmentAppLockCustomPasswordBinding? = null

    private val binding get() = _binding!!

    private var enteredPass = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppLockCustomPasswordBinding.inflate(inflater, container, false)

        binding.btnFinUnlock.setOnClickListener{
            enteredPass = binding.etUnlockPasscode.text.toString()
            if (verifyEnteredPass(enteredPass)){
                handleSuccessfulUnlock()
            } else {
                enteredPass = ""
                Toast.makeText(requireContext(), "Password entered is wrong", Toast.LENGTH_SHORT).show()
                binding.etUnlockPasscode.setText("")
            }
        }
        return binding.root
    }

    private fun verifyEnteredPass(enteredPin: String): Boolean {
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
                navController.navigate(R.id.action_appLockCustomPasswordFragment_to_profileFragment)
                Log.e("TAG1", "This working")
            }
        }
    }

}