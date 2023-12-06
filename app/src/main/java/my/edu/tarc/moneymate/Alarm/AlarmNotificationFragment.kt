package my.edu.tarc.moneymate.Alarm

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.databinding.FragmentAlarmNotificationBinding
class AlarmNotificationFragment : Fragment(), AlarmNotificationAdapter.AlarmNotificationClickListener {

    private var _binding: FragmentAlarmNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var alarmAdapter: AlarmNotificationAdapter
    private lateinit var viewModel: AlarmNotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmNotificationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[AlarmNotificationViewModel::class.java]
        alarmRecyclerView = binding.rvShowAlarm
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        alarmAdapter = AlarmNotificationAdapter(emptyList(), this)
        alarmRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.allNotification.observe(viewLifecycleOwner) { alarms ->
            alarmAdapter.updateAlarm(alarms)
        }
    }

    private fun setupListeners() {
        binding.buttonAddAlarm.setOnClickListener {
            val action = AlarmNotificationFragmentDirections
                .actionAlarmNotificationFragmentToSetAlarmFragment(0)
            findNavController().navigate(action)
        }

        binding.leftIcon.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onEditClicked(alarm: AlarmNotification) {
        val action = AlarmNotificationFragmentDirections
            .actionAlarmNotificationFragmentToSetAlarmFragment(alarm.id)
        findNavController().navigate(action)
    }

    override fun onDeleteClicked(alarm: AlarmNotification) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Alarm")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Yes") { _, _ -> viewModel.delete(alarm) }
            .setNegativeButton("No", null)
            .show()
    }
}