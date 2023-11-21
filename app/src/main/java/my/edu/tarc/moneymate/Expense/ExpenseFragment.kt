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
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Category.CategoryViewModel
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
    private var categorylist_expense = mutableListOf<Category>()
    private lateinit var viewModel: ExpenseViewModel
    val transactionViewModel: TransactionViewModel by activityViewModels()
    val CategoryViewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseBinding.inflate(inflater,container,false)
        recyclerView = binding.recyclerViewExpense

        recyclerView.layoutManager = GridLayoutManager(context,4)

        CategoryViewModel.expenseCategory.observe(viewLifecycleOwner){
            data-> categorylist_expense = data
            ExpenseAdapter = ExpenseAdapter(transactionViewModel,this,categorylist_expense)
            recyclerView.itemAnimator = null
            recyclerView.adapter = ExpenseAdapter
        }


        transactionViewModel.result.observe(viewLifecycleOwner, Observer{ data ->
            Log.w("test",data)
        })


//        prepareList()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        // TODO: Use the ViewModel
    }
    private fun prepareList()
    {
        var list = Category(0, "Salary", R.drawable.baseline_email_24, "expense")
        categorylist_expense.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Dairy", R.drawable.round_arrow_back_24, "expense")
        categorylist_expense.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Promote", R.drawable.round_person_24, "expense")
        categorylist_expense.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Dairy", R.drawable.round_arrow_back_24, "expense")
        categorylist_expense.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Promote", R.drawable.round_person_24, "expense")
        categorylist_expense.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Dairy", R.drawable.round_arrow_back_24, "expense")
        categorylist_expense.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Promote", R.drawable.round_person_24, "expense")
        categorylist_expense.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Dairy", R.drawable.round_arrow_back_24, "expense")
        categorylist_expense.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Promote", R.drawable.round_person_24, "expense")
        categorylist_expense.add(list)
        CategoryViewModel.addCategoryItem(list)

        ExpenseAdapter!!.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        transactionViewModel.result.removeObservers(viewLifecycleOwner)
        transactionViewModel.title.removeObservers(viewLifecycleOwner)
        transactionViewModel.transactionDescription.removeObservers(viewLifecycleOwner)
        transactionViewModel.selectedAccount.removeObservers(viewLifecycleOwner)
        transactionViewModel.transactionType.removeObservers(viewLifecycleOwner)
    }

}