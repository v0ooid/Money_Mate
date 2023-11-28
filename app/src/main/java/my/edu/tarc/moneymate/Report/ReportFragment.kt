package my.edu.tarc.moneymate.Report

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Income.IncomeViewModel
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentIncomeBinding
import my.edu.tarc.moneymate.databinding.FragmentReportBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Calendar

@Suppress("DEPRECATION")
class ReportFragment : Fragment() {

    companion object {
        fun newInstance() = ReportFragment()

        const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101
    }

    private var _binding: FragmentReportBinding ?= null
    private val binding get() = _binding!!
    private lateinit var viewModel: ReportViewModel
    private lateinit var incomeTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var incomeViewModel: IncomeViewModel
    private lateinit var layoutToCapture: ConstraintLayout
    private lateinit var searchButton: ImageView

    var startMonthYear = "" // Replace with actual start date
    var endMonthYear = "" // Replace with actual end date
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        recyclerView = view.findViewById(R.id.rvReport)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = ReportAdapter(emptyList())
//        viewModel = ViewModelProvider(this)[ReportViewModel::class.java]
//        Log.d("I am here","I am here")
//        viewModel.reportItems.observe(viewLifecycleOwner) { items ->
//            if (items.isNotEmpty()) {
//                recyclerView.adapter = ReportAdapter(items)
//                Log.e("report item  view model", items.toString())
//
//            }else
//            {
//                Log.e("report item  view model", "View Model Empty")
//            }
//        }
//
//        spinnerMonth = view.findViewById(R.id.tvReportMonthFrom)
//        spinnerYear = view.findViewById(R.id.tvReportYearFrom)
//
//        incomeViewModel = ViewModelProvider(this).get(IncomeViewModel::class.java)
//
////        binding.reportSearch.setOnClickListener{
////            val startDate = "selected start date" // Replace with logic to get start date
////            val endDate = "selected end date" // Replace with logic to get end date
////
////            incomeViewModel.getIncomeForDateRange(startDate, endDate)
////        }
////        reportAdapter = ReportAdapter(emptyList())
////        incomeViewModel.incomeInRange.observe(viewLifecycleOwner) { incomeList ->
////            val reportItems = incomeList.map { ReportItem.IncomeItem(it) }
////            reportAdapter.updateData(reportItems) // Update adapter data when new data arrives
////        }
////
////        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
////            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
////                updateIncomeData()
////            }
////            override fun onNothingSelected(parent: AdapterView<*>?) {}
////        }
////        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
////            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
////                updateIncomeData()
////            }
////            override fun onNothingSelected(parent: AdapterView<*>?) {}
////        }
//        reportAdapter = ReportAdapter(emptyList())
//        recyclerView.adapter = reportAdapter
//
//        binding.reportSearch.setOnClickListener {
//            val startMonthYear = "2023-10" // Adjust to get from UI
//            val endMonthYear = "2023-11" // Adjust to get from UI
//
//            viewModel.getFilteredData(startMonthYear, endMonthYear).observe(viewLifecycleOwner) { filteredData ->
//                reportAdapter.submitData(filteredData)
//            }
//        }
//
//
//
        val months = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear-3..currentYear).toList()
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tvReportMonthFrom.adapter = monthAdapter
        binding.tvReportMonthTo.adapter = monthAdapter

        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tvReportYearFrom.adapter = yearAdapter
        binding.tvReportYearTo.adapter = yearAdapter
        setupTabLayout()
        binding.tvReportMonthFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedMonth = months[position]
                val selectedYear = binding.tvReportYearFrom.selectedItem.toString()
                startMonthYear = "$selectedYear-${position + 1}" // Assuming month starts from 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            // Implement onNothingSelected
        }

        binding.tvReportMonthTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedMonth = months[position]
                val selectedYear = binding.tvReportYearTo.selectedItem.toString()
                endMonthYear = "$selectedYear-${position + 1}" // Assuming month starts from 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            // Implement onNothingSelected
        }

//        //back button
        binding.leftIcon.setOnClickListener{
            findNavController().navigateUp()
        }

        layoutToCapture = view.findViewById(R.id.report_bottomPart)
        searchButton = view.findViewById(R.id.ivReportShare)

        searchButton.setOnClickListener {
            checkPermissionAndSaveImage()
        }


        viewModel = ViewModelProvider(this)[ReportViewModel::class.java]

        setupRecyclerView()
        observeCombinedData()
        setupSearchButtonListener()

    }
    private fun deselectAllTabs(tabLayout: TabLayout) {
        val tabStrip = tabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).isClickable = false
        }
    }
    private fun enableTabClicks(tabLayout: TabLayout) {
        val tabStrip = tabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).isClickable = true
        }
    }
    private fun setupTabLayout() {
        val tabLayout = binding.transactiontab
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                enableTabClicks(tabLayout)
                Log.d("ReportFragment", "Tab selected: ${tab?.position}")
                when (tab?.position) {
                    0 ->{
                        viewModel.reportItems.observe(viewLifecycleOwner){
                            data-> reportAdapter.submitData(data)
                            setupPieChartWithReportItems(data)
                        }
                        setupSearchButtonListener()
                        binding.tvReportTitleCategory.text = "Category "

                    }
                    1 -> {
                        // Income tab selected
                        viewModel.incomeItems.observe(viewLifecycleOwner) { incomeData ->
                            Log.d("ReportFragment", "Income data received: ${incomeData.size}")
                            // Update RecyclerView with income data only
                            reportAdapter.submitIncomeData(incomeData)
                            setupPieChartWithIncome(incomeData)
                        }
                        binding.reportSearch.setOnClickListener{
                            viewModel.getFilteredDataIncome(startMonthYear, endMonthYear).observe(viewLifecycleOwner) { incomeData ->
                                reportAdapter.submitIncomeData(incomeData)
                                setupPieChartWithIncome(incomeData)
                            }
                        }
                        binding.cvChart.setOnClickListener {
                            val layoutParams = binding.cvChart.layoutParams
                            layoutParams.height = (300 * resources.displayMetrics.density).toInt()
                            binding.cvChart.layoutParams = layoutParams
                        }
                        binding.tvReportTitleCategory.text = "Category "

                    }
                    2 -> {
                        viewModel.expenseItems.observe(viewLifecycleOwner) { expenseData ->
                            Log.d("ReportFragment", "expense data received: ${expenseData.size}")

                            reportAdapter.submitExpensesData(expenseData)
                            setupPieChartWithExpense(expenseData)

                        }
                        binding.reportSearch.setOnClickListener {
                            viewModel.getFilteredDataExpense(startMonthYear, endMonthYear)
                                .observe(viewLifecycleOwner) { data ->
                                    reportAdapter.submitExpensesData(data)
                                    setupPieChartWithExpense(data)
                                }
                        }
                        binding.tvReportTitleCategory.text = "Category "

                    }
                    3 -> {
                        viewModel.goalItems.observe(viewLifecycleOwner) { goalData ->
                            Log.d("ReportFragment", "Goal data received: ${goalData.size}")

                            reportAdapter.submitGoalsData(goalData)
                            setupPieChartWithGoal(goalData)

                        }
                        binding.reportSearch.setOnClickListener {
                            viewModel.getFilteredDataGoal(startMonthYear, endMonthYear)
                                .observe(viewLifecycleOwner) { data ->
                                    reportAdapter.submitGoalsData(data)
                                    setupPieChartWithGoal(data)
                                }
                        }
                        binding.tvReportTitleCategory.text = "Target Amount: "
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle unselection if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle reselection if needed
            }
        })
    }

    private fun setupPieChartWithReportItems(data: List<ReportItem>) {
        val entries = ArrayList<PieEntry>()

        val incomeAmount = data.filterIsInstance<ReportItem.IncomeItem>().sumOf { it.income.amount }
        val expenseAmount = data.filterIsInstance<ReportItem.ExpenseItem>().sumOf { it.expense.amount }
        val goalAmount = data.filterIsInstance<ReportItem.GoalItem>().sumOf { it.goal.targetAmount }

        if (incomeAmount > 0) entries.add(PieEntry(incomeAmount.toFloat(), "Income"))
        if (expenseAmount > 0) entries.add(PieEntry(expenseAmount.toFloat(), "Expense"))
        if (goalAmount > 0) entries.add(PieEntry(goalAmount.toFloat(), "Goal"))

        val dataSet = PieDataSet(entries, "Categories")
        dataSet.setColors(listOf(Color.GREEN, Color.RED, Color.BLUE)) // Set specific colors for Income, Expense, Goal
        val pieData = PieData(dataSet)

        binding.pieChart.data = pieData
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)
        binding.pieChart.invalidate() // Refresh the chart
    }
    private fun setupPieChartWithIncome(data: List<Income>) {
        val entries = ArrayList<PieEntry>()

        // Assuming each Income has a category or type that you want to display
        val incomeByCategory = data.groupBy { it.title } // Replace 'category' with the actual field
        for ((category, incomes) in incomeByCategory) {
            val totalAmount = incomes.sumOf { it.amount }
            entries.add(PieEntry(totalAmount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "Income Categories")
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS) // Use Material Colors for Income Categories
        val pieData = PieData(dataSet)

        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false // Optional: disable the description
        binding.pieChart.isDrawHoleEnabled = false // Optional: for a full pie chart
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad) // Animate the chart
        binding.pieChart.invalidate() // Refresh the chart
    }
    private fun setupPieChartWithExpense(data: List<Expense>) {
        val entries = ArrayList<PieEntry>()

        // Assuming each Income has a category or type that you want to display
        val expenseByCategory = data.groupBy { it.title } // Replace 'category' with the actual field
        for ((category, incomes) in expenseByCategory) {
            val totalAmount = incomes.sumOf { it.amount }
            entries.add(PieEntry(totalAmount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "Expense Categories")
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS) // Use Material Colors for Income Categories
        val pieData = PieData(dataSet)

        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false // Optional: disable the description
        binding.pieChart.isDrawHoleEnabled = false // Optional: for a full pie chart
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad) // Animate the chart
        binding.pieChart.invalidate() // Refresh the chart
    }
    private fun setupPieChartWithGoal(data: List<Goal>) {
        val totalTargetAmount = data.sumOf { it.targetAmount }
        val totalSavedAmount = data.sumOf { it.savedAmount }

        val entries = ArrayList<PieEntry>().apply {
            add(PieEntry(totalTargetAmount.toFloat(), "Total Target Amount"))
            add(PieEntry(totalSavedAmount.toFloat(), "Total Saved Amount"))
        }

        val dataSet = PieDataSet(entries, "Goal Summary")
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS) // Use different colors for Target and Saved Amounts
        val pieData = PieData(dataSet)

        binding.pieChart.data = pieData
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = false
            animateY(1400, Easing.EaseInOutQuad)
            invalidate() // Refresh the chart
        }
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter()
        binding.rvReport.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reportAdapter
        }
    }

    private fun observeCombinedData() {
        viewModel.reportItems.observe(viewLifecycleOwner) { combinedData ->
            reportAdapter.submitData(combinedData)
            setupPieChartWithReportItems(combinedData)
        }
    }

    private fun setupSearchButtonListener() {
        binding.reportSearch.setOnClickListener {


            viewModel.getFilteredData(startMonthYear, endMonthYear).observe(viewLifecycleOwner) { filteredData ->
                reportAdapter.submitData(filteredData)
                setupPieChartWithReportItems(filteredData)

            }
        }
    }

    private fun checkPermissionAndSaveImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_REQUEST_CODE
            )
            Log.d("testing pressed","Failed 2")

        } else {
            // Permission has already been granted, save the image
            saveImageToGallery()
            Log.d("testing pressed","Success")

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to save the image
                saveImageToGallery()
                Log.d("testing pressed","Test")
            } else {
                // Permission denied, inform the user or handle accordingly
                showPermissionExplanationDialog()
                Log.d("testing pressed","Failed")

            }
        }
    }
    private fun showPermissionExplanationDialog() {
        // Create a dialog or snackbar to inform the user about the necessity of the permission
        // and guide them to the app settings
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Needed")
            .setMessage("This feature requires storage access permission. Please grant the permission in the app settings.")
            .setPositiveButton("Go to Settings") { dialog, _ ->
                navigateToAppSettings()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun saveImageToGallery() {
        val bitmap = getBitmapFromView(layoutToCapture) // Replace layoutToCapture with the view you want to capture

        // Generating a filename for the image
        val fileName = "layout_capture_${System.currentTimeMillis()}.png"

        // Creating a new content resolver
        val resolver = requireContext().contentResolver

        // Creating a new ContentValues object to store the image details
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.WIDTH, bitmap?.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap?.height)
        }

        // Inserting the image into the MediaStore
        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            try {
                // Open an output stream to write the bitmap data to the content provider
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                // Notify the gallery of the new image
                MediaScannerConnection.scanFile(
                    requireContext(),
                    arrayOf(uri.path),
                    arrayOf("image/png")
                ) { path, uri ->
                    // Callback when scanning is complete, you can log or perform any other action here
                    Log.d("MediaScanner", "Scanned $path")
                }

                // Image saved successfully
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        Snackbar.make(layoutToCapture, "Layout captured and saved", Snackbar.LENGTH_SHORT).show()
        findNavController().navigateUp()
        Log.d("testing Save", "Success")
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

//    private fun updateIncomeData() {
//        val selectedMonth = spinnerMonth.selectedItem.toString()
//        val selectedYear = spinnerYear.selectedItem.toString()
//
//        // Combine selected month and year into YYYY-MM format
//        val selectedMonthYear = "$selectedYear-$selectedMonth"
//
//        // Observe income data for the selected month and year
//        incomeViewModel.getIncomeForMonthYear(selectedMonthYear).observe(viewLifecycleOwner) { incomeList ->
//            // Handle the retrieved incomeList for display or further processing
//        }
//    }



}