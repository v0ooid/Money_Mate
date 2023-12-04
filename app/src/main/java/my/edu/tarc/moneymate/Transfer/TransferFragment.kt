package my.edu.tarc.moneymate.Transfer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import my.edu.tarc.moneymate.CustomSpinner.ClassSpinnerAdapter
import my.edu.tarc.moneymate.DataExport.DataExportViewModel
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transaction.TransactionViewModel
import my.edu.tarc.moneymate.databinding.FragmentTransactionBinding
import my.edu.tarc.moneymate.databinding.FragmentTransferBinding

class TransferFragment : Fragment() {

    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!
    private val transferViewModel: TransferViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransferBinding.inflate(inflater, container, false)

        val fromAccountEditText: TextView = binding.tvAccountFrom
        val fromAccountImage: ImageView = binding.iVAccountFrom
        val toAccountSpinner: Spinner = binding.spinnerTo

        transactionViewModel.selectedAccount.observe(viewLifecycleOwner) { accountId ->
            // Use transactionViewModel to get account details
            transferViewModel.getAccountNameById(accountId.toLong()).observe(viewLifecycleOwner) { accountDetails ->
                // Use the retrieved accountName and accountIcon here
                fromAccountEditText.setText(accountDetails.accountName)

                fromAccountImage.setImageResource(accountDetails.accountIcon)
            }
        }

        transactionViewModel.selectedAccount.observe(viewLifecycleOwner) { selectedAccountId ->
            val accountIdAsLong = selectedAccountId.toLongOrNull() // Convert String to Long or null if conversion fails

            transferViewModel.getAllMAccount.observe(viewLifecycleOwner) { accounts ->
                // Filter out the selected account from the list
                val filteredAccounts = accountIdAsLong?.let { accountId ->
                    accounts.filter { it.accountId != accountId }
                } ?: accounts // Use all accounts if accountIdAsLong is null (conversion failed)

                val adapter = ClassSpinnerAdapter(requireContext(), filteredAccounts,
                    { account -> account.accountIcon },
                    { account -> account.accountName }
                )
                toAccountSpinner.adapter = adapter
            }
        }

        toAccountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as? MonetaryAccount
                selectedItem?.let {
                    // Handle the selected MonetaryAccount
                    val selectedAccountId = it.accountId

                    transactionViewModel.updateToAccountData(selectedAccountId.toString())

                    // Perform actions with the selected account data
                    // For example, log the selected account details
                    Log.d("SelectedAccount", "ID: $selectedAccountId")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        transactionViewModel.result.removeObservers(viewLifecycleOwner)
        transactionViewModel.title.removeObservers(viewLifecycleOwner)
        transactionViewModel.transactionDescription.removeObservers(viewLifecycleOwner)
        transactionViewModel.selectedAccount.removeObservers(viewLifecycleOwner)
        transactionViewModel.transactionType.removeObservers(viewLifecycleOwner)
        transactionViewModel.toAccount.removeObservers(viewLifecycleOwner)
    }


}