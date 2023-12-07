package my.edu.tarc.moneymate.Income

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Category.CategoryViewModel
import my.edu.tarc.moneymate.IconSpinnerAdapter
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transaction.TransactionViewModel
import my.edu.tarc.moneymate.databinding.FragmentIncomeBinding

class IncomeFragment : Fragment() {

    private lateinit var viewModel: IncomeViewModel
    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var IncomeAdapter: IncomeAdapter
    private var categorylist_income = mutableListOf<Category>()
    val transactionViewModel: TransactionViewModel by activityViewModels()
    val CategoryViewModel: CategoryViewModel by activityViewModels()
    private var currentNumber = ""
    private var firstNumber = ""
    private var result = ""
    private var currentOperator = ""
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
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        //Show income category list recycleview
        recyclerView = binding.recyclerViewIncome
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        CategoryViewModel.incomeCategory.observe(viewLifecycleOwner) { data ->
            categorylist_income = data
            Log.d("test outside data", data.toString())

            IncomeAdapter = IncomeAdapter(transactionViewModel, this, categorylist_income)
            recyclerView.itemAnimator = null
            recyclerView.adapter = IncomeAdapter
        }
        Log.d("test outside data", categorylist_income.toString())

        binding.btnAddNewIncomeCategory.setOnClickListener{
            showAddCategoryDialog()
        }
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
                val newCategory = Category(title = title, image = selectedIcon, type = "income")
                CategoryViewModel.addCategoryItem(newCategory)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(IncomeViewModel::class.java)

    }




}