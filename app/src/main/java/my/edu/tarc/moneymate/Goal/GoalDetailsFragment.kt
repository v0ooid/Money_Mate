package my.edu.tarc.moneymate.Goal

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.GoalRepository
import my.edu.tarc.moneymate.databinding.FragmentGoalDetailsBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class GoalDetailsFragment : Fragment() {
    private var _binding: FragmentGoalDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var goalViewModel: GoalViewModel
    private lateinit var savedAmountAdapter: SavedAmountAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalDetailsBinding.inflate(inflater,container,false)
        val factory = GoalViewModel.GoalViewModelFactory(GoalRepository(AppDatabase.getDatabase(requireContext()).GoalDao()))
        goalViewModel = ViewModelProvider(this, factory).get(GoalViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val goalId = GoalDetailsFragmentArgs.fromBundle(requireArguments()).goalId
        setupSavedAmountsRecyclerView(goalId)
        observeGoalData(goalId)
        binding.btnAddSavedAmount.setOnClickListener {
            showAddSavedAmountDialog(goalId)
        }
        binding.backIcon.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.ivGoalEdit.setOnClickListener {
            val goalId = GoalDetailsFragmentArgs.fromBundle(requireArguments()).goalId
            val action = GoalDetailsFragmentDirections.actionGoalDetailsFragmentToGoalCreateFragment(goalId)
            findNavController().navigate(action)
        }
    }
    private fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observe(viewLifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    private fun observeGoalData(goalId: Long) {
        goalViewModel.getGoalById(goalId).observe(viewLifecycleOwner) { goal ->
            displayGoalDetails(goal)
            updateProgressBar(goal.savedAmount,goal.targetAmount)
        }
    }

    private fun displayGoalDetails(goal: Goal) {
        binding.tvTargetDate.text = "${goal.desiredDate}"
        binding.duration.text = "${calculateDuration(goal.desiredDate)} Days"
        binding.saved.text = "RM ${goal.savedAmount}"
        binding.targetAmount.text = "RM ${goal.targetAmount}"
    }
    private fun displayGoalDuration(targetDate: String) {
        val durationDays = calculateDuration(targetDate)
        binding.duration.text = "${durationDays} Days"
    }
    companion object {
        fun calculateDuration(targetDate: String): Long {
            val endDate = LocalDate.parse(targetDate)
            val today = LocalDate.now()
            Log.d("GoalDetailsFragment", "$today,$endDate")
            return ChronoUnit.DAYS.between(today, endDate)
        }
    }
    private fun updateProgressBar(savedAmount: Int, targetAmount: Int) {
        val progressBar = binding.progressBar
        val progress = if (targetAmount > 0) {
            (savedAmount * 100 / targetAmount).coerceIn(0, 100)
        } else {
            0 // Avoid division by zero
        }
        binding.progressText.text = "RM $savedAmount / RM $targetAmount"
        progressBar.progress = progress
    }

    private fun showAddSavedAmountDialog(goalId: Long) {
        val editText = EditText(context)

        editText.inputType = InputType.TYPE_CLASS_NUMBER
        AlertDialog.Builder(requireContext())
            .setTitle("Add Saved Amount (RM)")
            .setView(editText)
            .setPositiveButton("Enter") { _, _ ->
                val newAmount = editText.text.toString().toIntOrNull() ?: 0
                updateGoalSavedAmount(goalId, newAmount)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun updateGoalSavedAmount(goalId: Long, newAmount: Int) {
        goalViewModel.getGoalById(goalId).observeOnce { goal ->
            val updatedAmount = goal.savedAmount + newAmount
            val updatedGoal = goal.copy(savedAmount = updatedAmount)
            val newSavedAmount = SavedAmount(goalId = goalId, amount = newAmount, date = System.currentTimeMillis())

            // Update the goal and add the new saved amount
            goalViewModel.update(updatedGoal)
            goalViewModel.addSavedAmount(newSavedAmount)
        }
    }


    private fun setupSavedAmountsRecyclerView(goalId: Long) {
        savedAmountAdapter = SavedAmountAdapter(emptyList())
        binding.addSavedHistory.adapter = savedAmountAdapter
        binding.addSavedHistory.layoutManager = LinearLayoutManager(context)

        val goalId = GoalDetailsFragmentArgs.fromBundle(requireArguments()).goalId
        goalViewModel.getSavedAmountsByGoalId(goalId).observe(viewLifecycleOwner) { savedAmounts ->
            savedAmountAdapter.updateData(savedAmounts)
        }
    }
}