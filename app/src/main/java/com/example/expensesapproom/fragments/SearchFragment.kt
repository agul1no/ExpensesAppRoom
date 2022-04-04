package com.example.expensesapproom.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.expensesapproom.R
import com.example.expensesapproom.data.viewmodel.ExpenseViewModel
import com.example.expensesapproom.data.viewmodelfactory.ExpenseViewModelFactory
import com.example.expensesapproom.databinding.FragmentDashboardBinding
import com.example.expensesapproom.databinding.FragmentHomeBinding
import com.example.expensesapproom.databinding.FragmentSearchBinding
import com.example.expensesapproom.expenseitemadapter.ExpenseItemAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ExpenseItemAdapter
    private lateinit var expenseViewModel: ExpenseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater,container,false)

        binding.toolbarSearchFragment.setTitle("Find a expense")
        binding.rvSearchFragment.visibility = View.GONE
        binding.ivSearch.visibility = View.VISIBLE
        binding.tvSearchMessage.visibility = View.VISIBLE

        //toolbar click listener
        binding.toolbarSearchFragment.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addIcon -> {
                    // Navigate to settings screen
                    findNavController().navigate(R.id.action_searchFragment_to_addFragment)
                    true
                }
//                R.id.action_done -> {
//                    // Save profile changes
//                    true
//                }
                else -> false
            }
        }

        expenseViewModel = ViewModelProvider(requireActivity(),
            ExpenseViewModelFactory(requireActivity().application)
        ).get(ExpenseViewModel::class.java)
        adapter = ExpenseItemAdapter(expenseViewModel,requireContext())
        val recyclerView = binding.rvSearchFragment
        recyclerView.adapter = adapter

        expenseViewModel.getAllData().observe(requireActivity()){
            adapter.setData(it)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchDatabase(s.toString())
                binding.rvSearchFragment.visibility = View.VISIBLE
                binding.ivSearch.visibility = View.GONE
                binding.tvSearchMessage.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        return binding.root
    }

    override fun onStart() {
        binding.etSearch.text?.clear()
        binding.rvSearchFragment.visibility = View.GONE
        binding.ivSearch.visibility = View.VISIBLE
        binding.tvSearchMessage.visibility = View.VISIBLE
        super.onStart()
    }

    private fun searchDatabase(query: String){
        val searchQuery = "%$query%"

        expenseViewModel.findAExpenseByName(searchQuery).observe(this) { list ->
            list.let {
                adapter.setData(it)
            }
        }
    }

}