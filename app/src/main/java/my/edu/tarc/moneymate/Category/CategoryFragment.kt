package my.edu.tarc.moneymate.Category

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.moneymate.Expense.Expense
import my.edu.tarc.moneymate.Income.Income
import my.edu.tarc.moneymate.R

class CategoryFragment : Fragment() {

    companion object {
        fun newInstance() = CategoryFragment()
    }
    private var categorylist_income = mutableListOf<Income>()
    private var categorylist_expense = mutableListOf<Expense>()
    private lateinit var viewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}