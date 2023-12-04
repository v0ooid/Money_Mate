package my.edu.tarc.moneymate.Record

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Budget.BudgetViewModel
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.ExpenseRepository
import my.edu.tarc.moneymate.Database.RecordRepository
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Expense.ExpenseViewModel
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Income.IncomeAdapter
import my.edu.tarc.moneymate.Income.IncomeViewModel
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccountViewModel
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transaction.TransactionFragment
import my.edu.tarc.moneymate.Transaction.TransactionViewModel
import my.edu.tarc.moneymate.Transfer.TransferViewModel
import my.edu.tarc.moneymate.databinding.FragmentIncomeBinding
import my.edu.tarc.moneymate.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!
    private lateinit var incomerecyclerView: RecyclerView
    private lateinit var expenserecyclerView: RecyclerView
    private lateinit var transferRecyclerView: RecyclerView

    private lateinit var RecordAdapter: RecordAdapter
    private lateinit var RecordExpenseAdapter: RecordExpenseAdapter
    private lateinit var RecordTransferAdapter: RecordTransferAdapter

    private var categorylist_income = mutableListOf<Income>()
    val transactionViewModel: TransactionViewModel by activityViewModels()
    val monetaryAccountViewModel: MonetaryAccountViewModel by activityViewModels()
    val incomeViewModel: IncomeViewModel by activityViewModels()
    val expenseViewModel: ExpenseViewModel by activityViewModels()
    val transferViewModel: TransferViewModel by activityViewModels()
    val recordViewModel: RecordViewModel by activityViewModels()
    val budgetViewModel: BudgetViewModel by activityViewModels()

    private var monetaryAccountList: List<MonetaryAccount>? = null
    private var accountId: List<Long>? = null
    private var accountName: List<String>? = null
    var recordList: List<Record> = emptyList()
    private var recordList2 = mutableListOf<Record>()


    //private lateinit var viewModel: RecordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        Log.e("View Create", "View Create")
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        incomerecyclerView = binding.recordIncomeTransaction
        expenserecyclerView = binding.recordExpenseTransaction
        transferRecyclerView = binding.recordTransferTransaction
//        incomerecyclerView = binding.recordIncomeTransaction
//        incomerecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        RecordAdapter = RecordAdapter(requireContext(),incomeViewModel, recordViewModel,this, recordList2)
//        incomerecyclerView.itemAnimator = null
//        incomerecyclerView.adapter = RecordAdapter
//        incomerecyclerView.adapter?.notifyDataSetChanged()
//
//        expenserecyclerView = binding.recordExpenseTransaction
//        expenserecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        RecordExpenseAdapter = RecordExpenseAdapter(requireContext(),expenseViewModel,recordViewModel, this, recordList2)
//        expenserecyclerView.itemAnimator = null
//        expenserecyclerView.adapter = RecordExpenseAdapter
//        expenserecyclerView.adapter?.notifyDataSetChanged()
//        recordViewModel.getAllRecord.observe(viewLifecycleOwner) { data ->
//            recordList = data
//            recordList2 = data.toMutableList()
//            Log.d("Inside", "Inside")
//
//            Log.d("TestData", data.toString())
//            RecordAdapter.notifyDataSetChanged()
//            RecordExpenseAdapter.notifyDataSetChanged()
//            expenserecyclerView.adapter?.notifyDataSetChanged()
//        }
//
//
//
//        Log.d("recordList", recordList.toString())
//        Log.d("recordList2", recordList2.toString())

        setupRecyclerViews()
        observeRecordData()

        val navController = findNavController()
        binding.ivTask.setOnClickListener{
            navController.navigate(
                R.id.action_recordFragment_to_dailyTaskFragment)
        }
        binding.fabTransactionAdd.setOnClickListener {
            // findNavController().navigate(R.id.action_recordFragment_to_transactionFragment)
            showCustomDialog()
        }

    }

    private fun setupRecyclerViews() {
        incomerecyclerView.layoutManager = LinearLayoutManager(requireContext())
        RecordAdapter = RecordAdapter(requireContext(), incomeViewModel, recordViewModel, this, mutableListOf())
        incomerecyclerView.adapter = RecordAdapter

        expenserecyclerView.layoutManager = LinearLayoutManager(requireContext())
        RecordExpenseAdapter = RecordExpenseAdapter(requireContext(), expenseViewModel, recordViewModel, budgetViewModel,this, mutableListOf())
        expenserecyclerView.adapter = RecordExpenseAdapter

        transferRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        RecordTransferAdapter = RecordTransferAdapter(requireContext(), transferViewModel, mutableListOf())
        transferRecyclerView.adapter = RecordTransferAdapter

        transferViewModel.getAllMAccount.observe(viewLifecycleOwner){
            RecordTransferAdapter.setAccount(it)
        }

    }

    private fun observeRecordData() {
        recordViewModel.getAllRecord.observe(viewLifecycleOwner) { data ->
            // Update your adapters with new data
            RecordAdapter.updateList(data)
            RecordExpenseAdapter.updateList(data)
        }
        transferViewModel.getAllTransfer.observe(viewLifecycleOwner){
            RecordTransferAdapter.updateList(it)
        }
    }

    fun showCustomDialog() {
        // Inflate the custom layout
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.choose_account_dialog, null)
        val dialogSpinner = dialogView.findViewById<Spinner>(R.id.chooseAccount_spinner)

        // Create the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
//
//        // Set up button click handler
//        dialogSpinner.setOnClickListener {
//            val userInput = dialogInput.text.toString()
//            // Handle the user input
//            dialog.dismiss()
//        }
        monetaryAccountViewModel.accountIdsandName.observe(viewLifecycleOwner) { data ->
            val accountIdInside = data.map { it.first }
            val accountNameInside = data.map { it.second }
            val adapterItem = listOf("Account") + accountNameInside
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, adapterItem)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogSpinner.adapter = adapter
            dialogSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d("position outside", position.toString())

                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    if (selectedItem != "Account") {
                        val selectedAccountId = data[position - 1].first
                        val selectedAccountName = accountNameInside[position - 1]
                        Log.d("Account Choosed", selectedItem)
                        Log.d("position", position.toString())
                        Log.d("Account Id", selectedAccountId.toString())
                        Log.d("Account Name", selectedAccountName)

                        transactionViewModel.updateSelectedAccountData(selectedAccountId.toString())
                        val bundle = Bundle()
                        bundle.apply {
                            putString("selectedAccount", selectedItem)
                            putLong("selectedAccountId", selectedAccountId)
                            putString("selectedAccountName", selectedAccountName)
                        }
                        val nextFragment = TransactionFragment()
                        nextFragment.arguments = bundle
                        findNavController().navigate(R.id.action_recordFragment_to_transactionFragment)
                        dialog.dismiss()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
            Log.d("accountId", accountId.toString())
            Log.d("accountId", accountName.toString())
            dialog.show()
        }


//        dialogSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
//            AdapterView.OnItemSelectedListener {
//
//            override fun onItemClick(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                Log.d("testItem","testing")
//
//            }
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//
//                Log.d("testItem","testing")
//
//                getAccountId()
//
//
//
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//
//
//        }

        // Show the dialog

    }

//    fun getAccountId():LiveData<List<Long>>{
//
//        monetaryAccountViewModel.getAllmAccount.observe(viewLifecycleOwner){
//                data -> monetaryAccountList = data
//        }
//        monetaryAccountViewModel.accountIds.observe(viewLifecycleOwner)
//        {
//                data-> accountId = data
//
//        }
//        monetaryAccountViewModel.accountName.observe(viewLifecycleOwner){
//            data-> accountName = data
//        }
//
//
//        val accountIdList = monetaryAccountList?.map { it.accountId }
//        Log.d("test account Id", accountIdList.toString())
//        return MutableLiveData(accountIdList)
//    }

}