package my.edu.tarc.moneymate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView

class IconSpinnerAdapter(context: Context, private val icons: List<Int>)
    : ArrayAdapter<Int>(context, 0, icons) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_icon, parent, false)
        val icon = view.findViewById<ImageView>(R.id.iconView) // Replace with your ImageView ID
        icon.setImageResource(icons[position])
        return view
    }
}
