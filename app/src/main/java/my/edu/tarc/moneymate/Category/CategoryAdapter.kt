package my.edu.tarc.moneymate.Category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.R

class CategoryAdapter(private val items:List<Category>):
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    class CategoryViewHolder(view:View):RecyclerView.ViewHolder(view){
        val category_icon: ImageView = itemView.findViewById(R.id.category_icon)
        val category_title: TextView = itemView.findViewById(R.id.category_title)

    }

//    class CategoryViewHolder(val binding: GridItemBinding):RecyclerView.ViewHolder(binding.root) {
//
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_gridlayout,parent,false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
       holder.category_title.text = items[position].title
        holder.category_icon.setImageResource(items[position].imageResource)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}