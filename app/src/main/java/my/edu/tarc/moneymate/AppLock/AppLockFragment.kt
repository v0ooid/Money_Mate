package my.edu.tarc.moneymate.AppLock

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.MainActivity
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentAppLockBinding

class AppLockFragment : Fragment() {

    private var _binding: FragmentAppLockBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAppLockBinding.inflate(inflater, container, false)

        binding.btnBackAppLock.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_appLockFragment_to_profileFragment2)
        }

        if (!isAppLockEnabled()) {
            showBottomSheetDialog()
        } else {
            binding.appLockSwitch.setOnCheckedChangeListener { _, isChecked ->
                updateAppLockPreference(isChecked)
            }
        }

        val sharedPreferences = requireContext().getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)

        val profilePreferences = requireContext().getSharedPreferences("APP_PROFILE_PREFS", Context.MODE_PRIVATE)
        val budgetPreferences = requireContext().getSharedPreferences("APP_BUDGET_PREFS", Context.MODE_PRIVATE)
        val forumPreferences = requireContext().getSharedPreferences("APP_FORUM_PREFS", Context.MODE_PRIVATE)

        val securityType = sharedPreferences.getString("SECURITY_TYPE_KEY", "")

        if (securityType == "4Digit") {
            binding.tvSecurityType.text = "4 Digit Unlock"
        } else if (securityType == "6Digit") {
            binding.tvSecurityType.text = "6 Digit Unlock"
        } else {
            binding.tvSecurityType.text = "Custom Password"
        }

        binding.cvSecurityType.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_appLockFragment_to_appLockTypeFragment)
        }

        binding.switchAppLockProfile.isChecked = profilePreferences.getBoolean("Enabled", false) == true
        binding.switchAppLockBudget.isChecked = budgetPreferences.getBoolean("Enabled", false) == true
        binding.switchAppLockForum.isChecked = forumPreferences.getBoolean("Enabled", false) == true


        binding.switchAppLockProfile.setOnCheckedChangeListener { _, isChecked ->
            updateAppLockSection("Profile", isChecked)
        }

        binding.switchAppLockBudget.setOnCheckedChangeListener { _, isChecked ->
            updateAppLockSection("Budget", isChecked)
        }

        binding.switchAppLockForum.setOnCheckedChangeListener { _, isChecked ->
            updateAppLockSection("Forum", isChecked)
        }

        return binding.root
    }

    private fun updateAppLockPreference(isEnabled: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences(
            "APP_LOCK_PREFS",
            Context.MODE_PRIVATE
        )

        if (!isEnabled) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to disable App Lock?")

            builder.setPositiveButton("Yes") { dialog, which ->
                sharedPreferences.edit().remove("APP_LOCK_ENABLED_KEY").apply()
                val navController = findNavController()
                navController.navigate(R.id.action_appLockFragment_to_profileFragment)
                dialog.dismiss()
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        } else {
            sharedPreferences.edit().putBoolean("APP_LOCK_ENABLED_KEY", true).apply()
        }
    }

    private fun updateAppLockSection(sectionName: String, isEnabled: Boolean) {
        val sharedPreferences = when (sectionName) {
            "Profile" -> requireContext().getSharedPreferences("APP_PROFILE_PREFS", Context.MODE_PRIVATE)
            "Budget" -> requireContext().getSharedPreferences("APP_BUDGET_PREFS", Context.MODE_PRIVATE)
            else -> requireContext().getSharedPreferences("APP_FORUM_PREFS", Context.MODE_PRIVATE)
        }

        if (sectionName == "Profile"){
            sharedPreferences.edit()
                .putBoolean("Locked", false)
                .putBoolean("Enabled", isEnabled)
                .apply()
        } else {
            sharedPreferences.edit()
                .putBoolean("Locked", false)
                .putBoolean("Enabled", isEnabled)
                .apply()
        }

    }

    fun isAppLockEnabled(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)

        return sharedPreferences?.getBoolean("APP_LOCK_ENABLED_KEY", false) ?: false
    }

    private fun showBottomSheetDialog() {
        val dialogView = layoutInflater.inflate(R.layout.bottomsheetlayout, null)
        val overlayView = layoutInflater.inflate(R.layout.dark_overlay, null)

        val overlayLayout = overlayView.findViewById<FrameLayout>(R.id.overlayLayout)

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)

        val layout4Digit = dialogView.findViewById<LinearLayout>(R.id.layout4Digit)
        val layout6Digit = dialogView.findViewById<LinearLayout>(R.id.layout6Digit)
        val layoutCustom = dialogView.findViewById<LinearLayout>(R.id.layoutCustomPassword)
        val cancel = dialogView.findViewById<TextView>(R.id.tvBottomSheetCancel)

        layout4Digit.setOnClickListener {
            dialog.dismiss()
            val navController = findNavController()
            navController.navigate(R.id.action_appLockFragment_to_appLockSetup4DigitFragment)
        }

        layout6Digit.setOnClickListener{
            dialog.dismiss()
            val navController = findNavController()
            navController.navigate(R.id.action_appLockFragment_to_appLockSetup6DigitFragment)
        }

        layoutCustom.setOnClickListener{
            dialog.dismiss()
            val navController = findNavController()
            navController.navigate(R.id.action_appLockFragment_to_appLockSetupCusPassFragment)
        }

        cancel.setOnClickListener{
            dialog.dismiss()
            val navController = findNavController()
            navController.navigate(R.id.action_appLockFragment_to_profileFragment)
        }

        val parentLayout = requireActivity().findViewById<ViewGroup>(android.R.id.content)
        parentLayout.addView(overlayView)

        dialog.show()

        dialog.setOnDismissListener {
            parentLayout.removeView(overlayView)
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

}