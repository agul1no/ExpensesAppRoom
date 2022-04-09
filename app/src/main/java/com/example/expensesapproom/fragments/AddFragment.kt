package com.example.expensesapproom.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
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
    //private lateinit var dateForDatabase: String
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
            addForm()
        }

        nameFocusListener()
        amountFocusListener()

        return binding.root
    }

    private fun addForm() {
        binding.textInputLayoutName.helperText = validName()
        binding.textInputLayoutAmount.helperText = validAmount()

        val validName = binding.textInputLayoutName.helperText == "required"
        val validAmount = binding.textInputLayoutAmount.helperText == "required"
        val validDate = binding.tvDate.text.toString() != "Select a Date"

        if(validName && validAmount && validDate){
            val name = binding.etName.text.toString()
            val amount = binding.etAmount.text.toString().toDouble()
            val comment = binding.etComment.text.toString()
            val date = binding.tvDate.text.toString()
            val category = selectedCategory

            val expenseItem = ExpenseItem(0,name, amount, comment, date, category)
            expenseViewModel.insert(expenseItem)
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }else{
            invalidForm()
        }
    }

    private fun invalidForm() {
        var message = ""
        if(binding.textInputLayoutName.helperText != "required")
            message += "\n\nName should at least have 3 characters"
        if(binding.textInputLayoutAmount.helperText != "required")
            message += "\n\nThe introduced amount is invalid"
        if(binding.tvDate.text.toString() == "Select a Date")
            message += "\n\nPlease select a date"

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            //alertDialogBuilder.setTitle("Fill out following fields")
            alertDialogBuilder.setMessage(message)
            alertDialogBuilder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            alertDialogBuilder.show()
    }


    private fun amountFocusListener() {
        binding.etAmount.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.textInputLayoutName.helperText = validAmount()
            }
        }
    }

    private fun validAmount(): String? {
        if(binding.etAmount.text.toString().startsWith(".") || binding.etAmount.text.toString().endsWith(".") || binding.etAmount.text.toString().contains("..") || binding.etAmount.text.toString() == ""){
            return "Invalid Amount"
        }
        return "required"
    }

    private fun nameFocusListener() {
        binding.etName.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.textInputLayoutName.helperText = validName()
            }
        }
    }

    private fun validName(): String? {
        if(binding.etName.text.toString().length < 3){ return "invalid name" }
        return "required"
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