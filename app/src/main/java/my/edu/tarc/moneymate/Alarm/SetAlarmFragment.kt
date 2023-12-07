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
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentSetAlarmBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class SetAlarmFragment : Fragment() {


    private val binding get() = _binding!!
    private var _binding: FragmentSetAlarmBinding? = null
    private lateinit var viewModel: AlarmNotificationViewModel
    private lateinit var timePicker: TimePicker
    private lateinit var checkboxMonday: CheckBox
    private lateinit var checkBoxTuesday: CheckBox
    private lateinit var checkBoxWednesday: CheckBox
    private lateinit var checkBoxThursday: CheckBox
    private lateinit var checkBoxFriday: CheckBox
    private lateinit var checkBoxSaturday: CheckBox
    private lateinit var checkBoxSunday: CheckBox
    private var editingAlarmId: Long? = null
    private lateinit var specificAlarm: AlarmNotification

    //    private lateinit var AlarmNotificationViewModel: AlarmNotificationViewModel
    private var alarmList: MutableList<AlarmNotification> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSetAlarmBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(AlarmNotificationViewModel::class.java)
        arguments?.let {
            editingAlarmId = SetAlarmFragmentArgs.fromBundle(it).alarmId
        }
        editingAlarmId?.let { loadAlarmData(it) }
        Log.d("Set Alarm Fragment", "editing Alarm Id $editingAlarmId")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timePicker = view.findViewById(R.id.timepicker)
        checkboxMonday = view.findViewById(R.id.cbMonday)
        checkBoxTuesday = view.findViewById(R.id.cbTuesday)
        checkBoxWednesday = view.findViewById(R.id.cbWednesday)
        checkBoxThursday = view.findViewById(R.id.cbThursday)
        checkBoxFriday = view.findViewById(R.id.cbFriday)
        checkBoxSaturday = view.findViewById(R.id.cbSaturday)
        checkBoxSunday = view.findViewById(R.id.cbSunday)

        val repeatButton: Button = view.findViewById(R.id.button_set_repeat)
        val repeatDayCard: CardView = view.findViewById(R.id.cardViewRepeatDay)
        val saveButton: ImageView = view.findViewById(R.id.button_ok)
        val backButton: ImageView = view.findViewById(R.id.button_cancel)
        val deleteButton: ImageView = view.findViewById(R.id.button_delete)
        binding.buttonSetRepeat.setOnClickListener {
            if (repeatDayCard.visibility == View.GONE) {
                binding.cardViewRepeatDay.visibility = View.VISIBLE
            } else if (repeatDayCard.visibility == View.VISIBLE) {
                binding.cardViewRepeatDay.visibility = View.GONE
            }
        }

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }


        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()

        }
        checkAndRequestExactAlarmPermission()

        saveButton.setOnClickListener {
            saveAlarmSetting()

            findNavController().navigateUp()
            Toast.makeText(context, "Alarm Have been set", Toast.LENGTH_SHORT).show()
        }


    }
    private fun checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    private fun loadAlarmData(alarmId: Long) {
        viewModel.getAlarmById(alarmId).observe(viewLifecycleOwner) { alarm ->
            alarm?.let { populateFields(it) }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Alarm")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAlarm()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun scheduleAlarm(alarm: AlarmNotification) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("Channel_ID", "Alarm_Notification_Channel_ID")
            putExtra("Alarm Id", alarm.id)
            putExtra("Title", alarm.title)
            putExtra("Desc", alarm.description)
            putExtra("Repeating", alarm.repeatDay.isNotEmpty())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minit)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Log the time for debugging
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
        val formattedDateTime = sdf.format(calendar.time)
        Log.d("Alarm Scheduling", "Alarm set for: $formattedDateTime")

        if (alarm.repeatDay.isEmpty()) {
            // Schedule a non-repeating alarm
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            // Schedule the first instance of the repeating alarm
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }


    private fun deleteAlarm() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            action = "my.edu.tarc.moneymate.ALARM_ACTION"
        }

        specificAlarm.repeatDay.forEach { day ->
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                (specificAlarm.id + getDayOfWeek(day)).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            Log.d("Set Alarm Fragment", "Delete Alarm Function specific alarm repeat day $day")
            alarmManager.cancel(pendingIntent)
        }

        viewModel.delete(specificAlarm)
        findNavController().navigateUp()
    }

    private fun populateFields(alarm: AlarmNotification) {
        binding.SetAlarmTitle.text = "Edit Alarm"
        binding.buttonDelete.visibility = View.VISIBLE
        binding.eTAlarmTitle.setText(alarm.title)
        binding.eTAlarmDescription.setText(alarm.description)
        timePicker.hour = alarm.hour
        timePicker.minute = alarm.minit

        // Setting the checkboxes for days
        alarm.repeatDay.forEach { day ->
            when (day) {
                "Monday" -> checkboxMonday.isChecked = true
                "Tuesday" -> checkBoxTuesday.isChecked = true
                "Wednesday" -> checkBoxWednesday.isChecked = true
                "Thursday" -> checkBoxThursday.isChecked = true
                "Friday" -> checkBoxFriday.isChecked = true
                "Saturday" -> checkBoxSaturday.isChecked = true
                "Sunday" -> checkBoxSunday.isChecked = true
            }
        }
        specificAlarm = alarm
        // ...
    }


    private fun saveAlarmSetting() {
        val hour = timePicker.hour
        val minute = timePicker.minute
        val title = binding.eTAlarmTitle.text.toString().ifEmpty { "Alarm Notification" }
        val desc = binding.eTAlarmDescription.text.toString().ifEmpty { "Alarm Description" }
        val uniqueId = System.currentTimeMillis().toInt()

        var selectedDays = mutableListOf<String>()
        if (checkboxMonday.isChecked) selectedDays.add("Monday")
        if (checkBoxTuesday.isChecked) selectedDays.add("Tuesday")
        if (checkBoxWednesday.isChecked) selectedDays.add("Wednesday")
        if (checkBoxThursday.isChecked) selectedDays.add("Thursday")
        if (checkBoxFriday.isChecked) selectedDays.add("Friday")
        if (checkBoxSaturday.isChecked) selectedDays.add("Saturday")
        if (checkBoxSunday.isChecked) selectedDays.add("Sunday")


        val alarm = if (editingAlarmId!= null && editingAlarmId!!.toInt() != 0) {
            AlarmNotification(
                id = editingAlarmId!!,
                hour = hour,
                minit = minute,
                repeatDay = selectedDays.toList(),
                title = title,
                description = desc,
            )

        } else {
            AlarmNotification(
                id = uniqueId.toLong(),
                hour = hour,
                minit = minute,
                repeatDay = selectedDays.toList(),
                title = title,
                description = desc,
            )

        }

        Log.d("SetAlarm Fragment","Alarm Data $alarm")

        if (editingAlarmId != null && editingAlarmId != 0L) {
            viewModel.updateAlarm(alarm) // Update existing alarm
            Log.d("Set Alarm Fragment", " Save Alarm Setting update alarm")
        } else {
            viewModel.insert(alarm) // Insert new alarm
            Log.d("Set Alarm Fragment", " Save Alarm Setting insert alarm")

        }
        if (selectedDays.isEmpty()) {
            scheduleAlarm(alarm) // Schedule non-repeating alarm
        } else {
            selectedDays.forEach { day ->
                scheduleAlarmForDay(alarm, day) // Schedule repeating alarms
            }
            selectedDays.clear()

        }

        Toast.makeText(context, "Alarm set successfully", Toast.LENGTH_SHORT).show()

    }



    private fun getDayOfWeek(day: String): Int {
        return when (day.lowercase()) {
            "Sunday" -> Calendar.SUNDAY
            "Monday" -> Calendar.MONDAY
            "Tuesday" -> Calendar.TUESDAY
            "Wednesday" -> Calendar.WEDNESDAY
            "Thursday" -> Calendar.THURSDAY
            "Friday" -> Calendar.FRIDAY
            "Saturday" -> Calendar.SATURDAY
            else -> 0
        }
    }


    private fun scheduleAlarmForDay(alarm: AlarmNotification, dayOfWeek: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minit)
            set(Calendar.SECOND, 0)
            set(Calendar.DAY_OF_WEEK, getDayOfWeek(dayOfWeek))

            // Adjust for next occurrence if time is already past
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        val alarmTimeFormatted = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' HH:mm", Locale.getDefault()).format(calendar.time)
        Log.d("SetAlarmFragment", "Scheduling alarm for: $dayOfWeek, $alarmTimeFormatted in Device Time Zone")

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            action = "my.edu.tarc.moneymate.ALARM_ACTION"
            putExtra("Channel_ID", "Alarm_Notification_Channel")
            putExtra("Alarm Id", alarm.id)
            putExtra("Title", alarm.title)
            putExtra("Desc", alarm.description)
        }

        val requestCode = (alarm.id + getDayOfWeek(dayOfWeek)).toInt() // Unique request code
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use setExactAndAllowWhileIdle for more precise alarm scheduling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }



}