package my.edu.tarc.moneymate.Budget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.MonetaryAccount.mAccountAdapter
import my.edu.tarc.moneymate.R

class BudgetAdapter(
    private val context: Context,
    private val budgetViewModel: BudgetViewModel
    ) : RecyclerView.Adapter<BudgetAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.ivIconAccountItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_budget, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: BudgetAdapter.MyViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}