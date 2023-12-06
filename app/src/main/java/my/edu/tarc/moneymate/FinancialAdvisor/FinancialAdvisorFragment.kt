package my.edu.tarc.moneymate.FinancialAdvisor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentFinancialAdvisorBinding


class FinancialAdvisorFragment : Fragment() {

    private var _binding: FragmentFinancialAdvisorBinding?= null
    private val binding get() = _binding!!
//    private val viewModel: FinancialAdvisorViewModel by viewModels()
private lateinit var viewModel: FinancialAdvisorViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FinancialAdvisorAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFinancialAdvisorBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerViewAccounts)
        adapter = FinancialAdvisorAdapter() // Replace with your adapter instantiation
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val factory = FinancialAdvisorViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(FinancialAdvisorViewModel::class.java)

        observeViewModel()
        viewModel.accountsFinancialHealth.observe(viewLifecycleOwner) { accountsHealth ->
            accountsHealth.forEach { accountHealth ->
                Log.d("Fragmetn accoutn heal", accountHealth.toString())
            }
        }
        binding.leftIcon.setOnClickListener{
            findNavController().navigateUp()
        }


    }

    private fun observeViewModel() {
        viewModel.accountsFinancialHealth.observe(viewLifecycleOwner, { accounts ->
            Log.d("Fragment", accounts.toString())
            adapter.submitList(accounts)
        })

    }


}