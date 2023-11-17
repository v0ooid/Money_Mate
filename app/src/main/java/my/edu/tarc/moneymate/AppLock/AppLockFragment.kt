package my.edu.tarc.moneymate.AppLock

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentAppLockBinding

class AppLockFragment : Fragment() {

    private lateinit var viewModel: AppLockViewModel

    private var _binding: FragmentAppLockBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAppLockBinding.inflate(inflater, container,false)

        if (isAppLockEnabled()) {
            // App lock is enabled, proceed with the regular functionality
        } else {
            // App lock is not enabled, prompt the user to set it up
            // Show a dialog or navigate to a setup screen to allow the user to enable/set up the app lock
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AppLockViewModel::class.java)
    }

    fun isAppLockEnabled(): Boolean {
        val sharedPreferences = context?.getSharedPreferences("AppLockPreferences", Context.MODE_PRIVATE)
        return sharedPreferences?.getBoolean("isAppLockEnabled", false) ?: false
    }

}