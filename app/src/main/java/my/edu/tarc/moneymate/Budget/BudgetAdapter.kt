package my.edu.tarc.moneymate.Budget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.CustomSpinner.IconAdapter
import my.edu.tarc.moneymate.CustomSpinner.AccountIconItem
import my.edu.tarc.moneymate.CustomSpinner.CategorySpinnerAdapter
import my.edu.tarc.moneymate.R

class BudgetAdapter(
    private val context: Context,
    private val budgetViewModel: BudgetViewModel
) : RecyclerView.Adapter<BudgetAdapter.MyViewHolder>() {

    private var dataSet = mutableListOf<Budget>()
    private var categoryDataSet = mutableListOf<Category>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iVBudgetIcon)
        val name: TextView = itemView.findViewById(R.id.tvBudgetName)
        val limit: TextView = itemView.findViewById(R.id.tvBudgetLimit)
        val spent: TextView = itemView.findViewById(R.id.tvBudgetSpent)
        val menu: ImageView = itemView.findViewById(R.id.iVBudgetMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_budget, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetAdapter.MyViewHolder, position: Int) {
        val item = dataSet[position]
        holder.name.text = item.budgetName

        val budgetLimitF = String.format("%.2f", item.budgetLimit)
        holder.limit.text = budgetLimitF

        val budgetSpentF = String.format("%.2f", item.budgetSpent)
        holder.spent.text = budgetSpentF

        val resourceImage = holder.itemView.context.resources.getIdentifier(
            item.budgetIcon.toString(),
            "drawable",
            holder.itemView.context.packageName
        )

        holder.icon.setImageResource(resourceImage)

        holder.menu.setOnClickListener {
            val popupMenu = PopupMenu(holder.menu.context, holder.menu)
            popupMenu.inflate(R.menu.popup_menu_monetary_accounts)

            popupMenu.gravity = Gravity.END


            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.pmMAccountEdit -> {
                        // Delete the item from the list
                        editItem(position)
                        true
                    }

                    R.id.pmMAccountDelete -> {
                        deleteItem(position)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    internal fun setBudget(budget: List<Budget>) {
        this.dataSet = budget.toMutableList()
        notifyDataSetChanged()
    }

    internal fun setCategory(category: List<Category>) {
        this.categoryDataSet = category.toMutableList()
        notifyDataSetChanged()
    }

    private fun deleteItem(position: Int) {
        val deletedBudget = dataSet[position]
        dataSet.removeAt(position)
        notifyDataSetChanged()

        // Call the delete method in the ViewModel to delete from the database
        budgetViewModel.deleteBudget(deletedBudget)
    }

    private fun editItem(position: Int) {
        val editBudget = dataSet[position]

        // Open the dialog for editing
        showDialogForEdit(position, editBudget)
    }

    private fun updateItem(position: Int, updatedBudget: Budget) {
        dataSet[position] = updatedBudget
        notifyItemChanged(position)
    }

    private fun showDialogForEdit(position: Int, editBudget: Budget) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.layout_add_budget_dialog, null)
        val overlayView = inflater.inflate(R.layout.dark_overlay, null)


        val dialog = Dialog(context)
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
        val adapter = CategorySpinnerAdapter(context, categoryDataSet)
        spinner.adapter = adapter

        // Add more dialog UI components as needed

        // Pre-populate the dialog for editing
        nameTextView.text = editBudget.budgetName
        val formattedNumber = String.format("%.2f", editBudget.budgetLimit)
        limitTextView.text = formattedNumber

        // Add more logic to pre-populate other dialog UI components

        val yesBtn = dialog.findViewById<Button>(R.id.btnBudgetConfrim)
        yesBtn.setOnClickListener {
            // Handle the edit confirmation logic here

            val name = nameTextView.text.toString()
            val limit = limitTextView.text

            val selectedCate = spinner.selectedItem as Category

            val decimalRegex = Regex("^\\d+(\\.\\d{2})?\$")

            if (name.isEmpty()) {
                nameTextView.error = "Name cannot be empty"
            } else if (!name.matches(Regex("^[a-zA-Z ]+\$"))) {
                nameTextView.error = "Enter a valid name"
            } else if (limit.toString().toDouble() < 0.1) {
                limitTextView.error = "Amount cannot be 0"
            } else if (!limit.matches(decimalRegex))
                limitTextView.error = "Enter a decimal with two decimal places"
            else {

                val updatedBudget = Budget(
                    budgetId = editBudget.budgetId,
                    budgetIcon = selectedCate.iconResId,
                    budgetName = name,
                    budgetLimit = limit.toString().toDouble(),
                    budgetSpent = editBudget.budgetSpent,
                    categoryId = selectedCate.categoryId
                )


                // Update the dataset in the adapter
                updateItem(position, updatedBudget)

                // Update the database or perform other actions as needed
                budgetViewModel.updateBudget(updatedBudget)

                dialog.dismiss()
                overlayLayout.visibility = View.GONE

            }
        }

        val noBtn = dialog.findViewById<Button>(R.id.btnBudgetCancel)
        noBtn.setOnClickListener {
            // Dismiss the dialog
            dialog.dismiss()
            overlayLayout.visibility = View.GONE
        }

        overlayLayout.visibility = View.VISIBLE
        (context as AppCompatActivity).addContentView(
            overlayLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        dialog.show()
    }

}