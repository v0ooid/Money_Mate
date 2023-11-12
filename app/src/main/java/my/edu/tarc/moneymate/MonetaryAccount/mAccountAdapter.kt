package my.edu.tarc.moneymate.MonetaryAccount

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.CustomSpinner.IconAdapter
import my.edu.tarc.moneymate.CustomSpinner.IconItem
import my.edu.tarc.moneymate.R

class mAccountAdapter(
    private val context: Context,
    private val monetaryAccountViewModel: MonetaryAccountViewModel
    ) : RecyclerView.Adapter<mAccountAdapter.MyViewHolder>() {

    private var dataSet = mutableListOf<MonetaryAccount>()
    private var icon = ""


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.ivIconAccountItem)
        val name: TextView = itemView.findViewById(R.id.tvAccountNameRe)
        val amount: TextView = itemView.findViewById(R.id.tvAmountItem)
        val menu: ImageView = itemView.findViewById(R.id.iVMenuItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_monetary_account, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataSet[position]
        holder.name.text = item.accountName
        holder.amount.text = item.accountBalance.toString()

        val iconItems = listOf(
            IconItem(R.drawable.baseline_attach_money_24),
            IconItem(R.drawable.baseline_credit_card_24),
            IconItem(R.drawable.baseline_phone_android_24),
            // Add more items as needed
        )

        val resourceImage = holder.itemView.context.resources.getIdentifier(
            item.accountIcon,
            "drawable",
            holder.itemView.context.packageName
        )

        holder.icon.setImageResource(resourceImage)

        holder.menu.setOnClickListener {
            showPopupMenu(holder.menu, position)
        }
        Log.e("Check items", item.toString())
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    internal fun setAccount(account: List<MonetaryAccount>) {
        this.dataSet = account.toMutableList()
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
        val deletedAccount = dataSet[position]
        dataSet.removeAt(position)
        notifyDataSetChanged()

        // Call the delete method in the ViewModel to delete from the database
        monetaryAccountViewModel.deleteAccount(deletedAccount)
    }

    private fun editItem(position: Int) {
        val editAccount = dataSet[position]

        // Open the dialog for editing
        showDialogForEdit(position,editAccount)
    }

    private fun updateItem(position: Int, updatedAccount: MonetaryAccount) {
        dataSet[position] = updatedAccount
        notifyItemChanged(position)
    }


    private fun showDialogForEdit(position: Int,editAccount: MonetaryAccount) {
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

        val nameTextView = dialog.findViewById<TextView>(R.id.etvAccountName)
        val amountTextView = dialog.findViewById<TextView>(R.id.etvAccountAmount)
        val spinner: Spinner = dialog.findViewById(R.id.spinnerAddAccountIcon)
        val adapter = IconAdapter(context, iconItems)
        spinner.adapter = adapter

        // Add more dialog UI components as needed

        // Pre-populate the dialog for editing
        nameTextView.text = editAccount.accountName
        amountTextView.text = editAccount.accountBalance.toString()


        // Add more logic to pre-populate other dialog UI components

        val yesBtn = dialog.findViewById<Button>(R.id.btnConfirmAddAccount)
        yesBtn.setOnClickListener {
            // Handle the edit confirmation logic here

            val name  = nameTextView.text.toString()
            val amount = amountTextView.text.toString().toDouble()

            if (spinner.selectedItemPosition == 0){
                icon = "baseline_attach_money_24"
            } else if (spinner.selectedItemPosition == 1){
                icon = "baseline_credit_card_24"
            } else if (spinner.selectedItemPosition == 2){
                icon = "baseline_phone_android_24"
            }


            val updatedAccount = MonetaryAccount(
                accountId = editAccount.accountId,
                accountName = name,
                accountBalance = amount,
                accountIcon = icon
            )

            // Update the dataset in the adapter
            updateItem(position, updatedAccount)

            // Update the database or perform other actions as needed
            monetaryAccountViewModel.updateCategory(updatedAccount)

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



