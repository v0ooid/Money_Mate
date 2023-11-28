package my.edu.tarc.moneymate.AppLock

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentAppLockTypeBinding

class AppLockTypeFragment : Fragment() {

    private var _binding : FragmentAppLockTypeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppLockTypeBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences(
            "APP_LOCK_PREFS",
            Context.MODE_PRIVATE
        )

        val storedSecurityType = sharedPreferences.getString("SECURITY_TYPE_KEY", "")

        when (storedSecurityType){
            "4Digit" -> binding.rb4Digit.isChecked = true
            "6Digit" -> binding.rb6Digit.isChecked = true
            "CustomPassword" -> binding.rbCustomPassword.isChecked = true
        }

        binding.btnBackPasswordType.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_appLockTypeFragment_to_appLockFragment)
        }

        binding.rgPasswordType.setOnCheckedChangeListener{ group, checkedId ->
            when (checkedId){
                R.id.rb4Digit -> updateSecurityType("4Digit")
                R.id.rb6Digit -> updateSecurityType("6Digit")
                R.id.rbCustomPassword -> updateSecurityType("CustomPassword")
            }
        }

        return binding.root
    }

    private fun updateSecurityType(selectedTyp: String){
        if (selectedTyp == "4Digit"){
            val navController = findNavController()
            navController.navigate(R.id.action_appLockTypeFragment_to_appLockSetup4DigitFragment)
        } else if (selectedTyp == "6Digit"){
            val navController = findNavController()
            navController.navigate(R.id.action_appLockTypeFragment_to_appLockSetup6DigitFragment)
        } else {
            val navController = findNavController()
            navController.navigate(R.id.action_appLockTypeFragment_to_appLockSetupCusPassFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}