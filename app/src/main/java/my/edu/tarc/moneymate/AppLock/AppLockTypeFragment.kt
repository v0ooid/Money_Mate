package my.edu.tarc.moneymate.AppLock

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.moneymate.R

class AppLockTypeFragment : Fragment() {

    companion object {
        fun newInstance() = AppLockTypeFragment()
    }

    private lateinit var viewModel: AppLockTypeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_lock_type, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AppLockTypeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}