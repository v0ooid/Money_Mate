package my.edu.tarc.moneymate.Record

import android.app.Dialog
import android.content.Context
import android.text.InputType
import android.util.Log
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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Budget.BudgetViewModel
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Expense.ExpenseViewModel
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.R

class RecordExpenseAdapter constructor(
    private val context: Context,
    private val expenseViewModel: ExpenseViewModel,
    private val recordViewModel: RecordViewModel,
    private val budgetViewModel: BudgetViewModel,
    val getFragment: RecordFragment,
    private val recordList: MutableList<Record>
) : RecyclerView.Adapter<RecordExpenseAdapter.RecordViewHolder>() {
    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val incometitle: TextView = itemView.findViewById(R.id.record_income_title)
        val expenseTitle: TextView = itemView.findViewById(R.id.record_expense_title)
        val incomeDes: TextView = itemView.findViewById(R.id.record_income_description)
        val expenseDes: TextView = itemView.findViewById(R.id.record_expense_description)
        val incomeResult: TextView = itemView.findViewById(R.id.record_income_result)
        val expenseResult: TextView = itemView.findViewById(R.id.record_expense_result)
        val incomeDate: TextView = itemView.findViewById(R.id.record_income_dateTime)
        val expenseDate: TextView = itemView.findViewById(R.id.record_expense_dateTime)
        val incomeLayout: View = itemView.findViewById(R.id.income_linear)
        val expenseLayout: View = itemView.findViewById(R.id.expense_linear)
        val transferLayout: View = itemView.findViewById(R.id.transfer_linear)
        val expenseIcon : ImageView = itemView.findViewById(R.id.record_expense_icon)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordExpenseAdapter.RecordViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.record_item_layout, parent, false)
        return RecordExpenseAdapter.RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        if (recordList[position].type == "expense") {
            holder.incomeLayout.visibility = View.GONE
            holder.transferLayout.visibility = View.GONE
            holder.expenseLayout.visibility = View.VISIBLE

            holder.expenseTitle.text = recordList[position].title
            holder.expenseResult.text = "RM" + recordList[position].amount.toString()
            holder.expenseDate.text = recordList[position].date
            holder.expenseDes.text = recordList[position].description
            holder.expenseIcon.setImageResource(recordList[position].image)
            Log.d("currentdataExpense", recordList[position].toString())
        } else if (recordList[position].type == "income") {
            holder.expenseLayout.visibility = View.GONE
            holder.incomeLayout.visibility = View.GONE
            holder.transferLayout.visibility = View.GONE
        } else if (recordList[position].type == "transfer"){
            holder.expenseLayout.visibility = View.GONE
            holder.incomeLayout.visibility = View.GONE
            holder.transferLayout.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView)
            popupMenu.inflate(R.menu.popup_menu_monetary_accounts)
            popupMenu.gravity = Gravity.END
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.pmMAccountEdit -> {
                        // Delete the item from the list
                        editItem(position, recordList[position].type)
                        true
                    }

                    R.id.pmMAccountDelete -> {
                        deleteItem(position, recordList[position].type)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun editItem(position: Int, type: String) {
        val editExpense = recordList[position]

        // Open the dialog for editing
        showDialogForEdit(position, editExpense)
    }

    private fun deleteItem(position: Int, type: String) {
        val deleteRecord = recordList[position]
        var title = recordList[position].title
        var id = recordList[position].id
        var image = recordList[position].image
        var desc = recordList[position].description
        var amount = recordList[position].amount
        var accountId = recordList[position].accountId
        var categoryId = recordList[position].categoryId
        var date = recordList[position].date


        recordList.removeAt(position)
        notifyDataSetChanged()
        if (type == "expense") {
            val deleteExpense = Expense(id, title, image, desc, amount, categoryId.toLong(), accountId.toLong(), date)

            // Call the delete method in the ViewModel to delete from the database
            expenseViewModel.deleteExpense(deleteExpense)
        }
    }
    private fun updateItem(position: Int, updateExpense: Record) {
        recordList[position] = updateExpense
        notifyItemChanged(position)
    }
    fun updateList(newList: List<Record>) {
        recordList.clear()
        recordList.addAll(newList)
        notifyDataSetChanged()
    }


    private fun showDialogForEdit(position: Int, editRecord: Record) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.layout_edit_record, null)
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


        val editTextRecordTitle = dialog.findViewById<TextView>(R.id.etRecordTitle)
        val editTextRecordAmount = dialog.findViewById<TextView>(R.id.etRecordAmount)
        val editTextDesc = dialog.findViewById<TextView>(R.id.etRecordDesc)

        editTextRecordAmount.inputType = InputType.TYPE_CLASS_NUMBER

        // Add more dialog UI components as needed

        // Pre-populate the dialog for editing
        editTextRecordTitle.text = editRecord.title
        editTextRecordAmount.text = editRecord.amount.toString()
        editTextDesc.text = editRecord.description
        // Add more logic to pre-populate other dialog UI components

        val yesBtn = dialog.findViewById<Button>(R.id.btnRecordConfirm)
        yesBtn.setOnClickListener {
            // Handle the edit confirmation logic here

            val title = editTextRecordTitle.text.toString()
            val amount = editTextRecordAmount.text
            val desc = editTextDesc.text
            val decimalRegex = Regex("^\\d+(\\.\\d{2})?\$")

            if (title.isEmpty()) {
                editTextRecordTitle.error = "Title cannot be empty"
            } else if (amount.isEmpty()) {
                editTextRecordAmount.error = "Amount cannot be empty"
            } else if (amount.toString().toDouble() < 0.1) {
                editTextRecordAmount.error = "Amount cannot be 0 or less than 0"
            } else if (!amount.matches(decimalRegex))
                editTextRecordAmount.error = "Enter a decimal with two decimal places"
            else {
                val updateRecord = Record(
                    id = editRecord.id,
                    title = title,
                    image = editRecord.image,
                    description = desc.toString(),
                    amount = amount.toString().toInt(),
                    categoryId = editRecord.categoryId,
                    accountId = editRecord.accountId,
                    date = editRecord.date,
                    type = "Income"
                )
                val updateExpense = Expense(
                    expenseId = editRecord.id,
                    expense_title = title,
                    expense_icon_image = editRecord.image,
                    description = desc.toString(),
                    amount = amount.toString().toInt(),
                    categoryId = editRecord.categoryId.toLong(),
                    accountId = editRecord.accountId.toLong(),
                    date = editRecord.date
                )


                // Update the dataset in the adapter
                updateItem(position, updateRecord)

                // Update the database or perform other actions as needed
                expenseViewModel.updateExpense(updateExpense)
                budgetViewModel.updateBudgetWithExpense(updateExpense)

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


    override fun getItemCount(): Int {
        return recordList.size
    }


}