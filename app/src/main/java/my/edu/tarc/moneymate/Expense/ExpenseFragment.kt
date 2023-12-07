package my.edu.tarc.moneymate.Expense

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Category.CategoryViewModel
import my.edu.tarc.moneymate.IconSpinnerAdapter
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
    val icons = listOf(
        R.drawable.round_money_24,
        R.drawable.advise,
        R.drawable.baseline_attach_money_24,
        R.drawable.baseline_person_24,
        R.drawable.malaysian_ringgit_icon,
        R.drawable.round_auto_awesome_24,
        R.drawable.round_local_drink_24,
        R.drawable.round_fastfood_24,
        R.drawable.baseline_fastfood_24,
        R.drawable.bill
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseBinding.inflate(inflater,container,false)
        recyclerView = binding.recyclerViewExpense

        recyclerView.layoutManager = GridLayoutManager(context,3)

        CategoryViewModel.expenseCategory.observe(viewLifecycleOwner){
            data-> categorylist_expense = data
            ExpenseAdapter = ExpenseAdapter(transactionViewModel,this,categorylist_expense)
            recyclerView.itemAnimator = null
            recyclerView.adapter = ExpenseAdapter
        }


        transactionViewModel.result.observe(viewLifecycleOwner, Observer{ data ->
            Log.w("test",data)
        })
        binding.btnAddNewExpenseCategory.setOnClickListener{
            showAddCategoryDialog()
        }

//        prepareList()
        return binding.root
    }
    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_new_category_layout, null)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextCategoryTitle)
        val spinnerIcon = dialogView.findViewById<Spinner>(R.id.spinnerCategoryIcon)

        // Set up the spinner with the adapter
        val adapter = IconSpinnerAdapter(requireContext(), icons)
        spinnerIcon.adapter = adapter

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Add New Category")
            .setPositiveButton("Add") { dialog, _ ->
                val title = editTextTitle.text.toString()
                val selectedIcon = icons[spinnerIcon.selectedItemPosition]
                val newCategory = Category(title = title, image = selectedIcon, type = "expense")
                CategoryViewModel.addCategoryItem(newCategory)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
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