package com.example.expensesapproom.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.expensesapproom.R
import com.example.expensesapproom.callbacks.SwipeToDeleteCallback
import com.example.expensesapproom.data.viewmodel.ExpenseViewModel
import com.example.expensesapproom.data.viewmodelfactory.ExpenseViewModelFactory
import com.example.expensesapproom.databinding.FragmentHomeBinding
import com.example.expensesapproom.expenseitemadapter.ExpenseItemAdapter
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseItemAdapter

    private lateinit var itemSelectedOnSpinner: String
    private val myCalender = Calendar.getInstance()
    private var year = myCalender.get(Calendar.YEAR)
    private var month = myCalender.get(Calendar.MONTH)

    private var totalAmount : Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container,false)

        //sharedPref Limit Account
        val sharedPref = requireActivity().getSharedPreferences("limitAmount", Context.MODE_PRIVATE)
        var limitAmount = sharedPref.getInt("limitAmount", 1000)
        binding.tvLimit.text = "Limit: ${limitAmount} €"

        itemSelectedOnSpinner = transformingDateFromIntToString(month,year).toString()

        //toolbar click listener
        binding.toolbarHomeFragment.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addIcon -> {
                    // Navigate to settings screen
                    findNavController().navigate(R.id.action_homeFragment_to_addFragment)
                    true
                }

                else -> false
            }
        }

        //tvLimit Click listener
        binding.tvLimit.setOnClickListener {
            showUpdateLimitDialogAndSetSharedPref()
        }

        //viewModel and Recyclerview initialization
        expenseViewModel = ViewModelProvider(requireActivity(),ExpenseViewModelFactory(requireActivity().application)).get(ExpenseViewModel::class.java)
        adapter = ExpenseItemAdapter(expenseViewModel,requireContext())
        val recyclerView = binding.rvHomeFragment
        recyclerView.adapter = adapter

        // Swipe to Delete
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                swipeToDeleteDialog(viewHolder, expenseViewModel)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        //creating and setting the data for the spinner
        var spinnerList = creatingDataForTheSpinner()
        var arrayAdapter = ArrayAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinnerList)
        binding.spinnerHomeFragment.adapter = arrayAdapter

        //reading the info from spinner
        binding.spinnerHomeFragment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                itemSelectedOnSpinner = adapterView?.getItemAtPosition(position).toString()
                itemSelectedOnSpinner = transformingSpinnerInputToStartDate(itemSelectedOnSpinner)
                //Log.d("itemSelectedOnSpinner", itemSelectedOnSpinner)
                searchDatabaseWithSpinnerInput(itemSelectedOnSpinner)
                val query = "%$itemSelectedOnSpinner%"
                expenseViewModel.getTotalAmountByMonthLive(query).observe(viewLifecycleOwner, Observer { sum ->
                    if (sum == null) {
                        binding.tvTotal.text = "Total : 0 €"
                        settingPercent(itemSelectedOnSpinner)
                        binding.rvHomeFragment.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    else {
                        binding.tvTotal.text = "Total: $sum €"
                        settingPercent(itemSelectedOnSpinner)
                        binding.rvHomeFragment.visibility = View.VISIBLE
                        binding.tvNoData.visibility = View.GONE
                    }
                })
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

        expenseViewModel.getAllData().observe(viewLifecycleOwner, Observer { expenseItem ->
            adapter.setData(expenseItem) })

        return binding.root
    }

    private fun swipeToDeleteDialog(viewHolder: RecyclerView.ViewHolder, expenseViewModel: ExpenseViewModel){
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Delete?")
        alertDialogBuilder.setMessage("Are you sure you want to delete ${adapter.getExpenseAt(viewHolder.adapterPosition).name}?")
        alertDialogBuilder.setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialogInterface, i ->

                expenseViewModel.delete(adapter.getExpenseAt(viewHolder.adapterPosition))
            })
        alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                val position = viewHolder.adapterPosition
                adapter.notifyItemChanged(position)
        })
        alertDialogBuilder.show()
    }

    private fun showUpdateLimitDialogAndSetSharedPref() {
        binding.tvLimit.setOnClickListener {
            val maxAmountDialog = Dialog(requireContext(),R.style.Theme_Dialog)
            maxAmountDialog.setCancelable(false)
            maxAmountDialog.setContentView(R.layout.dialog_maxamount)
            val etAmount = maxAmountDialog.findViewById<EditText>(R.id.etAmount)
            val updateButton = maxAmountDialog.findViewById<Button>(R.id.updateButton)
            val cancelButton = maxAmountDialog.findViewById<Button>(R.id.cancelButton)
            maxAmountDialog.show()
            //sharedPref Limit Account
            val sharedPref = requireActivity().getSharedPreferences("limitAmount", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            var limitAmount: Int

            cancelButton.setOnClickListener {
                maxAmountDialog.dismiss()
            }
            updateButton.setOnClickListener {
                limitAmount = etAmount.text.toString().toInt()
                binding.tvLimit.text = "Limit: $limitAmount €"
                etAmount.text.clear()
                settingPercent(itemSelectedOnSpinner)
                editor.apply {
                    putInt("limitAmount", limitAmount)
                    apply()
                }
                maxAmountDialog.dismiss()
            }
        }
    }

    private fun settingPercent(date: String) {
        val sharedPref = requireActivity().getSharedPreferences("limitAmount", Context.MODE_PRIVATE)
        var limitAmount = sharedPref.getInt("limitAmount", 1000)
        val query = "%$date%"
        val total = expenseViewModel.getTotalAmountByMonth(query)
        val percentage: Int = ((total / limitAmount) * 100).roundToInt()
        binding.tvPercent.text = "$percentage %"
        binding.progressBar.progress = percentage
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun calculateSumFromSelectedMonth (itemSelectedOnSpinner: String) : Double {
        expenseViewModel = ViewModelProvider(requireActivity(),ExpenseViewModelFactory(requireActivity().application)).get(ExpenseViewModel::class.java)
        var date = "%$itemSelectedOnSpinner%"
        var total = 0.0
        expenseViewModel.getAllTheAmountsFromSelectedMonth(date).observe(this) { list ->
            //totalAmount = list.sum()
            for (item in list) {
                total += item
                totalAmount = total
            }
            binding.tvTotal.text = "Total: $total €"
        }
        return totalAmount
    }

    private fun searchDatabaseWithSpinnerInput (itemSelectedOnSpinner: String) {
        var date = "%$itemSelectedOnSpinner%"
        expenseViewModel.getAllDataFromSelectedMonth2(date).observe(this) { list ->
            list.let {
                adapter.setData(it)
                }
        }
    }


    private fun creatingDataForTheSpinner(): MutableList<String?>{
        /** Creating the mutableListOf Month/Year **/
        val calendar = Calendar.getInstance()
        var month = (calendar.get(Calendar.MONTH)) /** January = 0, February = 1 etc... **/
        var year = calendar.get(Calendar.YEAR)

        var spinnerlist = mutableListOf<String?>()
        for (i in 0..5){
            if(month == 0){
                spinnerlist.add(transformingDateFromIntToString(month,year))
                month = 12
                year -= 1
            }else{
                spinnerlist.add(transformingDateFromIntToString(month,year))
            }
            month -= 1
        }
        return spinnerlist
    }

    private fun transformingDateFromIntToString(month: Int, year: Int): String? {
        var monthString: String? = null
        var yearString: String? = null
        when(month){
            0 -> monthString = "Jan"
            1 -> monthString = "Feb"
            2 -> monthString = "Mar"
            3 -> monthString = "Apr"
            4 -> monthString = "Mai"
            5 -> monthString = "Jun"
            6 -> monthString = "Jul"
            7 -> monthString = "Aug"
            8 -> monthString = "Sep"
            9 -> monthString = "Oct"
            10 -> monthString = "Nov"
            11 -> monthString = "Dec"
        }
        when(year){
            2021 -> yearString = "21"
            2022 -> yearString = "22"
            2023 -> yearString = "23"
            2024 -> yearString = "24"
            2025 -> yearString = "25"
            2026 -> yearString = "26"
            2027 -> yearString = "27"
            2028 -> yearString = "28"
            2029 -> yearString = "29"
            2030 -> yearString = "30"
        }
        return "$monthString / $yearString"
    }

    private fun transformingSpinnerInputToEndDate(itemSelectedOnSpinner: String): String{
        // Jan / 22
        var month = formattingMonthFromSpinner(itemSelectedOnSpinner)
        var year = formattingYearFromSpinner(itemSelectedOnSpinner)
        when(month){
            "Jan" -> month = "01-31"
            "Feb" -> month = "02-28"
            "Mar" -> month = "03-31"
            "Apr" -> month = "04-30"
            "Mai" -> month = "05-31"
            "Jun" -> month = "06-30"
            "Jul" -> month = "07-31"
            "Aug" -> month = "08-31"
            "Sep" -> month = "09-30"
            "Oct" -> month = "10-31"
            "Nov" -> month = "11-30"
            "Dec" -> month = "12-31"
        }
        when(year){
            "21" -> year = "2021"
            "22" -> year = "2022"
            "23" -> year = "2023"
            "24" -> year = "2024"
            "25" -> year = "2025"
            "26" -> year = "2026"
            "27" -> year = "2027"
            "28" -> year = "2028"
            "29" -> year = "2029"
            "30" -> year = "2030"
        }
        return "$year-$month"
    }

    private fun transformingSpinnerInputToStartDate(itemSelectedOnSpinner: String): String{
        // Jan / 22
        var month = formattingMonthFromSpinner(itemSelectedOnSpinner)
        var year = formattingYearFromSpinner(itemSelectedOnSpinner)
        when(month){
            "Jan" -> month = "01"
            "Feb" -> month = "02"
            "Mar" -> month = "03"
            "Apr" -> month = "04"
            "Mai" -> month = "05"
            "Jun" -> month = "06"
            "Jul" -> month = "07"
            "Aug" -> month = "08"
            "Sep" -> month = "09"
            "Oct" -> month = "10"
            "Nov" -> month = "11"
            "Dec" -> month = "12"
        }
        when(year){
            "21" -> year = "2021"
            "22" -> year = "2022"
            "23" -> year = "2023"
            "24" -> year = "2024"
            "25" -> year = "2025"
            "26" -> year = "2026"
            "27" -> year = "2027"
            "28" -> year = "2028"
            "29" -> year = "2029"
            "30" -> year = "2030"
        }
        return "$month-$year"
    }

    private fun formattingMonthFromSpinner(itemSelectedOnSpinner: String): String {
        var dateOutput = ""
        var currentChar: Char
        var date = itemSelectedOnSpinner

        for(element in 0..2){
            currentChar = date[element]
            dateOutput = dateOutput + currentChar.toString()
        }
        return dateOutput
    }

    private fun formattingYearFromSpinner(itemSelectedOnSpinner: String): String {
        var dateOutput = ""
        var currentChar: Char
        var date = itemSelectedOnSpinner
        //var date = "Jan / 21"

        for(i in date.length-1 downTo 6){
            currentChar = date[i]
            dateOutput = currentChar.toString() + dateOutput
        }
        return dateOutput
    }


}