package com.example.expensesapproom.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.expensesapproom.R
import com.example.expensesapproom.data.viewmodel.ExpenseViewModel
import com.example.expensesapproom.data.viewmodelfactory.ExpenseViewModelFactory
import com.example.expensesapproom.databinding.FragmentSearchBinding
import com.example.expensesapproom.expenseitemadapter.ExpenseItemAdapter

class SearchFragment : Fragment(), ExpenseItemAdapter.OnItemCLickListener {

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
        binding.tvNotFoundMessage.visibility = View.GONE

        //toolbar click listener
        binding.toolbarSearchFragment.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addIcon -> {
                    // Navigate to add screen
                    findNavController().navigate(R.id.action_searchFragment_to_addFragment)
                    true
                }
                else -> false
            }
        }

        expenseViewModel = ViewModelProvider(requireActivity(),
            ExpenseViewModelFactory(requireActivity().application)
        ).get(ExpenseViewModel::class.java)
        adapter = ExpenseItemAdapter(expenseViewModel,requireContext(),this)
        val recyclerView = binding.rvSearchFragment
        recyclerView.adapter = adapter

        expenseViewModel.getAllData().observe(requireActivity()){
            adapter.setData(it)
        }


        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchDatabase(s.toString())
                if(s.toString().isEmpty()){
                    binding.rvSearchFragment.visibility = View.GONE
                    binding.ivSearch.visibility = View.VISIBLE
                    binding.tvSearchMessage.visibility = View.VISIBLE
                    binding.tvNotFoundMessage.visibility = View.GONE
                }else{
                    binding.rvSearchFragment.visibility = View.VISIBLE
                    binding.ivSearch.visibility = View.GONE
                    binding.tvSearchMessage.visibility = View.GONE
                    binding.tvNotFoundMessage.visibility = View.GONE
                }
                if(adapter.itemCount == 0 && s.toString().isNotEmpty()){
                    binding.tvNotFoundMessage.visibility = View.VISIBLE
                }else{
                    binding.tvNotFoundMessage.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // left empty because it is not going to be used or needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // left empty because it is not going to be used or needed
            }
        })

        binding.textInputLayoutSearch.setEndIconOnClickListener {
            binding.etSearch.text?.clear()
            binding.rvSearchFragment.visibility = View.GONE
            binding.ivSearch.visibility = View.VISIBLE
            binding.tvSearchMessage.visibility = View.VISIBLE
            binding.tvNotFoundMessage.visibility = View.GONE
        }

        return binding.root
    }

    override fun onItemCLick(position: Int) {
        // left empty because it is not going to be used or needed
    }

    override fun onStart() {
        binding.etSearch.text?.clear()
        binding.rvSearchFragment.visibility = View.GONE
        binding.ivSearch.visibility = View.VISIBLE
        binding.tvSearchMessage.visibility = View.VISIBLE
        binding.tvNotFoundMessage.visibility = View.GONE
        super.onStart()
    }

    private fun searchDatabase(query: String){
        val searchQuery = "%$query%"

        expenseViewModel.findAExpenseByNameDateOrAmount(searchQuery).observe(this) { list ->
            list.let {
                adapter.setData(it)
            }
        }
    }

}