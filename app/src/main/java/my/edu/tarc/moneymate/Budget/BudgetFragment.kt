package my.edu.tarc.moneymate.Budget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.AppLock.AppLock4DigitFragment
import my.edu.tarc.moneymate.AppLock.AppLock6DigitFragment
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.CustomSpinner.ClassSpinnerAdapter
import my.edu.tarc.moneymate.Database.AppDatabase
import my.edu.tarc.moneymate.Database.BudgetDao
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentBudgetBinding

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: BudgetAdapter
    private lateinit var budgetDao: BudgetDao

    private val budgetViewModel: BudgetViewModel by activityViewModels()
    private var budget: Budget? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)

        val sharedPreferences =
            requireContext().getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)

        val sectionPreferences =
            requireContext().getSharedPreferences("APP_BUDGET_PREFS", Context.MODE_PRIVATE)

        val lockEn = sectionPreferences.getBoolean("Enabled", false)

        val lockStatus = sectionPreferences.getBoolean("Locked", false)

        if (lockEn) {
            if (lockStatus) {
                val appLockType = sharedPreferences.getString("SECURITY_TYPE_KEY", "")
                if (appLockType == "4Digit") {
                    val fragment = AppLock4DigitFragment()
                    val bundle = Bundle()
                    bundle.putString("FRAGMENT_LOCK", "BUDGET_FRAGMENT")
                    fragment.arguments = bundle

                    val navController = findNavController()
                    navController.navigate(
                        R.id.action_budgetFragment_to_appLock4DigitFragment,
                        bundle
                    )
                } else if (appLockType == "6Digit") {
                    val fragment = AppLock6DigitFragment()
                    val bundle = Bundle()
                    bundle.putString("FRAGMENT_LOCK", "BUDGET_FRAGMENT")
                    fragment.arguments = bundle

                    val navController = findNavController()
                    navController.navigate(
                        R.id.action_budgetFragment_to_appLock6DigitFragment,
                        bundle
                    )
                } else {
                    val fragment = AppLock6DigitFragment()
                    val bundle = Bundle()
                    bundle.putString("FRAGMENT_LOCK", "BUDGET_FRAGMENT")
                    fragment.arguments = bundle

                    val navController = findNavController()
                    navController.navigate(
                        R.id.action_budgetFragment_to_appLockCustomPasswordFragment,
                        bundle
                    )
                }
            } else {
                runRemainingBudgetFragmentLogic()
            }
        } else {
            runRemainingBudgetFragmentLogic()
        }
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)
    }

    private fun runRemainingBudgetFragmentLogic(){
        val appDatabase = AppDatabase.getDatabase(requireContext())
        budgetDao = appDatabase.budgetDao()

        binding.rvBudget.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudget.setHasFixedSize(true)

        adapter = BudgetAdapter(requireContext(), budgetViewModel)
        recyclerView = binding.rvBudget
        recyclerView.adapter = adapter

        budgetViewModel.getAllBudget.observe(viewLifecycleOwner){
            adapter.setBudget(it)

            if (adapter.itemCount == 0) {
                Log.e("itemCount", adapter.itemCount.toString())
                binding.tvEmptyRecyclerView.visibility = View.VISIBLE
            } else {
                binding.tvEmptyRecyclerView.visibility = View.GONE
            }
        }

        budgetViewModel.getExpenseCategory.observe(viewLifecycleOwner){
            adapter.setCategory(it)
        }



        binding.fabBudgetAdd.setOnClickListener{
            showDialog()
        }
    }

    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_add_budget_dialog, null)
        val overlayView = layoutInflater.inflate(R.layout.dark_overlay, null)

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)

        val overlayLayout = overlayView.findViewById<FrameLayout>(R.id.overlayLayout)

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        val nameTextView = dialog.findViewById<TextView>(R.id.eTBudgetName)
        val limitTextView = dialog.findViewById<TextView>(R.id.eTBudgetLimit)

        val spinner: Spinner = dialog.findViewById(R.id.sBudgetCategory)

        budgetViewModel.getExpenseCategory.observe(viewLifecycleOwner) { categories ->
            val adapter = ClassSpinnerAdapter(requireContext(), categories,
                { category -> category.image },
                { category -> category.title }
            )
            spinner.adapter = adapter
        }

        val yesBtn = dialog.findViewById<Button>(R.id.btnBudgetConfrim)
        yesBtn.setOnClickListener {

            val name = nameTextView.text.toString()
            val limit = limitTextView.text
            val selectedCate = spinner.selectedItem as Category

            val decimalRegex = Regex("^\\d+(\\.\\d{2})?\$")

            if (name.isEmpty()) {
                nameTextView.error = "Name cannot be empty"
            } else if (!name.matches(Regex("^[a-zA-Z ]+\$"))) {
                nameTextView.error = "Enter a valid name"
            } else if (limit.isEmpty()) {
                limitTextView.error = "Amount cannot be empty"
            } else if (limit.toString().toDouble() < 0.1) {
                limitTextView.error = "Amount cannot be 0 or less than 0"
            } else if (!limit.matches(decimalRegex))
                limitTextView.error = "Enter a decimal with two decimal places"
            else {

                budget = Budget(
                    budgetIcon = selectedCate.image,
                    budgetName = name,
                    budgetLimit = limit.toString().toDouble(),
                    budgetSpent = 0.0,
                    categoryId = selectedCate.categoryId
                )
                insertDataToBudget()
                dialog.dismiss()
                overlayLayout.visibility = View.GONE

            }
        }

        val noBtn = dialog.findViewById<Button>(R.id.btnBudgetCancel)
        noBtn.setOnClickListener {
            dialog.dismiss()
            overlayLayout.visibility = View.GONE

        }

        val parentLayout = requireActivity().findViewById<ViewGroup>(android.R.id.content)
        parentLayout.addView(overlayView)

        dialog.show()
        dialog.setOnDismissListener {
            parentLayout.removeView(overlayView)
        }
    }

    private fun insertDataToBudget(){
        budget?.let { budgetViewModel.addBudget(it) }
        Toast.makeText(requireContext(), "Successfully Added!", Toast.LENGTH_LONG).show()

    }

}