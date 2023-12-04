package my.edu.tarc.moneymate.Gamification

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentDailyTaskBinding

class DailyTaskFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentDailyTaskBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDailyTaskBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("GamificationPref", Context.MODE_PRIVATE)

        val dailyLoginCount = sharedPreferences.getInt("LoggedInDays", 0)
        val incomeRecordCount = sharedPreferences.getInt("RecordedIncomes", 0)
        val expenseRecordCount = sharedPreferences.getInt("RecordedExpenses", 0)

        Log.e("LoggedInDays", dailyLoginCount.toString())
        Log.e("RecordedIncomes", incomeRecordCount.toString())
        Log.e("RecordedExpenses", expenseRecordCount.toString())


        val tvDailyLoginCounter = binding.tvDailyLoginCounter
        val tvExpenseCounter = binding.tvRecordExpenseCounter
        val tvIncomeCounter = binding.tvRecordIncomeCounter


        updateProgressCounter(tvDailyLoginCounter, dailyLoginCount)
        updateProgressCounter(tvExpenseCounter, expenseRecordCount)
        updateProgressCounter(tvIncomeCounter, incomeRecordCount)

        val navController = findNavController()
        binding.btnBackMAccount.setOnClickListener{
            navController.popBackStack()
        }

        return binding.root
    }

    fun updateProgressCounter(textView: TextView, count: Int) {
        if (count >= 5) {
            textView.visibility = View.GONE
            // Show the "Completed" text
            when (textView.id) {
                R.id.tvDailyLoginCounter -> binding.tvDailyLoginComplted.visibility = View.VISIBLE
                R.id.tvRecordIncomeCounter -> binding.tvRecordIncomeCompleted.visibility = View.VISIBLE
                R.id.tvRecordExpenseCounter -> binding.tvRecordExpenseCompleted.visibility = View.VISIBLE
            }
        } else {
            // Update counter text and make it visible
            textView.text = "$count/5"
            textView.visibility = View.VISIBLE
            // Hide the "Completed" text
            when (textView.id) {
                R.id.tvDailyLoginCounter -> binding.tvDailyLoginComplted.visibility = View.GONE
                R.id.tvRecordIncomeCounter -> binding.tvRecordIncomeCompleted.visibility = View.GONE
                R.id.tvRecordExpenseCounter -> binding.tvRecordExpenseCompleted.visibility = View.GONE
            }
        }
    }
}