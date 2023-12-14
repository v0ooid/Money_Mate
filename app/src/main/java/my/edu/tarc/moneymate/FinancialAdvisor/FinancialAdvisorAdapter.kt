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
            itemView.findViewById<TextView>(R.id.textViewAccountName).text = accountFinancialHealth.accountName
            itemView.findViewById<TextView>(R.id.textViewAccountBalance).text = "Balance: RM " + String.format("%.2f", accountFinancialHealth.netBalance)
            itemView.findViewById<TextView>(R.id.textViewFinancialTips).text = "Tip: ${accountFinancialHealth.financialTips.first()}"

            val statusView = itemView.findViewById<TextView>(R.id.textViewFinancialStatus)
            statusView.text = "Status: ${accountFinancialHealth.status}"

            when (accountFinancialHealth.status) {
                FinancialHealthStatus.HEALTHY -> statusView.setBackgroundResource(R.drawable.status_background_healthy)
                FinancialHealthStatus.ATTENTION -> statusView.setBackgroundResource(R.drawable.status_background_attention)
                FinancialHealthStatus.DANGER -> statusView.setBackgroundResource(R.drawable.status_background_danger)
            }
        }
    }
}