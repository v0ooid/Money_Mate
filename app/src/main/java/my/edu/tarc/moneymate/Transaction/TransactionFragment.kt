package my.edu.tarc.moneymate.Transaction

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import my.edu.tarc.moneymate.Budget.BudgetViewModel
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Expense.ExpenseViewModel
import my.edu.tarc.moneymate.Gamification.GamificationHelper
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Income.IncomeViewModel
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transfer.Transfer
import my.edu.tarc.moneymate.Transfer.TransferViewModel
import my.edu.tarc.moneymate.databinding.FragmentIncomeBinding
import my.edu.tarc.moneymate.databinding.FragmentTransactionBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class TransactionFragment : Fragment() {

    companion object {
        fun newInstance() = TransactionFragment()
    }

    private val binding get() = _binding!!
    private var _binding: FragmentTransactionBinding? = null

    //    private lateinit var viewModel: TransactionViewModel
    val viewModel: TransactionViewModel by activityViewModels()
    val incomeViewModel: IncomeViewModel by activityViewModels()
    val expenseViewModel : ExpenseViewModel by activityViewModels()
    val transferViewModel : TransferViewModel by activityViewModels()
    val budgetViewModel : BudgetViewModel by activityViewModels()

    private var currentNumber = ""
    private var firstNumber = ""
    private var result = ""
    private var currentOperator = ""
    private var transactionTypeSelected = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
        //return inflater.inflate(R.layout.fragment_transaction, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout: TabLayout = binding.transactiontab
        val navHostTransactionFragment =
            childFragmentManager.findFragmentById(R.id.transactionfragmentContainerView) as NavHostFragment
        val navController = navHostTransactionFragment.navController
        tabLayout.getTabAt(0)?.select()
        viewModel.updateTransactionType("income")

//        binding.expandCal.setOnClickListener {
//            if (binding.cardDesc.visibility == View.GONE) {
//                binding.cardDesc.visibility = View.VISIBLE
//            }
//            else if (binding.cardDesc.visibility == View.VISIBLE) {
//                binding.cardDesc.visibility = View.GONE
//            }
//        }



        binding.leftIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> {
                            Log.d("test Tab 1",it.position.toString())
                            navController.navigate(R.id.incomeFragment)
                            viewModel.updateTransactionType("income")
                        }
                        1 -> {
                            Log.d("test Tab 2",it.position.toString())
                            navController.navigate(R.id.expenseFragment)
                            viewModel.updateTransactionType("expense")
                        }
                        2 -> {
                            navController.navigate(R.id.transferFragment)
                            viewModel.updateTransactionType("transfer")
                        }
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        binding.apply {
            binding.layoutCal.children.filterIsInstance<Button>().forEach { button ->
                button.setOnClickListener {
                    val buttontext = button.text.toString()
                    when {
                        buttontext.matches(Regex(pattern = "[0-9]")) -> {
                            if (currentOperator.isEmpty()) {

                                binding.amountCalDelete.setOnClickListener {
                                    if (binding.amountCal.text.toString().length > 1) {
                                        binding.amountCal.text = binding.amountCal.text.dropLast(1)
                                        firstNumber = binding.amountCal.text.toString()
                                    } else {
                                        binding.amountCal.text = "0"
                                        firstNumber = ""
                                    }

                                }
                                firstNumber += buttontext
                                amountCal.text = firstNumber
                            } else {
                                binding.amountCalDelete.setOnClickListener {
                                    if (binding.amountCal.text.toString().length > 1) {

                                        binding.amountCal.text = binding.amountCal.text.dropLast(1)
                                        currentNumber = binding.amountCal.text.toString()
                                    } else {
                                        binding.amountCal.text = "0"
                                        currentNumber = binding.amountCal.text.toString()
                                    }
                                }
                                currentNumber += buttontext
                                amountCal.text = currentNumber
                            }
                        }

                        buttontext.matches(Regex(pattern = "[+\\-*/]")) -> {
                            currentNumber = ""
                            if (amountCal.text.toString().isNotEmpty()) {
                                currentOperator = buttontext
                                amountCal.text = "0"
                                if (currentNumber.isNotEmpty() && currentOperator.isNotEmpty()) {
                                    result =
                                        evaluateExpression(
                                            firstNumber,
                                            currentNumber,
                                            currentOperator
                                        )
                                    firstNumber = result
                                    amountCal.text = result
                                    currentNumber = ""
                                }
                            }

                        }

                        buttontext == "=" -> {
                            if (currentNumber.isNotEmpty() && currentOperator.isNotEmpty()) {
                                result =
                                    evaluateExpression(firstNumber, currentNumber, currentOperator)
                                firstNumber = result
                                amountCal.text = result
                                if (result.toInt() < 0)
                                    Toast.makeText(
                                        context,
                                        "The value will be recorded in positive value",
                                        Toast.LENGTH_LONG
                                    ).show()
                            }
                        }

                        buttontext == "Enter" -> {
                            Log.w("Outside", result)
                            if (result.isEmpty()) {
                                if (currentNumber.isNotEmpty() && currentOperator.isNotEmpty()) {
                                    result =
                                        evaluateExpression(
                                            firstNumber,
                                            currentNumber,
                                            currentOperator
                                        )
                                    if (result.toInt() < 0)
                                        Toast.makeText(
                                            context,
                                            "The value will be recorded in positive value",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    //store result into transaction ViewModel
                                    result = abs(result.toInt()).toString()
                                    viewModel.updateData(result)

                                    //save description
                                    val description = binding.transactionDescription.text.toString()
                                    Log.d("description", description)
                                    viewModel.updateTransactionDescription(description)
                                    addRecordIntoDatabase()
                                    result = ""

                                    Toast.makeText(
                                        requireContext(),
                                        "Added Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigateUp()

                                } else if (currentNumber.isEmpty() && firstNumber.isNotEmpty()) {
                                    result = firstNumber
                                    //store result into transaction ViewModel
                                    result = abs(result.toInt()).toString()
                                    viewModel.updateData(result)
                                    //save description
                                    val description = binding.transactionDescription.text.toString()
                                    viewModel.updateTransactionDescription(description)

                                    addRecordIntoDatabase()
                                    //rest result after record
                                    result = ""
                                    Toast.makeText(
                                        requireContext(),
                                        "Added Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigateUp()
                                } else if (firstNumber.isNullOrEmpty()) {
                                    Toast.makeText(context, "Value cant be 0", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                result = abs(result.toInt()).toString()
                                viewModel.updateData(result)
                                //Save Description
                                val description = binding.transactionDescription.text.toString()
                                viewModel.updateTransactionDescription(description)
                                addRecordIntoDatabase()
                                Toast.makeText(
                                    requireContext(),
                                    "Added Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                findNavController().navigateUp()
                            }
                        }
                    }
                }
            }
        }
        binding.amountCalDelete.setOnClickListener {
            binding.amountCal.text = binding.amountCal.text.dropLast(1)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun getCurrentTime():String
    {
        val currentDateTime= ZonedDateTime.now(ZoneId.of("GMT+8"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return currentDateTime.format(formatter)
    }


    private fun addRecordIntoDatabase(){
        var description = ""
        var acccount = ""
        var toAccount = ""
//        var acccount = 0
        var title = ""
        var result = ""
        var transactionTypeSelected = ""
        var categoryId = ""
//        var categoryId = 0
        var categoryImage:Int = 0
        val date = getCurrentTime()
        viewModel.transactionType.observe(viewLifecycleOwner){
            data -> transactionTypeSelected = data
        }
        viewModel.transactionDescription.observe(viewLifecycleOwner) { data ->
            description = data
        }
        viewModel.title.observe(viewLifecycleOwner) { data ->
            title = data
        }
        viewModel.selectedAccount.observe(viewLifecycleOwner) { data ->
            acccount = data
        }
        viewModel.toAccount.observe(viewLifecycleOwner){ data ->
            toAccount = data
        }
//        account = arguments?.getLong("selectedAccountId").toString()
//        val selectedAccountId = arguments?.getLong("selectedAccountId")
//        Log.d("testing bundle", selectedAccountId.toString())
        viewModel.result.observe(viewLifecycleOwner) { data ->
            result = String.format("%02d",data.toInt())
        }
        viewModel.categoryId.observe(viewLifecycleOwner){data ->
            categoryId = data
        }
        viewModel.categoryImage.observe(viewLifecycleOwner){data->
            categoryImage = data
        }

        if (transactionTypeSelected == "income") {
            val income = Income(0, title,categoryImage,description, result.toInt(),categoryId.toLong(),acccount.toLong(),date.toString())
            incomeViewModel.addIncome(income)
            onIncomeRecorded()
        }
        else if (transactionTypeSelected == "expense")
        {
            val expense = Expense(0, title,categoryImage,description, result.toInt(),categoryId.toLong(),acccount.toLong(),date.toString())
            expenseViewModel.addExpense(expense)
            budgetViewModel.updateBudgetWithExpense(expense)
            onExpenseRecorded()
        }
        else if (transactionTypeSelected == "transfer")
        {
            val transfer = Transfer(0, title, description, result.toInt(), acccount.toLong(), toAccount.toLong(), date.toString())
            transferViewModel.addTransfer(transfer, acccount.toLong(), toAccount.toLong(), result.toInt())

        }
    }

    fun onIncomeRecorded() {
        val sharedPreferences = requireContext().getSharedPreferences("GamificationPref", Context.MODE_PRIVATE)
        val recordedIncomes = sharedPreferences.getInt("RecordedIncomes", 0)

        if (recordedIncomes < 5){
            // Increment the count of recorded incomes
            sharedPreferences.edit {
                putInt("RecordedIncomes", recordedIncomes + 1)
            }

            // Check if the user has recorded 5 incomes
            val updatedRecordedIncomes = sharedPreferences.getInt("RecordedIncomes", 0)
            if (updatedRecordedIncomes >= 5) {
                GamificationHelper.checkTasksAndLevelUp(sharedPreferences, requireContext())

                Toast.makeText(requireContext(), "Nice! You have recorded 5 Incomes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onExpenseRecorded() {
        val sharedPreferences = requireContext().getSharedPreferences("GamificationPref", Context.MODE_PRIVATE)
        val recordedExpenses = sharedPreferences.getInt("RecordedExpenses", 0)

        Log.e("recordedExpenses", recordedExpenses.toString())

        if (recordedExpenses < 5){
            // Increment the count of recorded expenses
            sharedPreferences.edit {
                putInt("RecordedExpenses", recordedExpenses + 1)
            }

            val twoExpenses = sharedPreferences.getInt("RecordedExpenses", 0)

            Log.e("RecordedExpenses", twoExpenses.toString())

            // Check if the user has recorded 5 expenses
            val updatedRecordedExpenses = sharedPreferences.getInt("RecordedExpenses", 0)
            if (updatedRecordedExpenses >= 5) {
                GamificationHelper.checkTasksAndLevelUp(sharedPreferences, requireContext())

                Toast.makeText(requireContext(), "Nice! You have recorded 5 Expenses", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun evaluateExpression(
        firstNumber: String,
        secondNumber: String,
        operator: String
    ): String {
        val num1 = firstNumber.toInt()
        val num2 = secondNumber.toInt()
        return when (operator) {
            "+" -> (num1 + num2).toString()
            "-" -> (num1 - num2).toString()
            "*" -> (num1 * num2).toString()
            else -> ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.result.removeObservers(viewLifecycleOwner)
        viewModel.transactionType.removeObservers(viewLifecycleOwner)
        viewModel.selectedAccount.removeObservers(viewLifecycleOwner)
        viewModel.transactionDescription.removeObservers(viewLifecycleOwner)
    }

}