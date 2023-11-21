package my.edu.tarc.moneymate.Record

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
import android.widget.Spinner
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Income.IncomeAdapter
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transaction.TransactionFragment
import my.edu.tarc.moneymate.Transaction.TransactionViewModel
import my.edu.tarc.moneymate.databinding.FragmentIncomeBinding
import my.edu.tarc.moneymate.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding ?= null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var RecordAdapter: RecordAdapter
    private var categorylist_income = mutableListOf<Income>()
    //val transactionViewModel: TransactionViewModel by activityViewModels()
    val transactionViewModel: TransactionViewModel by activityViewModels()

    companion object {
        fun newInstance() = RecordFragment()
    }

    private lateinit var viewModel: RecordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabTransactionAdd.setOnClickListener{
           // findNavController().navigate(R.id.action_recordFragment_to_transactionFragment)
            showCustomDialog()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RecordViewModel::class.java)
        // TODO: Use the ViewModel
    }

    fun showCustomDialog() {
        // Inflate the custom layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.choose_account_dialog, null)
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

        dialogSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            val bundle = Bundle()

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("testItem","testing")

            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                Log.d("testItem","testing")
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem != "Account") {
                    Log.d("Account Choosed", selectedItem)
                    transactionViewModel.updateSelectedAccountData(selectedItem)
                    bundle.apply {
                        putString("selectedAccount", selectedItem)
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

        // Show the dialog
        dialog.show()
    }

}