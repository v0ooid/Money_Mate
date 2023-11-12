package my.edu.tarc.moneymate.Transaction

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentIncomeBinding
import my.edu.tarc.moneymate.databinding.FragmentTransactionBinding
import kotlin.math.abs

class TransactionFragment : Fragment() {

    companion object {
        fun newInstance() = TransactionFragment()
    }

    private val binding get() = _binding!!
    private var _binding: FragmentTransactionBinding? = null

    //    private lateinit var viewModel: TransactionViewModel
    val viewModel: TransactionViewModel by activityViewModels()
    private var currentNumber = ""
    private var firstNumber = ""
    private var result = ""
    private var currentOperator = ""

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
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> navController.navigate(R.id.incomeFragment)
                        1 -> navController.navigate(R.id.expenseFragment)
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
                                //testing part, need to remove
                                Log.w("currentNumber Outside", currentNumber)
                                Log.w("firstNumber Outside", firstNumber)
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

                                    //testing part, need to remove
                                    Log.w("testFirstNumber", firstNumber)
                                    Log.w("testResult", result)
                                    viewModel.result.observe(viewLifecycleOwner, Observer { data ->
                                        Log.w("testviewModel", data)
                                    })

                                    result = ""
                                } else if (currentNumber.isEmpty() && firstNumber.isNotEmpty()) {
                                    result = firstNumber
                                    //store result into transaction ViewModel
                                    result = abs(result.toInt()).toString()
                                    viewModel.updateData(result)

                                    //testing part, need to remove
                                    Log.w("firstNumber inside", firstNumber)
                                    viewModel.result.observe(viewLifecycleOwner, Observer { data ->
                                        Log.w("testviewModel2", data)
                                    })
                                    viewModel.result.removeObservers(viewLifecycleOwner)
                                    //rest result after record
                                    result = ""
                                } else if (firstNumber.isNullOrEmpty()) {
                                    Toast.makeText(context, "Value cant be 0", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                result = abs(result.toInt()).toString()
                                viewModel.updateData(result)
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
    }

}