package my.edu.tarc.moneymate.Report

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Income.IncomeViewModel
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentReportBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

        val months = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tvReportMonthFrom.adapter = monthAdapter
        binding.tvReportMonthTo.adapter = monthAdapter

        val years = (currentYear-3..currentYear+3).toList()
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tvReportYearFrom.adapter = yearAdapter
        binding.tvReportYearTo.adapter = yearAdapter
        setupTabLayout()
        binding.tvReportMonthFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedMonth = String.format("%02d",position+1)
                val selectedYear = String.format("%02d",binding.tvReportYearFrom.selectedItem)
                startMonthYear = "$selectedYear-$selectedMonth"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            // Implement onNothingSelected
        }

        binding.tvReportMonthTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedMonth = String.format("%02d",position+1)
                val selectedItem = binding.tvReportYearTo.selectedItem
                val selectedYear = String.format("%02d",selectedItem)
                Log.d("selected year", "selectedYear $selectedYear and $selectedItem")
                endMonthYear = "$selectedYear-$selectedMonth"
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

//        searchButton.setOnClickListener {
//            checkPermissionAndSaveImage()
//        }
        searchButton.setOnClickListener{


            savePdfFromView(layoutToCapture)
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
                        binding.tvReportTitle.text = "General Report"
                        binding.tvReportTitleCategory.text = "Category "
                        binding.tvReportTitleAmount.text = "Amount"

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
                        binding.tvReportTitle.text = "Income Report"

                        binding.tvReportTitleCategory.text = "Category "
                        binding.tvReportTitleAmount.text = "Amount"

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
                        binding.tvReportTitle.text = "Expense Report"

                        binding.tvReportTitleCategory.text = "Category "
                        binding.tvReportTitleAmount.text = "Amount"

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
                        binding.tvReportTitle.text = "Goal Report"

                        binding.tvReportTitleCategory.text = "Target Amount: "
                        binding.tvReportTitleAmount.text = "Saved Amount"

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

        val incomeByCategory = data.groupBy { it.incomeTitle }
        for ((category, incomes) in incomeByCategory) {
            val totalAmount = incomes.sumOf { it.amount }
            entries.add(PieEntry(totalAmount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "Income Categories")

        // Custom colors
        val colors = mutableListOf<Int>()
        colors.add(ContextCompat.getColor(requireContext(), R.color.color1))
        colors.add(ContextCompat.getColor(requireContext(), R.color.color2))
        colors.add(ContextCompat.getColor(requireContext(), R.color.color3))
        colors.add(ContextCompat.getColor(requireContext(), R.color.color4))

        while (colors.size < entries.size) {
            colors.add(R.color.color1) // Replace 'someDefaultColor' with a default color
        }
        // Add more colors if needed
        dataSet.colors = colors

        // Style the data set
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 10f

        val pieData = PieData(dataSet)

        // Customize the pie chart
        binding.pieChart.data = pieData
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            setDrawCenterText(true)
            centerText = "Income Distribution"

            setCenterTextColor(Color.BLACK)
            setCenterTextSize(14f)
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = true

            invalidate()
        }
    }

    private fun setupPieChartWithExpense(data: List<Expense>) {
        val entries = ArrayList<PieEntry>()

        val expenseByCategory = data.groupBy { it.expense_title }
        for ((category, expenses) in expenseByCategory) {
            val totalAmount = expenses.sumOf { it.amount }
            entries.add(PieEntry(totalAmount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "Expense Categories")

        // Custom colors
        val colors = mutableListOf<Int>()
        colors.add(ContextCompat.getColor(requireContext(), R.color.color1))
        colors.add(ContextCompat.getColor(requireContext(), R.color.color2))
        colors.add(ContextCompat.getColor(requireContext(), R.color.color3))
        colors.add(ContextCompat.getColor(requireContext(), R.color.color4))

        while (colors.size < entries.size) {
            colors.add(R.color.color1) // Replace 'someDefaultColor' with a default color
        }
        dataSet.colors = colors
        // Style the data set
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 10f

        val pieData = PieData(dataSet)

        // Customize the pie chart
        binding.pieChart.data = pieData
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            setDrawCenterText(true)
            centerText = "Expense Distribution"
            setCenterTextColor(Color.BLACK)
            setCenterTextSize(14f)
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = true

            invalidate()
        }
    }
    private fun setupPieChartWithGoal(data: List<Goal>) {
        val totalTargetAmount = data.sumOf { it.targetAmount }
        val totalSavedAmount = data.sumOf { it.savedAmount }

        val entries = ArrayList<PieEntry>().apply {
            add(PieEntry(totalTargetAmount.toFloat(), "Total Target Amount"))
            add(PieEntry(totalSavedAmount.toFloat(), "Total Saved Amount"))
        }

        val dataSet = PieDataSet(entries, "Goal Summary")

        // Custom colors
        val colors = mutableListOf<Int>()
        colors.add(ContextCompat.getColor(requireContext(), R.color.color1))
        colors.add(ContextCompat.getColor(requireContext(), R.color.color2))
        colors.add(ContextCompat.getColor(requireContext(), R.color.color3))
        colors.add(ContextCompat.getColor(requireContext(), R.color.color4))

        while (colors.size < entries.size) {
            colors.add(R.color.color1) // Replace 'someDefaultColor' with a default color
        }
        dataSet.colors = colors

        // Style the data set
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 10f

        val pieData = PieData(dataSet)

        // Customize the pie chart
        binding.pieChart.data = pieData
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            setDrawCenterText(true)
            centerText = "Goal Progress"
            setCenterTextColor(Color.BLACK)
            setCenterTextSize(14f)
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = true

            invalidate()
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
    private fun savePdfFromView(view: View) {

        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val dateString = dateFormat.format(Date())
        val fileName = "Report_$dateString.pdf"

        // Define the file path where the PDF will be saved
        val pdfFilePath = requireContext().getExternalFilesDir(null)?.absolutePath + "/" + fileName        // Measure and layout the view
        view.measure(View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY))
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        // Create a bitmap
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        // Create a PdfDocument
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        // Draw the bitmap onto the PdfDocument's Canvas
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        // Write the PdfDocument to a file
        try {
            val fileOutputStream = FileOutputStream(pdfFilePath)
            pdfDocument.writeTo(fileOutputStream)
            Snackbar.make(view, "PDF saved to $pdfFilePath", Snackbar.LENGTH_LONG)
                .setAction("View"){
                    openPDFFile(pdfFilePath)
                }
                .show()

        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(view, "Error saving PDF", Snackbar.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
            bitmap.recycle()
        }
    }



//
//    // Inside your Fragment or Activity
//    private fun generatePDF(adapter: ReportAdapter) {
//        val pdfDocument = PdfDocument(PdfWriter("report.pdf"))
//        val document = Document(pdfDocument, PageSize.A4)
//        document.setMargins(20f, 20f, 20f, 20f)
//
//        val table = Table(floatArrayOf(25f, 25f, 25f, 25f)).useAllAvailableWidth()
//        table.addCell(createCell("Source Title", true))
//        table.addCell(createCell("Date", true))
//        table.addCell(createCell("Category", true))
//        table.addCell(createCell("Amount", true))
//
//        for (i in 0 until adapter.itemCount) {
//            val viewHolder = adapter.createViewHolder(null, adapter.getItemViewType(i))
//            adapter.onBindViewHolder(viewHolder, i)
//            when (viewHolder) {
//                is ReportAdapter.IncomeViewHolder -> {
//                    table.addCell(createCell(viewHolder.itemView.findViewById<TextView>(R.id.report_income_source).text.toString()))
//                    table.addCell(createCell(viewHolder.itemView.findViewById<TextView>(R.id.report_income_date).text.toString()))
//                    table.addCell(createCell(viewHolder.itemView.findViewById<TextView>(R.id.report_income_category).text.toString()))
//                    table.addCell(createCell(viewHolder.itemView.findViewById<TextView>(R.id.report_income_amount).text.toString()))
//                }
//                // Add cases for GoalViewHolder and ExpenseViewHolder if needed
//            }
//        }
//
//        table.setBackgroundColor(ColorConstants.WHITE)
//        table.setFontColor(ColorConstants.BLACK)
//        table.setTextAlignment(TextAlignment.LEFT)
//
//        document.add(table)
//        document.close()
//
//        showSnackbarAndRedirect("report.pdf")
//    }
//
//    private fun createCell(text: String, isHeader: Boolean = false): Cell {
//        val cell = Cell().add(text)
//        cell.setBackgroundColor(if (isHeader) ColorConstants.LIGHT_GRAY else ColorConstants.WHITE)
//        cell.setFontColor(if (isHeader) ColorConstants.WHITE else ColorConstants.BLACK)
//        cell.setFontSize(12f)
//        cell.setTextAlignment(TextAlignment.CENTER)
//        cell.setBorder(Border.NO_BORDER)
//        cell.setPadding(5f)
//        return cell
//    }
    private fun showSnackbarAndRedirect(filePath: String?) {
        if (filePath != null) {
            Snackbar.make(
                requireView(),
                "PDF generated successfully",
                Snackbar.LENGTH_LONG
            ).setAction("View") {
                openPDFFile(filePath)
            }.show()
        } else {
            // Handle the case where filePath is null (if PDF generation fails)
            Snackbar.make(
                requireView(),
                "PDF generation failed",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun openPDFFile(filePath: String) {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle scenario where no PDF viewer app is installed
            Toast.makeText(
                requireContext(),
                "No PDF viewer application found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


//    private fun checkPermissionAndSaveImage() {
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Permission is not granted, request it
//            requestPermissions(
//                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                WRITE_EXTERNAL_STORAGE_REQUEST_CODE
//            )
//            Log.d("testing pressed","Failed 2")
//
//        } else {
//            // Permission has already been granted, save the image
//            saveImageToGallery()
//            Log.d("testing pressed","Success")
//
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, proceed to save the image
//                saveImageToGallery()
//                Log.d("testing pressed","Test")
//            } else {
//                // Permission denied, inform the user or handle accordingly
//                showPermissionExplanationDialog()
//                Log.d("testing pressed","Failed")
//
//            }
//        }
//    }
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

//    private fun saveImageToGallery() {
//        val bitmap = getBitmapFromView(layoutToCapture) // Replace layoutToCapture with the view you want to capture
//
//        // Generating a filename for the image
//        val fileName = "layout_capture_${System.currentTimeMillis()}.png"
//
//        // Creating a new content resolver
//        val resolver = requireContext().contentResolver
//
//        // Creating a new ContentValues object to store the image details
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
//            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
//            put(MediaStore.Images.Media.WIDTH, bitmap?.width)
//            put(MediaStore.Images.Media.HEIGHT, bitmap?.height)
//        }
//
//        // Inserting the image into the MediaStore
//        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        imageUri?.let { uri ->
//            try {
//                // Open an output stream to write the bitmap data to the content provider
//                resolver.openOutputStream(uri)?.use { outputStream ->
//                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//                }
//
//                // Notify the gallery of the new image
//                MediaScannerConnection.scanFile(
//                    requireContext(),
//                    arrayOf(uri.path),
//                    arrayOf("image/png")
//                ) { path, uri ->
//                    // Callback when scanning is complete, you can log or perform any other action here
//                    Log.d("MediaScanner", "Scanned $path")
//                }
//
//                // Image saved successfully
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//
//        Snackbar.make(layoutToCapture, "Layout captured and saved", Snackbar.LENGTH_SHORT).show()
//        findNavController().navigateUp()
//        Log.d("testing Save", "Success")
//    }
//
//    private fun getBitmapFromView(view: View): Bitmap? {
//        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        view.draw(canvas)
//        return bitmap
//    }

}