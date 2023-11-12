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
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transaction.TransactionViewModel
import my.edu.tarc.moneymate.databinding.FragmentIncomeBinding

class IncomeFragment : Fragment() {

    private lateinit var viewModel: IncomeViewModel
    private var _binding: FragmentIncomeBinding?= null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var IncomeAdapter: IncomeAdapter
    private var categorylist_income = mutableListOf<Income>()
    val transactionViewModel: TransactionViewModel by activityViewModels()
    private var currentNumber =""
    private var firstNumber =""
    private var result =""
    private var currentOperator =""

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
        recyclerView.layoutManager = GridLayoutManager(context,4)
        IncomeAdapter = IncomeAdapter(this,categorylist_income)
        recyclerView.itemAnimator = null
        recyclerView.adapter = IncomeAdapter

        binding.testing.setOnClickListener {
            Log.w("wellFunction","well")
            transactionViewModel.result.observe(viewLifecycleOwner,Observer{data ->
                Log.w("test",data)
            })
            transactionViewModel.result.removeObservers(viewLifecycleOwner)
        }






        prepareList()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(IncomeViewModel::class.java)

    }

    private fun prepareList()
    {
        var list = Income("Salary", R.drawable.baseline_email_24)
        categorylist_income.add(list)
        list = Income("Dairy", R.drawable.round_arrow_back_24)
        categorylist_income.add(list)
        list = Income("Promote", R.drawable.round_person_24)
        categorylist_income.add(list)
        list = Income("Sleep", R.drawable.baseline_email_24)
        categorylist_income.add(list)
        list = Income("Dairy", R.drawable.round_arrow_back_24)
        categorylist_income.add(list)
        list = Income("Promote", R.drawable.round_person_24)
        categorylist_income.add(list)
        list = Income("Sleep", R.drawable.baseline_email_24)
        categorylist_income.add(list)
        list = Income("Dairy", R.drawable.round_arrow_back_24)
        categorylist_income.add(list)
        list = Income("Promote", R.drawable.round_person_24)
        categorylist_income.add(list)
        list = Income("Sleep", R.drawable.baseline_email_24)
        categorylist_income.add(list)
        list = Income("Dairy", R.drawable.round_arrow_back_24)
        categorylist_income.add(list)
        list = Income("Promote", R.drawable.round_person_24)
        categorylist_income.add(list)
        list = Income("Sleep", R.drawable.baseline_email_24)
        categorylist_income.add(list)
        list = Income("Dairy", R.drawable.round_arrow_back_24)
        categorylist_income.add(list)
        list = Income("Promote", R.drawable.round_person_24)
        categorylist_income.add(list)
        list = Income("Sleep", R.drawable.baseline_email_24)
        categorylist_income.add(list)
        list = Income("Dairy", R.drawable.round_arrow_back_24)
        categorylist_income.add(list)
        list = Income("Promote", R.drawable.round_person_24)
        categorylist_income.add(list)
        list = Income("Sleep", R.drawable.baseline_email_24)
        categorylist_income.add(list)

        IncomeAdapter!!.notifyDataSetChanged()
    }



}