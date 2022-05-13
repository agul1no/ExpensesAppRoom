package com.example.expensesapproom.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
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
import com.example.expensesapproom.utils.TransformingDateUtil
import java.lang.NumberFormatException
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : Fragment(), ExpenseItemAdapter.OnItemCLickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseItemAdapter

    private lateinit var itemSelectedOnSpinner: String
    private val myCalender = Calendar.getInstance()
    private var year = myCalender.get(Calendar.YEAR)
    private var month = myCalender.get(Calendar.MONTH)

    private var totalAmount : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container,false)

        //sharedPref Limit Amount
        val sharedPref = requireActivity().getSharedPreferences("limitAmount", Context.MODE_PRIVATE)
        var limitAmount = sharedPref.getInt("limitAmount", 1000)
        binding.tvLimit.text = "Limit: ${limitAmount} €"

        itemSelectedOnSpinner = TransformingDateUtil.transformingDateFromIntToString(month,year).toString()

        //toolbar click listener
        binding.toolbarHomeFragment.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addIcon -> {
                    // Navigate to add screen
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
        adapter = ExpenseItemAdapter(expenseViewModel,requireContext(), this)
        val recyclerView = binding.rvHomeFragment
        recyclerView.adapter = adapter

        // Swipe to Delete
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                swipeToDeleteDialog(viewHolder, expenseViewModel)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        //creating and setting the data for the spinner
        var spinnerList = TransformingDateUtil.creatingDataForTheSpinner(month,year)
        var arrayAdapter = ArrayAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinnerList)
        binding.spinnerHomeFragment.adapter = arrayAdapter

        //reading the info from spinner
        binding.spinnerHomeFragment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                itemSelectedOnSpinner = adapterView?.getItemAtPosition(position).toString()
                itemSelectedOnSpinner = TransformingDateUtil.transformingSpinnerInputToStartDate(itemSelectedOnSpinner)
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
                // left empty because it is not going to be used or needed
            }
        }

        expenseViewModel.getAllData().observe(viewLifecycleOwner, Observer { expenseItem ->
            adapter.setData(expenseItem) })

        return binding.root
    }

    // onRecyclerView Item Click Listener
    override fun onItemCLick(position: Int) {
        val curExpenseItem = adapter.getExpenseAt(position)
        val action = HomeFragmentDirections.actionHomeFragmentToUpdateFragment(curExpenseItem)
        findNavController().navigate(action)
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
        val maxAmountDialog = Dialog(requireContext(),R.style.Theme_Dialog)
        maxAmountDialog.setCancelable(false)
        maxAmountDialog.setContentView(R.layout.dialog_maxamount)
        val etAmount = maxAmountDialog.findViewById<EditText>(R.id.etAmountUpdate)
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
            try{
                limitAmount = etAmount.text.toString().toInt()
                if(limitAmount < 100){
                    limitAmount = 100
                    throw NumberFormatException()
                }
                binding.tvLimit.text = "Limit: $limitAmount €"
                etAmount.text.clear()
                settingPercent(itemSelectedOnSpinner)
                editor.apply {
                    putInt("limitAmount", limitAmount)
                    apply()
                }
                maxAmountDialog.dismiss()
            }catch (e: NumberFormatException){
                Toast.makeText(requireContext(), "Amount must be bigger than 100 €", Toast.LENGTH_SHORT).show()
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
}