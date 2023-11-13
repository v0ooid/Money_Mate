package my.edu.tarc.moneymate.Budget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.CustomSpinner.IconAdapter
import my.edu.tarc.moneymate.CustomSpinner.IconItem
import my.edu.tarc.moneymate.R

class BudgetAdapter(
    private val context: Context,
    private val budgetViewModel: BudgetViewModel
    ) : RecyclerView.Adapter<BudgetAdapter.MyViewHolder>() {

    private var dataSet = mutableListOf<Budget>()
    private var icon = ""

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
        holder.limit.text = item.budgetLimit.toString()
        holder.spent.text = item.budgetSpent.toString()

        val resourceImage = holder.itemView.context.resources.getIdentifier(
            item.budgetIcon,
            "drawable",
            holder.itemView.context.packageName
        )

        holder.icon.setImageResource(resourceImage)

        holder.menu.setOnClickListener {
            showPopupMenu(holder.menu, position)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    internal fun setBudget(budget: List<Budget>) {
        this.dataSet = budget.toMutableList()
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popup_menu_monetary_accounts)

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
        showDialogForEdit(position,editBudget)
    }

    private fun updateItem(position: Int, updatedBudget: Budget) {
        dataSet[position] = updatedBudget
        notifyItemChanged(position)
    }

    private fun showDialogForEdit(position: Int, editBudget: Budget) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_add_monetary_account_dialog)

        val iconItems = listOf(
            IconItem(R.drawable.baseline_attach_money_24),
            IconItem(R.drawable.baseline_credit_card_24),
            IconItem(R.drawable.baseline_phone_android_24),
            // Add more items as needed
        )

        val nameTextView = dialog.findViewById<TextView>(R.id.eTBudgetName)
        val limitTextView = dialog.findViewById<TextView>(R.id.eTBudgetLimit)

        val spinner: Spinner = dialog.findViewById(R.id.sBudgetIcon)
        val adapter = IconAdapter(context, iconItems)
        spinner.adapter = adapter

        // Add more dialog UI components as needed

        // Pre-populate the dialog for editing
        nameTextView.text = editBudget.budgetName
        limitTextView.text = editBudget.budgetLimit.toString()

        // Add more logic to pre-populate other dialog UI components

        val yesBtn = dialog.findViewById<Button>(R.id.btnBudgetConfrim)
        yesBtn.setOnClickListener {
            // Handle the edit confirmation logic here

            val name  = nameTextView.text.toString()
            val limit = limitTextView.text.toString().toDouble()

            if (spinner.selectedItemPosition == 0){
                icon = "baseline_attach_money_24"
            } else if (spinner.selectedItemPosition == 1){
                icon = "baseline_credit_card_24"
            } else if (spinner.selectedItemPosition == 2){
                icon = "baseline_phone_android_24"
            }


            val updatedBudget = Budget(
                budgetId = editBudget.budgetId,
                budgetIcon = icon,
                budgetName = name,
                budgetLimit = limit,
                budgetSpent = editBudget.budgetSpent
            )

            // Update the dataset in the adapter
            updateItem(position, updatedBudget)

            // Update the database or perform other actions as needed
            budgetViewModel.updateBudget(updatedBudget)

            dialog.dismiss()
        }

        val noBtn = dialog.findViewById<Button>(R.id.btnCancelAddAccount)
        noBtn.setOnClickListener {
            // Dismiss the dialog
            dialog.dismiss()
        }

        dialog.show()
    }

}