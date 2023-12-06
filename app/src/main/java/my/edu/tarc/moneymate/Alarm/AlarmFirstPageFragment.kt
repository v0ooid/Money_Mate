package my.edu.tarc.moneymate.Alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentAlarmFirstPageBinding

class AlarmFirstPageFragment : Fragment() {
    private var _binding: FragmentAlarmFirstPageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentAlarmFirstPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.leftIcon.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.btnGoalAlarm.setOnClickListener{
            findNavController().navigate(R.id.action_alarmFirstPageFragment_to_goalAlarmFragment)
        }
        binding.btnAlarmNotification.setOnClickListener{
            findNavController().navigate(R.id.action_alarmFirstPageFragment_to_alarmNotificationFragment)
        }
    }


}