package my.edu.tarc.moneymate.MonetaryAccount

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.CustomSpinner.ClasslessSpinnerAdapter
import my.edu.tarc.moneymate.CustomSpinner.ClasslessItem
import my.edu.tarc.moneymate.R

class mAccountAdapter(
    private val context: Context,
    private val monetaryAccountViewModel: MonetaryAccountViewModel,
    private val fragment: MonetaryAccountFragment
) : RecyclerView.Adapter<mAccountAdapter.MyViewHolder>() {

    private var dataSet = mutableListOf<MonetaryAccount>()
    private var icon: Int = 0

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
        val formattedNumber = String.format("%.2f", item.accountBalance)
        holder.amount.text = formattedNumber

        holder.icon.setImageResource(item.accountIcon)

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

    internal fun setAccount(account: List<MonetaryAccount>) {
        this.dataSet = account.toMutableList()
        notifyDataSetChanged()
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
        showDialogForEdit(position, editAccount)
    }

    private fun updateItem(position: Int, updatedAccount: MonetaryAccount) {
        dataSet[position] = updatedAccount
        notifyItemChanged(position)
    }


    private fun showDialogForEdit(position: Int, editAccount: MonetaryAccount) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_add_monetary_account_dialog)

        val dialogName = dialog.findViewById<TextView>(R.id.dialog_title)

        dialogName.text = "Edit Monetary Account"

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        val iconItems = listOf(
            ClasslessItem(R.drawable.malaysian_ringgit_icon, "Cash"),
            ClasslessItem(R.drawable.bank_svgrepo_com__1_, "Bank"),
            ClasslessItem(R.drawable.baseline_credit_card_24, "Card"),
            ClasslessItem(R.drawable.baseline_phone_android_24, "Digital Wallet")
        )

        val nameTextView = dialog.findViewById<TextView>(R.id.etvAccountName)
        val amountTextView = dialog.findViewById<TextView>(R.id.etvAccountAmount)
        val spinner: Spinner = dialog.findViewById(R.id.spinnerAddAccountIcon)
        val adapter = ClasslessSpinnerAdapter(context, iconItems)
        spinner.adapter = adapter

        // Add more dialog UI components as needed

        // Pre-populate the dialog for editing
        nameTextView.text = editAccount.accountName
        val formattedNumber = String.format("%.2f", editAccount.accountBalance)
        amountTextView.text = formattedNumber

        fragment.overlay.visibility = View.VISIBLE
        fragment.overlay2.visibility = View.VISIBLE

        // Add more logic to pre-populate other dialog UI components

        val yesBtn = dialog.findViewById<Button>(R.id.btnConfirmAddAccount)
        yesBtn.setOnClickListener {
            // Handle the edit confirmation logic here

            val name = nameTextView.text.toString()
            val amount = amountTextView.text

            val decimalRegex = Regex("^\\d+(\\.\\d{2})?\$")


            if (name.isEmpty()) {
                nameTextView.error = "Name cannot be empty"
            } else if (!name.matches(Regex("^[a-zA-Z ]+\$"))) {
                nameTextView.error = "Enter a valid name"
            } else if (amount.isEmpty()){
                amountTextView.error = "Amount cannot be empty"
            } else if (amount.toString().toDouble() < 0.1) {
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


                val updatedAccount = MonetaryAccount(
                    accountId = editAccount.accountId,
                    accountName = name,
                    accountBalance = amount.toString().toDouble(),
                    accountIcon = icon
                )

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Confirmation")
                builder.setMessage("Are you sure you want to confirm this edit?")

                builder.setPositiveButton("Yes") { _, _ ->
                    updateItem(position, updatedAccount)

                    // Update the database or perform other actions as needed
                    monetaryAccountViewModel.updateAccount(updatedAccount)

                    dialog.dismiss()
                    fragment.overlay.visibility = View.GONE
                    fragment.overlay2.visibility = View.GONE
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    // Handle cancellation of the edit
                    // This code block will be executed if the user clicks "No"
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()

            }
        }

        val noBtn = dialog.findViewById<Button>(R.id.btnCancelAddAccount)
        noBtn.setOnClickListener {
            // Dismiss the dialog
            dialog.dismiss()
            fragment.overlay.visibility = View.GONE
            fragment.overlay2.visibility = View.GONE
        }

        dialog.show()
    }


}



