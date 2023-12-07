package my.edu.tarc.moneymate.Alarm

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.GoalRepository
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Goal.GoalViewModel
import my.edu.tarc.moneymate.databinding.FragmentGoalAlarmBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class GoalAlarmFragment : Fragment() {
    companion object {
        const val ACTION_GOAL_REMINDER = "my.edu.tarc.moneymate.ACTION_GOAL_REMINDER"
    }

    private var _binding: FragmentGoalAlarmBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GoalViewModel
    private lateinit var goalsAdapter: GoalAlarmAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvGoalAlarmList
        setupRecyclerView()

        val goalDao = AppDatabase.getDatabase(requireActivity().application).GoalDao()
        val repository = GoalRepository(goalDao)
        val factory = GoalViewModel.GoalViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[GoalViewModel::class.java]

        viewModel.allGoals.observe(viewLifecycleOwner) { goals ->
            goalsAdapter.updateGoals(goals)
        }

        binding.leftIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        goalsAdapter = GoalAlarmAdapter(emptyList()) { goal ->
            showAlarmSettingDialog(goal)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = goalsAdapter
    }

    private fun showAlarmSettingDialog(goal: Goal) {
        val options = arrayOf("Every Day", "Every Week", "Every Month")
        AlertDialog.Builder(requireContext())
            .setTitle("Set Reminder for ${goal.title}")
            .setItems(options) { _, which ->
                val frequency = when (which) {
                    0 -> AlarmFrequency.DAILY
                    1 -> AlarmFrequency.WEEKLY
                    2 -> AlarmFrequency.MONTHLY
                    else -> AlarmFrequency.DAILY
                }
                scheduleAlarm(goal, frequency)
                Toast.makeText(requireContext(), "Goal Alarm will remind you around 8am", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun scheduleAlarm(goal: Goal, frequency: AlarmFrequency) {
        val context = context ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            requestExactAlarmPermission()
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_GOAL_REMINDER
            putExtra("Channel_ID","Goal_Alarm_Channel_ID")
            putExtra("Title", goal.title)
            putExtra("Goal Desc", goal.description)
            putExtra("goalId", goal.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            goal.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextAlarmTime = getNextAlarmTime(frequency)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android M and above, use setExactAndAllowWhileIdle to allow alarms to fire even in Doze mode
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime,
                pendingIntent
            )
        } else {
            // For older versions, use setExact for precise timing
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime,
                pendingIntent
            )
        }
    }

    private fun requestExactAlarmPermission() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        startActivity(intent)
    }

    private fun getNextAlarmTime(frequency: AlarmFrequency): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur")).apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            Log.d("Goal Alarm Fragment", "getNextAlarmTime $timeInMillis")
            when (frequency) {
                AlarmFrequency.DAILY -> add(Calendar.DAY_OF_YEAR, 1)
                AlarmFrequency.WEEKLY -> add(Calendar.DAY_OF_YEAR, 7)
                AlarmFrequency.MONTHLY -> add(Calendar.MONTH, 1)
            }

            if (timeInMillis <= System.currentTimeMillis()) {
                when (frequency) {
                    AlarmFrequency.DAILY -> add(Calendar.DAY_OF_YEAR, 1)
                    AlarmFrequency.WEEKLY -> add(Calendar.DAY_OF_YEAR, 7)
                    AlarmFrequency.MONTHLY -> add(Calendar.MONTH, 1)
                }
            }
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val alarmTime = dateFormat.format(Date(calendar.timeInMillis))
        Log.d("GoalAlarmFragment", "Alarm scheduled for: $alarmTime")

        return calendar.timeInMillis
    }

    private fun getIntervalMillis(frequency: AlarmFrequency): Long {
        return when (frequency) {
            AlarmFrequency.DAILY -> AlarmManager.INTERVAL_DAY
            AlarmFrequency.WEEKLY -> AlarmManager.INTERVAL_DAY * 7
            AlarmFrequency.MONTHLY -> AlarmManager.INTERVAL_DAY * 30
        }
    }
}

enum class AlarmFrequency {
    DAILY, WEEKLY, MONTHLY
}
