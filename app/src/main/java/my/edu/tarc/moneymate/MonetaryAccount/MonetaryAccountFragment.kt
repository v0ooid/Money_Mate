package my.edu.tarc.moneymate.MonetaryAccount

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.CustomSpinner.ClasslessSpinnerAdapter
import my.edu.tarc.moneymate.CustomSpinner.ClasslessItem
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
    private var icon:Int = 0
    lateinit var overlay: View
    lateinit var overlay2: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonetaryAccountBinding.inflate(inflater, container, false)

        overlay = binding.overlay
        overlay2 = binding.overlay2

        val appDatabase = AppDatabase.getDatabase(requireContext())
        monetaryAccountDao = appDatabase.monetaryAccountDao()

        val navController = findNavController()
        binding.btnBackMAccount.setOnClickListener{
            navController.popBackStack()
        }

        binding.rVmAccount.layoutManager = LinearLayoutManager(requireContext())
        binding.rVmAccount.setHasFixedSize(true)

        adapter = mAccountAdapter(requireContext() ,mAccountViewModel, this)
        recyclerView = binding.rVmAccount
        recyclerView.adapter = adapter

        mAccountViewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            val formattedNumber = String.format("%.2f", total)
            Log.e("Checking", formattedNumber)
            if (total == null){
                binding.tvMonetaryAccount.text = "RM 0.00"
            }
            else {
                binding.tvMonetaryAccount.text = "RM $formattedNumber"
            }
        }

        mAccountViewModel.getAllmAccount.observe(viewLifecycleOwner){
            adapter.setAccount(it)

            if (adapter.itemCount == 0) {
                Log.e("itemCount", adapter.itemCount.toString())
                binding.tvEmptyRecyclerView.visibility = View.VISIBLE
                binding.cardViewTotal.visibility = View.GONE
            } else {
                binding.tvEmptyRecyclerView.visibility = View.GONE
                binding.cardViewTotal.visibility = View.VISIBLE

            }
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

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        val nameTextView = dialog.findViewById<TextView>(R.id.etvAccountName)
        val amountTextView = dialog.findViewById<TextView>(R.id.etvAccountAmount)

        val iconItems = listOf(
            ClasslessItem(R.drawable.malaysian_ringgit_icon, "Cash"),
            ClasslessItem(R.drawable.bank_svgrepo_com__1_, "Bank"),
            ClasslessItem(R.drawable.baseline_credit_card_24, "Card"),
            ClasslessItem(R.drawable.baseline_phone_android_24, "Digital Wallet")
        )

        val spinner: Spinner = dialog.findViewById(R.id.spinnerAddAccountIcon)
        val adapter = ClasslessSpinnerAdapter(requireContext(), iconItems)
        spinner.adapter = adapter

        binding.overlay.visibility = View.VISIBLE
        binding.overlay2.visibility = View.VISIBLE

        val yesBtn = dialog.findViewById<Button>(R.id.btnConfirmAddAccount)
        yesBtn.setOnClickListener {

            val name = nameTextView.text.toString()
            val amount = amountTextView.text

            val decimalRegex = Regex("^\\d+(\\.\\d{2})?\$")

            if (name.isEmpty()) {
                nameTextView.error = "Name cannot be empty"
            } else if (!name.matches(Regex("^[a-zA-Z ]+\$"))) {
                nameTextView.error = "Enter a valid name"
            } else if (amount.isEmpty()){
                amountTextView.error = "Amount cannot be emtpy"
            } else if (amount.toString().toDouble() < 0.1){
                amountTextView.error = "Amount cannot be 0 or less than 0"
            } else if (!amount.matches(decimalRegex))
                amountTextView.error = "Enter a decimal with two decimal places"
            else {
                if (spinner.selectedItemPosition == 0) {
                    icon = R.drawable.malaysian_ringgit_icon
                } else if (spinner.selectedItemPosition == 1) {
                    icon = R.drawable.bank_svgrepo_com__1_
                } else if (spinner.selectedItemPosition == 2) {
                    icon = R.drawable.baseline_credit_card_24
                } else if (spinner.selectedItemPosition == 3) {
                    icon = R.drawable.baseline_phone_android_24
                }

                mAccount = MonetaryAccount(
                    accountName = name,
                    accountBalance = amount.toString().toDouble(),
                    accountIcon = icon
                )
                insertDataToMAccount()
                dialog.dismiss()
                binding.overlay.visibility = View.GONE
                binding.overlay2.visibility = View.GONE
            }

        }

        val noBtn = dialog.findViewById<Button>(R.id.btnCancelAddAccount)
        noBtn.setOnClickListener {
            dialog.dismiss()
            binding.overlay.visibility = View.GONE
            binding.overlay2.visibility = View.GONE
        }

        dialog.show()
    }

    private fun insertDataToMAccount(){
        mAccount?.let { mAccountViewModel.addAccount(it) }
        Toast.makeText(requireContext(), "Successfully Added!", Toast.LENGTH_LONG).show()

    }


}