package my.edu.tarc.moneymate.FinancialAdvisor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.R

class FinancialAdvisorAdapter : RecyclerView.Adapter<FinancialAdvisorAdapter.YourViewHolder>() {
    private var accountFinancialHealthList: List<AccountFinancialHealth> = listOf()

    fun submitList(accountFinancialHealthList: List<AccountFinancialHealth>) {
        this.accountFinancialHealthList = accountFinancialHealthList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_item, parent, false)
        return YourViewHolder(view)
    }

    override fun onBindViewHolder(holder: YourViewHolder, position: Int) {
        holder.bind(accountFinancialHealthList[position])
    }

    override fun getItemCount(): Int = accountFinancialHealthList.size

    inner class YourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(accountFinancialHealth: AccountFinancialHealth) {
            // Bind data to your item view
            itemView.findViewById<TextView>(R.id.textViewAccountName).text = accountFinancialHealth.accountName.toString()
            itemView.findViewById<TextView>(R.id.textViewAccountBalance).text = "Balance: ${accountFinancialHealth.netBalance}"
            itemView.findViewById<TextView>(R.id.textViewFinancialStatus).text = "Status: ${accountFinancialHealth.status}"
            itemView.findViewById<TextView>(R.id.textViewFinancialTips).text = "Tips: ${accountFinancialHealth.financialTips.joinToString(", ")}"
        }
    }
}