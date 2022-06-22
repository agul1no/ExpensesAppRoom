package com.example.expensesapproom.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.expensesapproom.R
import com.example.expensesapproom.data.entities.ExpenseItem
import com.example.expensesapproom.data.viewmodel.ExpenseViewModel
import com.example.expensesapproom.databinding.FragmentUpdateBinding
import java.text.SimpleDateFormat
import java.util.*

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var selectedCategory: String

    private val args by navArgs<UpdateFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBinding.inflate(inflater,container,false)

        binding.toolbarUpdateFragment.title = "Update"
        binding.toolbarUpdateFragment.navigationIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_arrow_back)

        expenseViewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)

        binding.tvDateUpdate.setOnClickListener {
            datePickerDialog()
        }

        binding.toolbarUpdateFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
        }

        binding.etNameUpdate.setText(args.currentExpense.name)
        binding.etAmountUpdate.setText(args.currentExpense.amount.toString())
        binding.etCommentUpdate.setText(args.currentExpense.comment)
        binding.tvDateUpdate.setText(args.currentExpense.date)

        //reading input of the spinner
        binding.spinnerCategoryUpdate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = adapterView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.buttonUpdate.setOnClickListener {
            addForm()
        }

        nameFocusListener()
        amountFocusListener()

        return binding.root
    }

    private fun addForm() {
        binding.textInputLayoutNameUpdate.helperText = validName()
        binding.textInputLayoutAmountUpdate.helperText = validAmount()

        val validName = binding.textInputLayoutNameUpdate.helperText == "required"
        val validAmount = binding.textInputLayoutAmountUpdate.helperText == "required"
        val validDate = binding.tvDateUpdate.text.toString() != "Select a Date"

        if(validName && validAmount && validDate){
            val name = binding.etNameUpdate.text.toString()
            val amount = binding.etAmountUpdate.text.toString().toDouble()
            val comment = binding.etCommentUpdate.text.toString()
            val date = binding.tvDateUpdate.text.toString()
            val category = selectedCategory

            val expenseItem = ExpenseItem(args.currentExpense.id,name, amount, comment, date, category)
            expenseViewModel.update(expenseItem)
            findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
        }else{
            invalidForm()
        }
    }

    private fun invalidForm() {
        var message = ""
        if(binding.textInputLayoutNameUpdate.helperText != "required")
            message += "\n\nName should at least have 3 characters"
        if(binding.textInputLayoutAmountUpdate.helperText != "required")
            message += "\n\nThe introduced amount is invalid"
        if(binding.tvDateUpdate.text.toString() == "Select a Date")
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
        binding.etAmountUpdate.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.textInputLayoutNameUpdate.helperText = validAmount()
            }
        }
    }

    private fun validAmount(): String? {
        if(binding.etAmountUpdate.text.toString().startsWith(".") || binding.etAmountUpdate.text.toString().endsWith(".") || binding.etAmountUpdate.text.toString().contains("..") || binding.etAmountUpdate.text.toString() == ""){
            return "Invalid Amount"
        }
        return "required"
    }

    private fun nameFocusListener() {
        binding.etNameUpdate.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.textInputLayoutNameUpdate.helperText = validName()
            }
        }
    }

    private fun validName(): String? {
        if(binding.etNameUpdate.text.toString().length < 3){ return "invalid name" }
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
                binding.tvDateUpdate.text = "$desiredFormat"//"$selectedDay-${selectedMonth + 1}-$selectedYear"
                year = selectedYear
                month = selectedMonth
                day = selectedDay
            },
            year,
            month,
            day).show()
    }

}