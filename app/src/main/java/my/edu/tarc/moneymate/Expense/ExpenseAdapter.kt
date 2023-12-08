package my.edu.tarc.moneymate.Expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transaction.TransactionFragment
import my.edu.tarc.moneymate.Transaction.TransactionViewModel

class ExpenseAdapter(private val viewModel: TransactionViewModel, val getFragment: ExpenseFragment,private val categoryExpenseList: MutableList<Category>):
    RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    init {
        if (categoryExpenseList.isNotEmpty()) {
            viewModel.updateCategoryId(categoryExpenseList[0].categoryId.toString())
            viewModel.updateTitleData(categoryExpenseList[0].title)
            viewModel.updateCategoryImage(categoryExpenseList[0].image)
        }
    }


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
        holder.categoryTitle.text = categoryExpenseList[position].title
        holder.image.setImageResource(categoryExpenseList[position].image)
        viewModel.updateTitleData(categoryExpenseList[selectedPosition].title)

        if (position == selectedPosition)
        {
            holder.image.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.main_color))
        }else{
            holder.image.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.white))
        }
        holder.itemView.setOnClickListener{
            notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            viewModel.updateCategoryId(categoryExpenseList[selectedPosition].categoryId.toString())
            viewModel.updateTitleData(categoryExpenseList[selectedPosition].title)
            viewModel.updateCategoryImage(categoryExpenseList[selectedPosition].image)
            notifyItemChanged(selectedPosition)
//            Toast.makeText(holder.itemView.context, categoryList_income[position].title, Toast.LENGTH_SHORT).show()
        }
    }
}