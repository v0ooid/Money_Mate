package my.edu.tarc.moneymate.Goal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SavedAmountAdapter(private var savedAmounts: List<SavedAmount>) : RecyclerView.Adapter<SavedAmountAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        fun bind(savedAmount: SavedAmount) {

            amountTextView.text = "Amount: RM ${savedAmount.amount}"
            // Format and display the date as needed
            dateTextView.text = "Date: ${formatDate(savedAmount.date)}"
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_saved_amount, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(savedAmounts[position])
    }

    override fun getItemCount(): Int = savedAmounts.size

    fun updateData(newSavedAmounts: List<SavedAmount>) {
        savedAmounts = newSavedAmounts
        notifyDataSetChanged()
    }
}
