package my.edu.tarc.moneymate.Goal

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.GoalRepository
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentGoalCreateBinding
import my.edu.tarc.moneymate.databinding.FragmentShowGoalBinding

class GoalFragment : Fragment() {

    companion object {
        fun newInstance() = GoalFragment()
    }

    private var _binding: FragmentShowGoalBinding? = null
    private val binding get() = _binding!!
    private lateinit var goalViewModel: GoalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowGoalBinding.inflate(inflater, container, false)
        val factory = GoalViewModel.GoalViewModelFactory(
            GoalRepository(
                AppDatabase.getDatabase(requireContext()).GoalDao()
            )
        )
        goalViewModel = ViewModelProvider(this, factory).get(GoalViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeGoals()
        binding.fabTransactionAdd.setOnClickListener {
            navigateToCreateGoal()
        }
        binding.leftIcon.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    //    private fun setupRecyclerView() {
//        val adapter = GoalsAdapter(
//            listOf(),
//            onGoalClick = { goal -> editGoal(goal) },
//            onGoalLongClick = { goal -> confirmDeleteGoal(goal) }
//        )// Initially empty list
//        binding.rvGoalList.adapter = adapter
//        binding.rvGoalList.layoutManager = LinearLayoutManager(context)
//    }
    private fun setupRecyclerView() {
        val adapter = GoalsAdapter(
            listOf(),
            onGoalClick = { goal -> navigateToGoalDetail(goal) },
            onGoalLongClick = { goal -> confirmDeleteGoal(goal) }
        )
        binding.rvGoalList.adapter = adapter
        binding.rvGoalList.layoutManager = LinearLayoutManager(context)
    }

    private fun navigateToGoalDetail(goal: Goal) {
        // Assuming you have a navigation action defined in your navigation graph
        val action = GoalFragmentDirections.actionGoalFragmentToGoalDetailsFragment(goal.id)
        findNavController().navigate(action)
    }

    private fun editGoal(goal: Goal) {
        val action = GoalFragmentDirections.actionGoalFragmentToGoalCreateFragment(goal.id)
        findNavController().navigate(action)
    }

    private fun confirmDeleteGoal(goal: Goal) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Goal")
            .setMessage("Are you sure you want to delete this goal?")
            .setPositiveButton("Delete") { _, _ ->
                goalViewModel.delete(goal)
                Toast.makeText(requireContext(), "Goal deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeGoals() {
        goalViewModel.allGoals.observe(viewLifecycleOwner) { goals ->
            (binding.rvGoalList.adapter as GoalsAdapter).updateGoals(goals)
        }
    }

    private fun navigateToCreateGoal() {
        findNavController().navigate(R.id.action_goalFragment_to_goalCreateFragment)
    }


}