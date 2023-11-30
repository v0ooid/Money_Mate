package my.edu.tarc.moneymate.Alarm

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TimePicker
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentAlarmNotificationBinding

class AlarmNotificationFragment : Fragment(), AlarmNotificationAdapter.AlarmNotificationClickListener {

    companion object {
        fun newInstance() = AlarmNotificationFragment()
    }
    private var _binding: FragmentAlarmNotificationBinding?= null
    private val binding get() = _binding!!

    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var alarmAdapter: AlarmNotificationAdapter
    private lateinit var viewModel: AlarmNotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmNotificationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AlarmNotificationViewModel::class.java)
        alarmRecyclerView = binding.rvShowAlarm

        val textClock = binding.clock2
        textClock.setFormat24Hour("HH:mm:ss")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Initialize alarmAdapter before calling setupRecycleView
        alarmAdapter = AlarmNotificationAdapter(emptyList(),this)
        setupRecycleView()
        observeViewModel()
        Log.d("after observe", "After lah")
        binding.buttonAddAlarm.setOnClickListener{
            checkAndRequestExactAlarmPermission(requireContext())
            checkAndRequestNotificationPermission(requireContext())
            val action = AlarmNotificationFragmentDirections
                .actionAlarmNotificationFragmentToSetAlarmFragment(0)
            findNavController().navigate(action)
        }
        binding.leftIcon.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.clock.setOnClickListener{
            if (binding.clock.visibility == View.INVISIBLE)
            {
                binding.clock.visibility = View.VISIBLE
                binding.clock2.visibility = View.INVISIBLE
            }
            else if (binding.clock.visibility == View.VISIBLE)
            {
                binding.clock.visibility = View.INVISIBLE
                binding.clock2.visibility = View.VISIBLE
            }
        }
        binding.clock2.setOnClickListener{
            if (binding.clock2.visibility == View.INVISIBLE)
            {
                binding.clock2.visibility = View.VISIBLE
                binding.clock.visibility = View.INVISIBLE
            }
            else if (binding.clock2.visibility == View.VISIBLE)
            {
                binding.clock.visibility = View.VISIBLE
                binding.clock2.visibility = View.INVISIBLE
            }
        }

//        val notificationList: List<AlarmNotification> = getNoficiationData()
    }



    private fun setupRecycleView(){
        alarmRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
        }
    }
    private fun observeViewModel() {
        viewModel.notification.observe(viewLifecycleOwner) { alarms ->
            alarmAdapter.updateAlarm(alarms)
            Log.d("testing notification", alarms.toString())
        }
        viewModel.allNotification.observe(viewLifecycleOwner){
            alarms -> alarmAdapter.updateAlarm(alarms)
            Log.d("testing Allnotification", alarms.toString())
        }
    }


    override fun onEditClicked(alarm: AlarmNotification) {
        // Open a dialog or another fragment to edit the alarm
        // Pass the 'alarm' object to the editor
        // The implementation will depend on how you want to handle editing

        val action = AlarmNotificationFragmentDirections
            .actionAlarmNotificationFragmentToSetAlarmFragment(alarm.id)
        findNavController().navigate(action)
    }
    override fun onDeleteClicked(alarm: AlarmNotification) {
        AlertDialog.Builder(context)
            .setTitle("Delete Alarm")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.delete(alarm)
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun updateAlarms(alarms: List<AlarmNotification>) {
        viewModel.updateNotification(alarms)
    }

//    private fun getAlarmNotification():List<AlarmNotification>{
//        val alarmList = mutableListOf<AlarmNotification>()
//        alarmList.add(AlarmNotification(0,10,10,"nothing","nothing", listOf("")))
//        alarmList.add(AlarmNotification(2,50,30,"nothi","nothing", listOf("")))
//        alarmList.add(AlarmNotification(4,60,30,"nothin","nothing", listOf("")))
//        return alarmList
//    }


    private fun checkAndRequestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }


    // Function to check if the app has notification permission
    fun hasNotificationPermission(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannels = notificationManager.notificationChannels
        return notificationChannels.isNotEmpty() && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    // Function to request notification permission
    fun requestNotificationPermission(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        context.startActivity(intent)
    }

    // Usage example
    fun checkAndRequestNotificationPermission(context: Context) {
        if (!hasNotificationPermission(context)) {
            // If the app does not have notification permission, request it
            requestNotificationPermission(context)
        }else{
            Log.d("NotificationPermission", "Already granted.")
        }
    }



}