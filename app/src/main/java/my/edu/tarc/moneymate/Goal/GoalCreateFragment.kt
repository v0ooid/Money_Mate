package my.edu.tarc.moneymate.Goal

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.GoalRepository
import my.edu.tarc.moneymate.databinding.FragmentGoalCreateBinding
import java.util.Calendar

class GoalCreateFragment : Fragment() {
    private var _binding: FragmentGoalCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var goalViewModel: GoalViewModel
    private var currentGoalId: Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dao = AppDatabase.getDatabase(requireContext()).GoalDao()
        val repository = GoalRepository(dao)
        val factory = GoalViewModel.GoalViewModelFactory(repository)
        goalViewModel = ViewModelProvider(this, factory).get(GoalViewModel::class.java)
        _binding = FragmentGoalCreateBinding.inflate(inflater, container, false)

        currentGoalId = arguments?.getLong("goalId") ?: 0
        if (currentGoalId != 0L) {
            loadGoalData(currentGoalId)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener {
            backButton()
        }
        binding.ivGoalOk.setOnClickListener {
            saveGoal()
        }
        binding.etGoalDate.setOnClickListener{
            showDatePickerDialog()
        }
        binding.ivGoalDelete.setOnClickListener{
            confirmDeleteGoal()
        }

    }
    private fun loadGoalData(goalId: Long) {
        goalViewModel.getGoalById(goalId).observe(viewLifecycleOwner, { goal ->
            populateFields(goal)
        })
    }

    private fun populateFields(goal: Goal) {
        binding.title.setText("Edit Goal")
        binding.ivGoalDelete.visibility = View.VISIBLE
        binding.etGoalName.setText(goal.title)
        binding.etGoalDesc.setText(goal.description)
        binding.etGoalTargetAmount.setText(goal.targetAmount.toString())
        binding.etGoalSaved.setText(goal.savedAmount.toString())
        binding.etGoalDate.setText(goal.desiredDate)
    }

    private fun confirmDeleteGoal() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Goal")
            .setMessage("Are you sure you want to delete this goal?")
            .setPositiveButton("Delete") { _, _ ->
                deleteGoal()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteGoal() {
        val goalToDelete = Goal(currentGoalId, "", "", 0, 0, "")
        goalViewModel.delete(goalToDelete)
        Toast.makeText(context, "Goal deleted", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp() // Navigate back after deletion
    }
    private fun showDatePickerDialog() {
        // Get Current Date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Set the selected date to EditText in the format you desire
                val formattedMonth = String.format("%02d",selectedMonth + 1)
                val selectedDate = "${selectedYear}-$formattedMonth-${selectedDayOfMonth}"
                binding.etGoalDate.setText(selectedDate)
            }, year, month, day)

        // Set the minimum date to the current date
        datePickerDialog.datePicker.minDate = c.timeInMillis

        datePickerDialog.show()
    }
    private fun saveGoal() {
        val goalName = binding.etGoalName.text.toString()
        val goalDesc = binding.etGoalDesc.text.toString()
        val targetAmount = binding.etGoalTargetAmount.text.toString().toIntOrNull() ?: 0
        val savedAmount = binding.etGoalSaved.text.toString().toIntOrNull() ?: 0
        val desiredDate = binding.etGoalDate.text.toString()

        if (validateInput(goalName, targetAmount, savedAmount, desiredDate)) {
            if (currentGoalId != 0L)
           {
               val updatedGoal = Goal(currentGoalId, goalName, goalDesc, targetAmount, savedAmount, desiredDate)
               goalViewModel.update(updatedGoal)
           }
            else{
               val newGoal = Goal(0, goalName, goalDesc, targetAmount, savedAmount, desiredDate)

               goalViewModel.insert(newGoal)
           }

            Toast.makeText(context, "Goal created successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
        }
    }
    private fun validateInput(name: String, targetAmount: Int, savedAmount: Int, date: String): Boolean {
        // Add your validation logic here
        return name.isNotBlank() && targetAmount > 0 && savedAmount >= 0 && date.isNotBlank()
    }


    private fun backButton() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton("No", null)
            .show() // This line is necessary to display the dialog
    }

//    var goalName = binding.etGoalName.text.toString()
//    var goalDesc = binding.etGoalDesc.text.toString()
//    var targetAmount = binding.etGoalTargetAmount.text.toString()
//    var savedAmount = binding.etGoalSaved.text.toString()
//    var desiredDate = binding.etGoalDate.text.toString()
//
//    goalViewModel.insert(
//    Goal(
//    0,
//    goalName,
//    goalDesc,
//    targetAmount.toInt(),
//    savedAmount.toInt(),
//    desiredDate
//    )
//    )
//
//
//    goalViewModel.allGoals.observe(viewLifecycleOwner) { data ->
//        Log.d("test goal view model", data.toString())
//    }
//    findNavController().navigateUp()
//    Toast.makeText(context, "Goal have successfully created", Toast.LENGTH_SHORT).show()

}