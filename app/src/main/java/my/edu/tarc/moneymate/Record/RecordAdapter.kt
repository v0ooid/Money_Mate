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
import my.edu.tarc.moneymate.Database.MonetaryAccountDao
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.Income.IncomeViewModel
import my.edu.tarc.moneymate.R
import org.w3c.dom.Text

class RecordAdapter constructor(
    private val context: Context,
    private val incomeViewModel: IncomeViewModel,
    private val recordViewModel: RecordViewModel,
    val getFragment: RecordFragment,
    private val recordList: MutableList<Record>,
    private val monetaryAccountDao: MonetaryAccountDao
) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {
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
        val incomeIcon : ImageView = itemView.findViewById(R.id.record_income_icon)
        val incomeMAccount: TextView = itemView.findViewById(R.id.incomeMAccount)
        val expenseMAccount: TextView = itemView.findViewById(R.id.expenseMAccount)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordAdapter.RecordViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.record_item_layout, parent, false)
        return RecordViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recordList.size
    }
//    private fun getAccountName(accountId: Long): String? {
//        return monetaryAccountDao.getAccountNameById(accountId)
//    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {

        if (recordList[position].type == "income") {
            holder.expenseLayout.visibility = View.GONE
            holder.incomeLayout.visibility = View.VISIBLE
            holder.incometitle.text = recordList[position].title
            holder.incomeDate.text = recordList[position].date
            holder.incomeResult.text = "RM" + recordList[position].amount.toString()
            holder.incomeDes.text = recordList[position].description
            holder.incomeIcon.setImageResource(recordList[position].image)
            recordViewModel.getAccountNameForRecord(recordList[position].accountId.toLong()).observe(getFragment.viewLifecycleOwner, { accountName ->
                holder.incomeMAccount.text = accountName ?: "Unknown Account"
            })
            val accountNamee = recordViewModel.getAccountName(recordList[position].accountId.toLong())
//            val accountName = getAccountName(recordList[position].accountId.toLong())
//            holder.incomeMAccount.text = (accountNamee?:"Unknown Acccount").toString()
            Log.d("currentdata", recordList[position].type)
        } else if (recordList[position].type == "expense") {
            holder.incomeLayout.visibility = View.GONE
            holder.expenseLayout.visibility = View.GONE
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
        val editIncome = recordList[position]

        // Open the dialog for editing
        showDialogForEdit(position, editIncome)
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
        if (type == "income") {
            val deleteIncome = Income(id, title, image, desc, amount, categoryId.toLong(), accountId.toLong(), date)

            // Call the delete method in the ViewModel to delete from the database
            incomeViewModel.deleteIncome(deleteIncome)
        }

    }

    private fun updateItem(position: Int, updateIncome: Record) {
        recordList[position] = updateIncome
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
            val amount = editTextRecordAmount.text.toString()
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
                val updateIncome = Income(
                    incomeId = editRecord.id,
                    incomeTitle = title,
                    image = editRecord.image,
                    description = desc.toString(),
                    amount = amount.toString().toInt(),
                    categoryId = editRecord.categoryId.toLong(),
                    accountId = editRecord.accountId.toLong(),
                    date = editRecord.date
                )


                // Update the dataset in the adapter
                updateItem(position, updateRecord)

                // Update the database or perform other actions as needed
                incomeViewModel.updateIncome(updateIncome)

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
}


