package my.edu.tarc.moneymate.DataExport

import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.CustomSpinner.ClassSpinnerAdapter
import my.edu.tarc.moneymate.CustomSpinner.ClasslessItem
import my.edu.tarc.moneymate.CustomSpinner.ClasslessSpinnerAdapter
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentDataExportBinding
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DataExportFragment : Fragment() {

    private var _binding: FragmentDataExportBinding? = null
    private val binding get() = _binding!!
    private val dataExportViewModel: DataExportViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDataExportBinding.inflate(inflater, container, false)

        val navController = findNavController()
        binding.btnBackMAccount.setOnClickListener {
            navController.popBackStack()
        }

        val spinnerMAccount: Spinner = binding.sDEMAccount
        dataExportViewModel.getAllMAccount.observe(viewLifecycleOwner) { accounts ->
            val adapter = ClassSpinnerAdapter(requireContext(), accounts,
                { accounts -> accounts.accountIcon },
                { accounts -> accounts.accountName }
            )
            spinnerMAccount.adapter = adapter
        }

        val transactionType = listOf(
            ClasslessItem(R.drawable.baseline_arrow_upward_24, "Income"),
            ClasslessItem(R.drawable.baseline_arrow_downward_24, "Expenses"),
            ClasslessItem(R.drawable.baseline_compare_arrows_24, "Transfer"),
        )

        val spinnerCategory: Spinner = binding.sDECategory

        val spinnerTransaction: Spinner = binding.sDETransactionType

        spinnerTransaction.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position) as? ClasslessItem
                if (selectedItem != null) {
                    if (selectedItem.title == "Income") {
                        dataExportViewModel.getAllIncomeCategories()
                            .observe(viewLifecycleOwner) { categories ->
                                // Ensure categories is not null and not empty before setting the adapter
                                if (categories != null && categories.isNotEmpty()) {
                                    val adapter = ClassSpinnerAdapter(
                                        requireContext(),
                                        categories,
                                        { category -> category.image },
                                        { category -> category.title }
                                    )
                                    Log.e("Adapter", adapter.toString())
                                    spinnerCategory.adapter = adapter
                                } else {
                                    // Handle empty or null category list if needed
                                    Log.e("Adapter2", "Categories list is empty or null")
                                }
                            }
                    } else if (selectedItem.title == "Expense"){
                        dataExportViewModel.getAllExpensesCategories()
                            .observe(viewLifecycleOwner) { categories ->
                                // Ensure categories is not null and not empty before setting the adapter
                                if (categories != null && categories.isNotEmpty()) {
                                    val adapter = ClassSpinnerAdapter(
                                        requireContext(),
                                        categories,
                                        { category -> category.image },
                                        { category -> category.title }
                                    )
                                    Log.e("Adapter", adapter.toString())
                                    spinnerCategory.adapter = adapter
                                } else {
                                    // Handle empty or null category list if needed
                                    Log.e("Adapter2", "Categories list is empty or null")
                                }
                            }
                    } else {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected in the spinner
            }
        }

        val adapterTransaction = ClasslessSpinnerAdapter(requireContext(), transactionType)
        spinnerTransaction.adapter = adapterTransaction

        val tvStartDate: TextView = binding.tvStartDate
        val tvEndDate: TextView = binding.tvEndDate

        val currentDate = Calendar.getInstance()
        tvStartDate.text = formatDate(currentDate)
        tvEndDate.text = formatDate(currentDate)

        val etDateStart: ConstraintLayout = binding.etDateStart
        val etDateEnd: ConstraintLayout = binding.etDateEnd

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


        etDateStart.setOnClickListener {
            showDatePickerDialog(tvStartDate, currentDate)
        }

        // Set OnClickListener for end date selection
        etDateEnd.setOnClickListener {
            val startDateString = tvStartDate.text.toString()
            val startDate = dateFormat.parse(startDateString)

            val minDateCalendar = Calendar.getInstance().apply {
                time = startDate ?: Date()
            }

            showDatePickerDialog(tvEndDate, currentDate, minDateCalendar)
        }

        val fileType = listOf(
            ClasslessItem(R.drawable.csv_file_type_svgrepo_com, "CSV"),
            ClasslessItem(R.drawable.json_svgrepo_com, "JSON")
        )
        val spinnerFileType: Spinner = binding.sDEFileType
        val adapterFileType = ClasslessSpinnerAdapter(requireContext(), fileType)
        spinnerFileType.adapter = adapterFileType

        binding.btnExport.setOnClickListener {
            val selectedAccount = binding.sDEMAccount.selectedItem as MonetaryAccount
            val selectedTransaction = binding.sDETransactionType.selectedItem as ClasslessItem
            val selectedCategory = binding.sDECategory.selectedItem as Category
            val startDate = binding.tvStartDate.text.toString()
            val endDate = binding.tvEndDate.text.toString()
            val selectedFileType = binding.sDEFileType.selectedItem as ClasslessItem

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            Log.e("selectedAccount", selectedAccount.accountId.toString())
            Log.e("selectedTransaction", selectedTransaction.toString())
            Log.e("selectedCategory", selectedCategory.categoryId.toString())
            Log.e("startDate", startDate.toString())
            Log.e("endDate", endDate.toString())


            if (selectedTransaction.title == "Income") {
                dataExportViewModel.fetchIncomeByCriteria(
                    selectedAccount.accountId,
                    selectedCategory.categoryId
//                    dateFormat.parse(startDate),
//                    dateFormat.parse(endDate),
                )
                dataExportViewModel.incomeLiveData.observe(viewLifecycleOwner) { incomeList ->
                    when (selectedFileType.title){
                        "CSV" -> generateIncomeCSVFile(incomeList)
//                        "JSON" -> generateIncomeJSONFile(incomeList)
                        else -> null
                    }
                }
            } else if (selectedTransaction.title == "Expenses") {
//                dataExportViewModel.fetchExpenseByCriteria(
//                    selectedAccount.accountId,
//                    selectedCategory.categoryId,
//                    dateFormat.parse(startDate),
//                    dateFormat.parse(endDate),
//                )
                dataExportViewModel.expenseLiveData.observe(viewLifecycleOwner) { expensesList ->
                    // Handle expenses list and export it to CSV or JSON
                    when (selectedFileType.title){
//                        "CSV" -> generateExpenseCSVFile(expensesList)
//                        "JSON" -> generateExpenseJSONFile(expensesList)
                        else -> null
                    }
                }
            }
            else{
            }
        }
        return binding.root
    }

    //Original
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun generateIncomeCSVFile(
        incomeList: List<Income>
    ): Uri? {
        val csvHeader =
            "ACCOUNT, TRANSACTION TYPE, TITLE, CATEGORY, AMOUNT, DATE\n" // Header of the CSV file

        val csvContent = StringBuilder()
        csvContent.append(csvHeader)

        Log.e("incomeList", incomeList.toString())

//         Iterate through the income list and append data to the CSV content
        for (income in incomeList) {

//            val accountName = income.accountName
            val title = income.incomeTitle
//            val category = income.title
            val amount = income.amount.toString()
            val date = income.date.toString()

            val row = "Income,$title,$amount,$date\n"
            csvContent.append(row)
        }

        // Write the CSV content to a file
        val fileName = "income_data.csv"
        val fileContent = csvContent.toString().toByteArray()

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { fileUri ->
            resolver.openOutputStream(fileUri)?.use { outputStream ->
                outputStream.write(fileContent)
            }
        }

        return uri
    }

//    @RequiresApi(Build.VERSION_CODES.Q)
//    private fun generateExpenseCSVFile(expenseList: List<Expense>): Uri? {
//        val csvHeader =
//            "ACCOUNT, TRANSACTION TYPE, TITLE, CATEGORY, AMOUNT, DATE\n" // Header of the CSV file
//
//        val csvContent = StringBuilder()
//        csvContent.append(csvHeader)
//
//        // Iterate through the income list and append data to the CSV content
//        for (expense in expenseList) {
//            val title = expense.title
//            val amount = expense.amount.toString()
//            val date = expense.date.toString()
//
//            val row = "$title,$amount,$date\n"
//            csvContent.append(row)
//        }
//
//        // Write the CSV content to a file
//        val fileName = "expense_data.csv"
//        val fileContent = csvContent.toString().toByteArray()
//
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
//        }
//
//        val resolver = requireContext().contentResolver
//        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
//
//        uri?.let { fileUri ->
//            resolver.openOutputStream(fileUri)?.use { outputStream ->
//                outputStream.write(fileContent)
//            }
//        }
//
//        return uri
//    }

//    @RequiresApi(Build.VERSION_CODES.Q)
//    private fun generateIncomeJSONFile(incomeList: List<Income>): Uri? {
//        val jsonArray = JSONArray()
//
//        // Iterate through the income list and create JSON objects
//        for (income in incomeList) {
//            val jsonObject = JSONObject()
//            jsonObject.put("title", income.title)
//            jsonObject.put("amount", income.amount)
//            jsonObject.put("date", income.date.toString())
//
//            jsonArray.put(jsonObject)
//        }
//
//        // Convert the JSON array to a string
//        val jsonString = jsonArray.toString()
//
//        // Write the JSON string to a file
//        val fileName = "income_data.json"
//        val fileContent = jsonString.toByteArray()
//
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
//        }
//
//        val resolver = requireContext().contentResolver
//        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
//
//        uri?.let { fileUri ->
//            resolver.openOutputStream(fileUri)?.use { outputStream ->
//                outputStream.write(fileContent)
//            }
//        }
//
//        return uri
//    }

//    @RequiresApi(Build.VERSION_CODES.Q)
//    private fun generateExpenseJSONFile(incomeList: List<Expense>): Uri? {
//        val jsonArray = JSONArray()
//
//        // Iterate through the income list and create JSON objects
//        for (income in incomeList) {
//            val jsonObject = JSONObject()
//            jsonObject.put("title", income.title)
//            jsonObject.put("amount", income.amount)
//            jsonObject.put("date", income.date.toString())
//
//            jsonArray.put(jsonObject)
//        }
//
//        // Convert the JSON array to a string
//        val jsonString = jsonArray.toString()
//
//        // Write the JSON string to a file
//        val fileName = "income_data.json"
//        val fileContent = jsonString.toByteArray()
//
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
//        }
//
//        val resolver = requireContext().contentResolver
//        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
//
//        uri?.let { fileUri ->
//            resolver.openOutputStream(fileUri)?.use { outputStream ->
//                outputStream.write(fileContent)
//            }
//        }
//
//        return uri
//    }

    private fun showDatePickerDialog(
        textViewToUpdate: TextView, currentDate: Calendar, minDate: Calendar? = null
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                textViewToUpdate.text = formatDate(calendar)
            },
            year,
            month,
            day
        )

        minDate?.let {
            datePickerDialog.datePicker.minDate = it.timeInMillis
        }

        datePickerDialog.datePicker.maxDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun formatDate(calendar: Calendar): String {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return simpleDateFormat.format(calendar.time)
    }
}