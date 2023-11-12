package my.edu.tarc.moneymate.MonetaryAccount

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.CustomSpinner.IconAdapter
import my.edu.tarc.moneymate.CustomSpinner.IconItem
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.MonetaryAccountDao
//import my.edu.tarc.moneymate.CustomSpinner.IconAdapter
//import my.edu.tarc.moneymate.CustomSpinner.IconItem
//import my.edu.tarc.moneymate.Database.AppDatabase
//import my.edu.tarc.moneymate.Database.MonetaryAccountDao
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentMonetaryAccountBinding

class MonetaryAccountFragment : Fragment() {

    private var _binding: FragmentMonetaryAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: mAccountAdapter
    private lateinit var monetaryAccountDao: MonetaryAccountDao

    private val mAccountViewModel: MonetaryAccountViewModel by activityViewModels()
    private var mAccount: MonetaryAccount? = null
    private var icon = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonetaryAccountBinding.inflate(inflater, container, false)

        val appDatabase = AppDatabase.getDatabase(requireContext())
        monetaryAccountDao = appDatabase.monetaryAccountDao()

        binding.rVmAccount.layoutManager = LinearLayoutManager(requireContext())
        binding.rVmAccount.setHasFixedSize(true)

        adapter = mAccountAdapter(requireContext() ,mAccountViewModel)
        recyclerView = binding.rVmAccount
        recyclerView.adapter = adapter

        mAccountViewModel.getAllmAccount.observe(viewLifecycleOwner){
            Log.e("CHECKING", it.toString())
            adapter.setAccount(it)
        }

        binding.fabAddAccount.setOnClickListener{
            showDialog()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(MonetaryAccountViewModel::class.java)
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_add_monetary_account_dialog)

        val nameTextView = dialog.findViewById<TextView>(R.id.etvAccountName)

        val amountTextView = dialog.findViewById<TextView>(R.id.etvAccountAmount)

        val iconItems = listOf(
            IconItem(R.drawable.baseline_attach_money_24),
            IconItem(R.drawable.baseline_credit_card_24),
            IconItem(R.drawable.baseline_phone_android_24),
            // Add more items as needed
        )

        val spinner: Spinner = dialog.findViewById(R.id.spinnerAddAccountIcon)
        val adapter = IconAdapter(requireContext(), iconItems)
        spinner.adapter = adapter

        val yesBtn = dialog.findViewById<Button>(R.id.btnConfirmAddAccount)
        yesBtn.setOnClickListener {

            val name = nameTextView.text.toString()
            val amount = amountTextView.text.toString().toDouble()

            if (spinner.selectedItemPosition == 0){
                icon = "baseline_attach_money_24"
            } else if (spinner.selectedItemPosition == 1){
                icon = "baseline_credit_card_24"
            } else if (spinner.selectedItemPosition == 2){
                icon = "baseline_phone_android_24"

            }

            mAccount = MonetaryAccount(
                accountName = name,
                accountBalance = amount,
                accountIcon = icon
            )
            insertDataToMAccount()
            dialog.dismiss()
        }

        val noBtn = dialog.findViewById<Button>(R.id.btnCancelAddAccount)
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun insertDataToMAccount(){
        mAccount?.let { mAccountViewModel.addAccount(it) }
        Toast.makeText(requireContext(), "Successfully Added!", Toast.LENGTH_LONG).show()

    }


}