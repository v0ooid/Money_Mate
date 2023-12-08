package my.edu.tarc.moneymate.Record

import android.app.Dialog
import android.content.Context
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.CustomSpinner.ClassSpinnerAdapter
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.MonetaryAccount.MonetaryAccount
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transfer.Transfer
import my.edu.tarc.moneymate.Transfer.TransferViewModel

class RecordTransferAdapter constructor(
    private val context: Context,
    private val transferViewModel: TransferViewModel,
    private val recordList: MutableList<Transfer>
) : RecyclerView.Adapter<RecordTransferAdapter.RecordViewHolder>() {

    private var monetaryAccounts = mutableListOf<MonetaryAccount>()

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sourceAccount: TextView = itemView.findViewById(R.id.transferSourceAccount)
        val destAccount: TextView = itemView.findViewById(R.id.transferDestAccount)
        val transferDesc: TextView = itemView.findViewById(R.id.record_transfer_description)
        val transferResult: TextView = itemView.findViewById(R.id.record_transfer_result)
        val transferDate: TextView = itemView.findViewById(R.id.record_transfer_dateTime)
        val incomeLayout: View = itemView.findViewById(R.id.income_linear)
        val expenseLayout: View = itemView.findViewById(R.id.expense_linear)
        val transferLayout: View = itemView.findViewById(R.id.transfer_linear)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordTransferAdapter.RecordViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.record_item_layout, parent, false)
        return RecordTransferAdapter.RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val transfer = recordList[position]

        holder.transferLayout.visibility = View.VISIBLE
        holder.incomeLayout.visibility = View.GONE
        holder.expenseLayout.visibility = View.GONE

        holder.transferDesc.text = "${transfer.transferDescription}"
        holder.transferResult.text = "RM ${transfer.transferAmount}"
        holder.transferDate.text = "${transfer.transferDate}"

        transferViewModel.getAccountNameById(transfer.sourceAccountId)
            .observe(context as LifecycleOwner) {
                holder.sourceAccount.text = it.accountName
            }

        transferViewModel.getAccountNameById(transfer.destinationAccountId)
            .observe(context as LifecycleOwner) {
                holder.destAccount.text = it.accountName
            }

        holder.itemView.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView)
            popupMenu.inflate(R.menu.popup_menu_monetary_accounts)
            popupMenu.gravity = Gravity.END
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.pmMAccountEdit -> {
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

    private fun editItem(position: Int) {
        val editExpense = recordList[position]

        // Open the dialog for editing
        showDialogForEdit(position, editExpense)
    }

    private fun deleteItem(position: Int) {
        val deleteRecord = recordList[position]
        recordList.removeAt(position)
        notifyDataSetChanged()

        transferViewModel.deleteTransfer(deleteRecord)
        transferViewModel.updateTransfer(deleteRecord)


    }

    private fun updateItem(position: Int, updateExpense: Transfer) {
        recordList[position] = updateExpense
        notifyItemChanged(position)
    }

    fun updateList(newList: List<Transfer>) {
        recordList.clear()
        recordList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun showDialogForEdit(position: Int, editTransfer: Transfer) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.layout_edit_transfer_record, null)
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


        val editFromAccount : Spinner = dialog.findViewById(R.id.spinnerEditFrom)
        val editToAccount : Spinner = dialog.findViewById(R.id.spinnerEditTo)
        val editTextRecordAmount = dialog.findViewById<TextView>(R.id.etRecordAmount)
        val editTextDesc = dialog.findViewById<TextView>(R.id.etRecordDesc)

        editTextRecordAmount.inputType = InputType.TYPE_CLASS_NUMBER

        // Pre-populate the dialog for editing
        editTextRecordAmount.text = editTransfer.transferAmount.toString()
        editTextDesc.text = editTransfer.transferDescription

        val adapter = ClassSpinnerAdapter(
            context,
            monetaryAccounts,
            { mAccount -> mAccount.accountIcon},
            { mAccount -> mAccount.accountName}
        )
        editFromAccount.adapter = adapter

        editFromAccount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected "From" account
                val selectedFromAccount = editFromAccount.selectedItem as MonetaryAccount

                // Filter out the selected "From" account from the list of accounts
                val filteredAccounts = monetaryAccounts.filter { it.accountId != selectedFromAccount.accountId }

                // Create a new adapter excluding the selected "From" account
                val toAccountAdapter = ClassSpinnerAdapter(
                    context,
                    filteredAccounts,
                    { mAccount -> mAccount.accountIcon},
                    { mAccount -> mAccount.accountName}
                )

                // Set the adapter for the "To" spinner
                editToAccount.adapter = toAccountAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }

        // Add more logic to pre-populate other dialog UI components

        val yesBtn = dialog.findViewById<Button>(R.id.btnRecordConfirm)
        yesBtn.setOnClickListener {
            // Handle the edit confirmation logic here

            val sourceAccount = editFromAccount.selectedItem as MonetaryAccount
            val destAccount = editToAccount.selectedItem as MonetaryAccount

            val amount = editTextRecordAmount.text
            val desc = editTextDesc.text
            val decimalRegex = Regex("^\\d+(\\.\\d{2})?\$")

            if (amount.isEmpty()) {
                editTextRecordAmount.error = "Amount cannot be empty"
            } else if (amount.toString().toDouble() < 0.1) {
                editTextRecordAmount.error = "Amount cannot be 0 or less than 0"
            } else if (!amount.matches(decimalRegex))
                editTextRecordAmount.error = "Enter a decimal with two decimal places"
            else {

                val transfer = Transfer(
                    transferId = editTransfer.transferId,
                    transferDescription = desc.toString(),
                    transferAmount = amount.toString().toInt(),
                    sourceAccountId = sourceAccount.accountId,
                    destinationAccountId = destAccount.accountId,
                    transferDate = editTransfer.transferDate,
                    transferTitle = editTransfer.transferTitle
                )


                // Update the dataset in the adapter
                updateItem(position, transfer)

                // Update the database or perform other actions as needed
                transferViewModel.updateTransfer(transfer)

                dialog.dismiss()
                overlayLayout.visibility = View.GONE

            }
        }

        val noBtn = dialog.findViewById<Button>(R.id.btnRecordCancel)
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

    internal fun setAccount(mAccount: List<MonetaryAccount>) {
        this.monetaryAccounts = mAccount.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return recordList.size
    }
}

