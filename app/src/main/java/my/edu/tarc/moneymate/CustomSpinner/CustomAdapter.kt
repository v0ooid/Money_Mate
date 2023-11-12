package my.edu.tarc.moneymate.CustomSpinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import my.edu.tarc.moneymate.R

class IconAdapter(context: Context, private val items: List<IconItem>) :
    ArrayAdapter<IconItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_spinner_layout, parent, false)

        val iconImageView: ImageView = view.findViewById(R.id.iVSpinnerIcon)

        iconImageView.setImageResource(item?.iconResId ?: R.drawable.baseline_attach_money_24)

        return view
    }
}