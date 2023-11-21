package my.edu.tarc.moneymate.Income

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Category.CategoryViewModel
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

    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        categorylist_income = ArrayList()
//        Log.e("testArray","got")
//        recyclerView = binding.recyclerViewIncome
//        Log.e("testrecycle","got")
//        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(),4)
//        recyclerView!!.layoutManager = layoutManager
//        recyclerView!!.adapter = IncomeAdapter
//        prepareList()
//
//
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        //Show income category list recycleview
        recyclerView = binding.recyclerViewIncome
        recyclerView.layoutManager = GridLayoutManager(context, 4)

        CategoryViewModel.incomeCategory.observe(viewLifecycleOwner) { data ->
            categorylist_income = data
            IncomeAdapter = IncomeAdapter(transactionViewModel, this, categorylist_income)
            recyclerView.itemAnimator = null
            recyclerView.adapter = IncomeAdapter
        }
        Log.d("test outside data", categorylist_income.toString())


//testing button set
//        binding.testing.setOnClickListener {
//            Log.w("wellFunction", "well")
//            transactionViewModel.result.observe(viewLifecycleOwner, Observer { data ->
//                Log.w("testResultData", data)
//            })
//            transactionViewModel.title.observe(viewLifecycleOwner, Observer { data ->
//                Log.w("testTitleButtonClicked", data)
//            })
//            transactionViewModel.transactionDescription.observe(
//                viewLifecycleOwner,
//                Observer { data ->
//                    Log.w("testTitleDescClicked", data)
//                })
//            transactionViewModel.selectedAccount.observe(viewLifecycleOwner, Observer { data ->
//                Log.w("testSelected_account", data)
//            })
//            transactionViewModel.transactionType.observe(viewLifecycleOwner, Observer { data ->
//                Log.w("testTransaction_ TYPE", data)
//            })
//
//            transactionViewModel.result.removeObservers(viewLifecycleOwner)
//            transactionViewModel.title.removeObservers(viewLifecycleOwner)
//            transactionViewModel.transactionDescription.removeObservers(viewLifecycleOwner)
//            transactionViewModel.selectedAccount.removeObservers(viewLifecycleOwner)
//            transactionViewModel.transactionType.removeObservers(viewLifecycleOwner)
//        }

//        prepareList()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(IncomeViewModel::class.java)

    }

    private fun prepareList() {
//        var transactionType =""
//        transactionViewModel.transactionType.observe(viewLifecycleOwner){
//            data -> transactionType = data
//        }
//        if (transactionType == "income")
        var list = Category(0, "Salary", R.drawable.baseline_email_24, "income")
        categorylist_income.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Dairy", R.drawable.round_arrow_back_24, "income")
        categorylist_income.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Promote", R.drawable.round_person_24, "income")
        categorylist_income.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Dairy", R.drawable.round_arrow_back_24, "income")
        categorylist_income.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Promote", R.drawable.round_person_24, "income")
        categorylist_income.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Dairy", R.drawable.round_arrow_back_24, "income")
        categorylist_income.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Promote", R.drawable.round_person_24, "income")
        categorylist_income.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Dairy", R.drawable.round_arrow_back_24, "income")
        categorylist_income.add(list)
        CategoryViewModel.addCategoryItem(list)
        list = Category(0, "Promote", R.drawable.round_person_24, "income")
        categorylist_income.add(list)
        CategoryViewModel.addCategoryItem(list)
        IncomeAdapter!!.notifyDataSetChanged()
//
//        CategoryViewModel.addCategoryItem(list)
//
//        CategoryViewModel.incomeCategory.observe(viewLifecycleOwner,{
//                items-> categorylist_income.addAll(items)
//            IncomeAdapter.notifyDataSetChanged()
//            Log.d("size of items", items.size.toString())
//        })
    }


}