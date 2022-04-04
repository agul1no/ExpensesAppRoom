package com.example.expensesapproom.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.expensesapproom.MainActivity
import com.example.expensesapproom.R
import com.example.expensesapproom.data.entities.ExpenseItem
import com.example.expensesapproom.data.viewmodel.ExpenseViewModel
import com.example.expensesapproom.databinding.FragmentAddBinding
import com.example.expensesapproom.databinding.FragmentSearchBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_add.*
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var dateForDatabase: String
    private lateinit var selectedCategory: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater,container,false)

        binding.toolbarAddFragment.setTitle("Add a expense")

        expenseViewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)

        binding.tvDate.setOnClickListener {
            datePickerDialog()
        }

        //reading input of the spinner
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = adapterView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.buttonAdd.setOnClickListener {
            val name = binding.etName.text.toString()
            val amount = binding.etAmount.text.toString().toDouble()
            val comment = binding.etComment.text.toString()
            val date = binding.tvDate.text.toString()
            val category = selectedCategory

            val expenseItem = ExpenseItem(0,name, amount, comment, date, category)
            expenseViewModel.insert(expenseItem)
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)

        }

        return binding.root

    }

    private fun datePickerDialog() {

        val myCalender = Calendar.getInstance()
        var year = myCalender.get(Calendar.YEAR)
        var month = myCalender.get(Calendar.MONTH)
        var day = myCalender.get(Calendar.DAY_OF_MONTH)

        var myDate = DatePickerDialog(requireContext(),
            DatePickerDialog.OnDateSetListener { datePicker, selectedYear, selectedMonth, selectedDay ->
                val formatter = SimpleDateFormat("dd-MM-yyyy")
                val dateToFormat = "$selectedDay-${selectedMonth+1}-$selectedYear"
                val formattedDate = formatter.parse(dateToFormat)
                val desiredFormat = SimpleDateFormat("dd-MM-yyyy").format(formattedDate)
                binding.tvDate.text = "$desiredFormat"//"$selectedDay-${selectedMonth + 1}-$selectedYear"
                year = selectedYear
                month = selectedMonth
                day = selectedDay
            },
            year,
            month,
            day).show()
    }

}