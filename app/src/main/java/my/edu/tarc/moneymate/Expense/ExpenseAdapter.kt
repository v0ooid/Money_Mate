package my.edu.tarc.moneymate.Expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.R

class ExpenseAdapter(private val categoryExpenseList: MutableList<Expense>):
    RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    private var selectedPosition:Int = 0
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        //tell which is the template layout(category_item_gridlayout)
        val categoryTitle:TextView = itemView.findViewById(R.id.category_title)
        val image: ImageView = itemView.findViewById(R.id.category_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_gridlayout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryExpenseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Set value into the field
        holder.categoryTitle.text = categoryExpenseList[position].expense_title
        holder.image.setImageResource(categoryExpenseList[position].expense_icon_image)
        if (position == selectedPosition)
        {
            holder.image.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.main_color))
        }else{
            holder.image.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.white))
        }
        holder.itemView.setOnClickListener{
            notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
//            Toast.makeText(holder.itemView.context, categoryList_income[position].title, Toast.LENGTH_SHORT).show()
        }
    }
}