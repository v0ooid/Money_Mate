package my.edu.tarc.moneymate.CustomSpinner

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import my.edu.tarc.moneymate.Category.Category
import my.edu.tarc.moneymate.R

class CategorySpinnerAdapter(
    context: Context,
    private val categories: List<Category>
) : ArrayAdapter<Category>(context, 0, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.category_spinner_layout, parent, false)


        val iconImageView: ImageView = view.findViewById(R.id.ivSpinnerCategoryIcon)
        val textTextView: TextView = view.findViewById(R.id.tvSpinnerCategoryName)

        item?.let {
            iconImageView.setImageResource(item.iconResId)
            textTextView.text = it.title
            Log.e("Check", item.iconResId.toString())
        }


        return view
    }
}