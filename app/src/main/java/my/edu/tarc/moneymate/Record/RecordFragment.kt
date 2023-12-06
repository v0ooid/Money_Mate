package my.edu.tarc.moneymate.Record

import android.annotation.SuppressLint
import android.app.AlertDialog

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Budget.BudgetViewModel
import my.edu.tarc.moneymate.Expense.ExpenseViewModel
import my.edu.tarc.moneymate.FinancialAdvisor.FinancialAdvisorViewModel
import my.edu.tarc.moneymate.FinancialAdvisor.FinancialHealthStatus
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Income.IncomeViewModel
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccountViewModel
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transaction.TransactionFragment
import my.edu.tarc.moneymate.Transaction.TransactionViewModel
import my.edu.tarc.moneymate.Transfer.TransferViewModel
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
    private lateinit var financialAdvisorViewModel: FinancialAdvisorViewModel

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

        // Initialize FinancialAdvisorViewModel
        val factory = FinancialAdvisorViewModel.Factory(requireActivity().application)
        financialAdvisorViewModel =
            ViewModelProvider(this, factory).get(FinancialAdvisorViewModel::class.java)

        financialAdvisorViewModel.accountsFinancialHealth.observe(viewLifecycleOwner){
            data -> data.forEach{
                accountHealth ->
            if (accountHealth.status == FinancialHealthStatus.ATTENTION || accountHealth.status == FinancialHealthStatus.DANGER) {
                val message = "Account: ${accountHealth.accountName}\nStatus: ${accountHealth.status}\nTips: ${accountHealth.financialTips.joinToString(" ")}"
                financialAdvisorViewModel.showNotification("Financial Health Alert", message)
            }
        }

        }


//        monetaryAccountViewModel.getAllmAccount.observe(viewLifecycleOwner) { data ->
//            val accountId = data.map { it.accountId }
//            Log.e("Record Fragment", "On ViewCreated $accountId")
//            accountId.forEach { accountId ->
//                financialAdvisorViewModel.checkAndNotifyAccountStatus(accountId)
//                Log.e("Record Fragment","Inside $accountId")
//            }
//        }
//        financialAdvisorViewModel.accountsFinancialHealth.observe(viewLifecycleOwner) { accountsHealth ->
//            accountsHealth.forEach { accountHealth ->
//                Log.d("Fragmetn accoutn heal", accountHealth.toString())
//            }
//        }


    }

    private fun setupRecyclerViews() {
        incomerecyclerView.layoutManager = LinearLayoutManager(requireContext())
        RecordAdapter = RecordAdapter(
            requireContext(),
            incomeViewModel,
            recordViewModel,
            this,
            mutableListOf(),
            recordViewModel.monetaryAccountDao
        )
        incomerecyclerView.adapter = RecordAdapter
        expenserecyclerView.layoutManager = LinearLayoutManager(requireContext())
        RecordExpenseAdapter = RecordExpenseAdapter(
            requireContext(),
            expenseViewModel,
            recordViewModel,
            budgetViewModel,this,
            mutableListOf()
        )
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
            Log.d("Record Fragment", "observeRecordData$data.")
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

        monetaryAccountViewModel.accountIdsandName.observe(viewLifecycleOwner) { data ->
            val accountIdInside = data.map { it.first }
            val accountNameInside = data.map { it.second }

            val adapterItem = listOf("Choose An Account") + accountNameInside
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
                    if (selectedItem != "Choose An Account") {
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
            Log.e("Record Fragment", "Show custom dialog $accountId and $accountName")
            dialog.show()
        }
    }

}