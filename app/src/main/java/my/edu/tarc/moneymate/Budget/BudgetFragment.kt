package my.edu.tarc.moneymate.Budget

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.BudgetDao
import my.edu.tarc.moneymate.Database.MonetaryAccountDao
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.MonetaryAccount.mAccountAdapter
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentBudgetBinding
import my.edu.tarc.moneymate.databinding.FragmentMonetaryAccountBinding

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: mAccountAdapter
    private lateinit var budgetDao: BudgetDao

    private val budgetViewModel: BudgetViewModel by activityViewModels()
    private var budget: Budget? = null
    private var icon = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)

        val appDatabase = AppDatabase.getDatabase(requireContext())
        budgetDao = appDatabase.budgetDao()

        binding.rvBudget.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudget.setHasFixedSize(true)

        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)
    }

}