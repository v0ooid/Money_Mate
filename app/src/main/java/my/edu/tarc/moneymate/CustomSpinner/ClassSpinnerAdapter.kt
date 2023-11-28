package my.edu.tarc.moneymate.CustomSpinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import my.edu.tarc.moneymate.R

class ClassSpinnerAdapter<T>(
    context: Context,
    private val itemList: List<T>,
    private val iconCallback: (T) -> Int,
    private val textCallback: (T) -> String
) : ArrayAdapter<T>(context, 0, itemList) {

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
            iconImageView.setImageResource(iconCallback(it))
            textTextView.text = textCallback(it)
        }

        return view
    }
}