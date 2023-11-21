package my.edu.tarc.moneymate.Income

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.Transaction.TransactionViewModel

class IncomeAdapter constructor(private val viewModel: TransactionViewModel, val getFragment: IncomeFragment, private val categoryList_income: MutableList<Category>)
    :RecyclerView.Adapter<IncomeAdapter.ViewHolder>()
{

    private var selectedPosition:Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_gridlayout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomeAdapter.ViewHolder, position: Int) {
        holder.categoryTitle.text = categoryList_income[position].title
        holder.image.setImageResource(categoryList_income[position].image)
        viewModel.updateTitleData(categoryList_income[selectedPosition].title)

        if (position == selectedPosition)
        {
            holder.image.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.main_color))
        }else{
            holder.image.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.white))
        }
        holder.itemView.setOnClickListener{
            notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            viewModel.updateCategoryId(categoryList_income[selectedPosition].categoryId.toString())
            viewModel.updateTitleData(categoryList_income[selectedPosition].title)
            viewModel.updateCategoryImage(categoryList_income[selectedPosition].image)
            Log.d("Title",categoryList_income[selectedPosition].title )
            notifyItemChanged(selectedPosition)
//            Toast.makeText(holder.itemView.context, categoryList_income[position].title, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return categoryList_income.size
    }



    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        val image :ImageView = itemView.findViewById(R.id.category_icon)
    }

}