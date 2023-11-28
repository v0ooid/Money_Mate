package my.edu.tarc.moneymate.Report

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.R

class ReportAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = mutableListOf<ReportItem>()
    private val incomeList = mutableListOf<Income>()
    private val expensesList = mutableListOf<Expense>()
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ReportItem.IncomeItem -> R.layout.report_income_item_layout
            is ReportItem.GoalItem -> R.layout.report_goal_item_layout
            is ReportItem.ExpenseItem -> R.layout.report_expense_item_layout
            else -> Log.d("item", items.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.report_income_item_layout -> IncomeViewHolder(view)
            R.layout.report_goal_item_layout -> GoalViewHolder(view)
            R.layout.report_expense_item_layout -> ExpenseViewHolder(view)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ReportItem.IncomeItem -> {
                // Bind income item to IncomeViewHolder
                (holder as? IncomeViewHolder)?.bind(item.income)
            }

            is ReportItem.GoalItem -> {
                // Bind goal item to GoalViewHolder
                (holder as? GoalViewHolder)?.bind(item.goal)
            }

            is ReportItem.ExpenseItem -> {
                // Bind expense item to ExpenseViewHolder
                (holder as? ExpenseViewHolder)?.bind(item.expense)
            }
        }
    }

    fun submitData(data: List<ReportItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }


    fun submitIncomeData(data: List<Income>) {
        Log.d("ReportAdapter", "submitIncomeData called with ${data.size} items")
        items.clear()
        items.addAll(data.map { ReportItem.IncomeItem(it) })
        notifyDataSetChanged()
    }

    fun submitExpensesData(data: List<Expense>) {
        items.clear()
        items.addAll(data.map { ReportItem.ExpenseItem(it) })
        notifyDataSetChanged()
    }

    fun submitGoalsData(data: List<Goal>) {
        items.clear()
        items.addAll(data.map { ReportItem.GoalItem(it) })
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    // Define ViewHolders for each type
    class IncomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(income: Income) {
            itemView.findViewById<TextView>(R.id.report_income_source).text = income.title
            itemView.findViewById<TextView>(R.id.report_income_date).text = income.date
            itemView.findViewById<TextView>(R.id.report_income_category).text = income.title
            itemView.findViewById<TextView>(R.id.report_income_amount).text = "RM " + income.amount.toString()
        }
    }

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(goal: Goal) {
            itemView.findViewById<TextView>(R.id.report_goal_source).text = goal.title
            itemView.findViewById<TextView>(R.id.report_goal_date).text = goal.desiredDate
            itemView.findViewById<TextView>(R.id.report_goal_targetAmount).text = "RM " + goal.targetAmount.toString()
            itemView.findViewById<TextView>(R.id.report_goal_savedAmount).text = "RM " + goal.savedAmount.toString()
        }
    }

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(expense: Expense) {
            itemView.findViewById<TextView>(R.id.report_expense_source).text = expense.title
            itemView.findViewById<TextView>(R.id.report_expense_date).text = expense.date
            itemView.findViewById<TextView>(R.id.report_expense_category).text = expense.title
            itemView.findViewById<TextView>(R.id.report_expense_amount).text = "RM " + expense.amount.toString()
        }
    }


}