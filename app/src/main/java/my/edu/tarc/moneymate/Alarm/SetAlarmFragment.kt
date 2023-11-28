package my.edu.tarc.moneymate.Alarm

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentSetAlarmBinding
import java.util.Calendar


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
        Log.d("editingAlarmId", editingAlarmId.toString())
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
        saveButton.setOnClickListener {
            saveAlarmSetting()

            findNavController().navigateUp()
            Toast.makeText(context, "Alarm Have been set", Toast.LENGTH_SHORT).show()
        }


    }

    private fun loadAlarmData(alarmId: Long) {
        viewModel.getAlarmById(alarmId).observe(viewLifecycleOwner) { alarm ->
            alarm?.let { populateFields(it) }
        }
    }


    //    viewModel.allNotification.observe(viewLifecycleOwner){
//        alarm->
//
//        val position = alarmList.indexOfFirst { it.id == editingAlarmId }
//        // Use the 'position' as needed
//        if (position != -1) {
//            // Alarm with the specified alarmId found at 'position'
//            specificAlarm = alarmList[position]
//            Log.d("selecific alarm2", specificAlarm.toString())
//
//            // Do something with specificAlarm
//        }
//    }
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
        val channelId = "Alarm_Notification_Channel"
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            action = "my.edu.tarc.moneymate.ALARM_ACTION"
            putExtra("Channel_ID", channelId)
            putExtra("Title", alarm.title)
            putExtra("Desc", alarm.description)
        }
        Log.d("title", alarm.title)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d("Set Alarm Id", alarm.id.toString())

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minit)
            set(Calendar.SECOND, 0)
        }
        try {


            if (alarm.repeatDay.isEmpty()) {
                // For one-time alarm
                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    calendar.add(
                        Calendar.DAY_OF_YEAR,
                        1
                    ) // Set for the next day if time is in the past
                }
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d("testing no repeat day", Calendar.DAY_OF_YEAR.toString())
            } else {
                // For repeating alarms
                for (day in alarm.repeatDay) {
                    val dayOfWeek = getDayOfWeek(day)
                    Log.d("Day of week", dayOfWeek.toString())
                    Log.d("Day ", day)

                    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                    if (calendar.timeInMillis < System.currentTimeMillis()) {
                        calendar.add(Calendar.DAY_OF_YEAR, 7)
                    }
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingIntent
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.e("Alarm Scheduling Error", "SecurityException: ${e.message}")
        }
        Log.d(
            "Alarm Scheduled",
            "Hour: ${alarm.hour}, Minute: ${alarm.minit}, Days: ${alarm.repeatDay}"
        )
    }
    private fun deleteAlarm() {
        // Cancel the alarm from AlarmManager
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            action = "my.edu.tarc.moneymate.ALARM_ACTION"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            specificAlarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d("SpecificAlarm Id", specificAlarm.id.toString())
        alarmManager.cancel(pendingIntent)

        // Delete the alarm from your data source (e.g., ViewModel, database)
        viewModel.delete(specificAlarm)

        // Navigate back
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

        var hour = timePicker.hour
        var minute = timePicker.minute
        val uniqueId = System.currentTimeMillis().toInt()

        var title: String = binding.eTAlarmTitle.text.toString()
        if (binding.eTAlarmTitle.text.toString() == "") {
            title = "Alarm Notification"
        }
        var desc: String = binding.eTAlarmDescription.text.toString()
        if (binding.eTAlarmDescription.text.toString() == "") {
            desc = "Alarm Description"
        }
        var selectedDay = mutableListOf<String>()
        if (checkboxMonday.isChecked) {
            selectedDay.add("Monday")
        }
        if (checkBoxTuesday.isChecked) {
            selectedDay.add("Tuesday")
        }
        if (checkBoxWednesday.isChecked) {
            selectedDay.add("Wednesday")
        }
        if (checkBoxThursday.isChecked) {
            selectedDay.add("Thursday")
        }
        if (checkBoxFriday.isChecked) {
            selectedDay.add("Friday")
        }
        if (checkBoxSaturday.isChecked) {
            selectedDay.add("Saturday")
        }
        if (checkBoxSunday.isChecked) {
            selectedDay.add("Sunday")
        }
        val alarm =
            if (editingAlarmId!= null && editingAlarmId!!.toInt() != 0) {
                AlarmNotification(
                    id = editingAlarmId!!,
                    hour = hour,
                    minit = minute,
                    repeatDay = selectedDay.toList(),
                    title = title,
                    description = desc,
                )

            } else {
                AlarmNotification(
                    id = uniqueId.toLong(),
                    hour = hour,
                    minit = minute,
                    repeatDay = selectedDay.toList(),
                    title = title,
                    description = desc,
                )

            }
        Log.e("Log to test alarm ", editingAlarmId.toString())
        Log.d("uniqueId.toLong()", uniqueId.toString())
        Log.d("test alarm data", alarm.toString())
        val currentList = viewModel.notification.value ?: emptyList()
        val updatedList = currentList + alarm

//        viewModel.updateNotification(updatedList)
//        viewModel.insertAll(updatedList)

        if (editingAlarmId?.toInt() != 0) {
            viewModel.updateAlarm(alarm)
        } else {
            viewModel.insertAll(updatedList)
        }


        Log.d("testing", updatedList.toString())
//        viewModel.updateNotification(alarmList)
        scheduleAlarm(alarm)
        Log.d("Alarm ID after being set", alarm.id.toString())

        binding.timepicker.setOnTimeChangedListener { view, hourOfDays, minitOfDays ->

            hour = hourOfDays
            minute = minitOfDays

            val checkAmOrPm = if (hourOfDays < 12) "AM" else "PM"
            val displayHour = if (hourOfDays > 12) hourOfDays - 12 else hourOfDays
            Log.d("test hour", hour.toString())
            Log.d("test hour", minute.toString())
        }
        specificAlarm = alarm
    }



    private fun getDayOfWeek(day: String): Int {
        return when (day.lowercase()) {
            "sunday" -> Calendar.SUNDAY
            "monday" -> Calendar.MONDAY
            "tuesday" -> Calendar.TUESDAY
            "wednesday" -> Calendar.WEDNESDAY
            "thursday" -> Calendar.THURSDAY
            "friday" -> Calendar.FRIDAY
            "saturday" -> Calendar.SATURDAY
            else -> throw IllegalArgumentException("Invalid day: $day")
        }
    }


}