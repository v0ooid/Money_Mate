package my.edu.tarc.moneymate.AppLock

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.moneymate.R

class AppLockSettingFragment : Fragment() {

    companion object {
        fun newInstance() = AppLockSettingFragment()
    }

    private lateinit var viewModel: AppLockSettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_lock_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AppLockSettingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}