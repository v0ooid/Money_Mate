package my.edu.tarc.moneymate.Expense

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Income.IncomeAdapter
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transaction.TransactionViewModel
import my.edu.tarc.moneymate.databinding.FragmentExpenseBinding

class ExpenseFragment : Fragment() {


    companion object {
        fun newInstance() = ExpenseFragment()
    }
    private var _binding: FragmentExpenseBinding ?=null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var ExpenseAdapter: ExpenseAdapter
    private var categorylist_expense = mutableListOf<Expense>()
    private lateinit var viewModel: ExpenseViewModel
    val transactionViewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseBinding.inflate(inflater,container,false)
        recyclerView = binding.recyclerViewExpense
        recyclerView.layoutManager = GridLayoutManager(context,4)
        ExpenseAdapter = ExpenseAdapter(categorylist_expense)
        recyclerView.itemAnimator = null
        recyclerView.adapter = ExpenseAdapter
        transactionViewModel.result.observe(viewLifecycleOwner, Observer{ data ->
            Log.w("test",data)
        })


        prepareList()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        // TODO: Use the ViewModel
    }
    private fun prepareList()
    {
        var list = Expense("Salary", R.drawable.baseline_email_24)
        categorylist_expense.add(list)
        list = Expense("Slave", R.drawable.round_person_24)
        categorylist_expense.add(list)
        list = Expense("Weapon", R.drawable.round_arrow_back_24)
        categorylist_expense.add(list)
        list = Expense("Shelter", R.drawable.baseline_home_24)
        categorylist_expense.add(list)
        list = Expense("Salary", R.drawable.baseline_email_24)
        categorylist_expense.add(list)
        list = Expense("Slave", R.drawable.round_person_24)
        categorylist_expense.add(list)
        list = Expense("Weapon", R.drawable.round_arrow_back_24)
        categorylist_expense.add(list)
        list = Expense("Shelter", R.drawable.baseline_home_24)
        categorylist_expense.add(list)
        list = Expense("Salary", R.drawable.baseline_email_24)
        categorylist_expense.add(list)
        list = Expense("Slave", R.drawable.round_person_24)
        categorylist_expense.add(list)
        list = Expense("Weapon", R.drawable.round_arrow_back_24)
        categorylist_expense.add(list)
        list = Expense("Shelter", R.drawable.baseline_home_24)
        categorylist_expense.add(list)
        list = Expense("Salary", R.drawable.baseline_email_24)
        categorylist_expense.add(list)
        list = Expense("Slave", R.drawable.round_person_24)
        categorylist_expense.add(list)
        list = Expense("Weapon", R.drawable.round_arrow_back_24)
        categorylist_expense.add(list)
        list = Expense("Shelter", R.drawable.baseline_home_24)
        categorylist_expense.add(list)
        list = Expense("Salary", R.drawable.baseline_email_24)
        categorylist_expense.add(list)
        list = Expense("Slave", R.drawable.round_person_24)
        categorylist_expense.add(list)
        list = Expense("Weapon", R.drawable.round_arrow_back_24)
        categorylist_expense.add(list)
        list = Expense("Shelter", R.drawable.baseline_home_24)
        categorylist_expense.add(list)



        ExpenseAdapter!!.notifyDataSetChanged()
    }

}