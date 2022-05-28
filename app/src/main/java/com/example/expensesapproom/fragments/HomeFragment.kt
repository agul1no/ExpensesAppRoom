package com.example.expensesapproom.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container,false)

        val limitAmount = initializingSharedPref()
        binding.tvLimit.text = "Limit: $limitAmount €"

        itemSelectedOnSpinner = TransformingDateUtil.transformingDateFromIntToString(month,year).toString()

        //toolbar click listener add Button
        binding.toolbarHomeFragment.setOnMenuItemClickListener { menuItem ->
            navigateToAddFragment(menuItem)
        }

        binding.tvLimit.setOnClickListener {
            showUpdateLimitDialog()
        }

        expenseViewModel = ViewModelProvider(requireActivity(),ExpenseViewModelFactory(requireActivity().application)).get(ExpenseViewModel::class.java)

        val recyclerView = initializingRecyclerView(expenseViewModel)

        // Swipe to Delete
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                swipeToDeleteDialog(viewHolder, expenseViewModel)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        creatingTheDataForTheSpinner()

        //reading the info from spinner
        binding.spinnerHomeFragment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                itemSelectedOnSpinner = adapterView?.getItemAtPosition(position).toString()
                itemSelectedOnSpinner = TransformingDateUtil.transformingSpinnerInputToDate(itemSelectedOnSpinner)

                val query = "%$itemSelectedOnSpinner%"
                expenseViewModel.getTotalAmountByMonthLive(query).observe(viewLifecycleOwner, Observer { sum ->
                    if (sum == null){
                        settingTotal(0.0)
                    }else{
                        settingTotal(sum)
                    }
                })
                expenseViewModel.getAllDataFromSelectedMonth(query).observe(viewLifecycleOwner, Observer { expenseItemList->
                    adapter.setData(expenseItemList)
                })
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // left empty because it is not going to be used or needed
            }
        }

        return binding.root
    }

    private fun navigateToAddFragment (menuItem: MenuItem) : Boolean{
        when (menuItem.itemId) {
            R.id.addIcon -> {
                // Navigate to add screen
                findNavController().navigate(R.id.action_homeFragment_to_addFragment)
                return true
            }

            else -> return false
        }
    }

    private fun initializingRecyclerView(expenseViewModel: ExpenseViewModel) : RecyclerView?{
        adapter = ExpenseItemAdapter(expenseViewModel,requireContext(), this)
        val recyclerView = binding.rvHomeFragment
        recyclerView.adapter = adapter
        return recyclerView
    }

    private fun creatingTheDataForTheSpinner(){
        val spinnerList = TransformingDateUtil.creatingDataForTheSpinner(month,year)
        val arrayAdapter = ArrayAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinnerList)
        binding.spinnerHomeFragment.adapter = arrayAdapter
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

    fun initializingSharedPref(): Int {
        val sharedPref = requireActivity().getSharedPreferences("limitAmount", Context.MODE_PRIVATE)
        return sharedPref.getInt("limitAmount", 1000)
    }

    private fun editingSharedPref(): SharedPreferences.Editor {
        val sharedPref = requireActivity().getSharedPreferences("limitAmount", Context.MODE_PRIVATE)
        return sharedPref.edit()
    }

    private fun showUpdateLimitDialog() {
        val maxAmountDialog = Dialog(requireContext(),R.style.Theme_Dialog)
        maxAmountDialog.setCancelable(false)
        maxAmountDialog.setContentView(R.layout.dialog_maxamount)
        val updateButton = maxAmountDialog.findViewById<Button>(R.id.updateButton)
        val cancelButton = maxAmountDialog.findViewById<Button>(R.id.cancelButton)
        maxAmountDialog.show()

        val editor = editingSharedPref()

        cancelButton.setOnClickListener {
            maxAmountDialog.dismiss()
        }
        updateButton.setOnClickListener {
            updateButtonAction(itemSelectedOnSpinner, editor, maxAmountDialog)
        }
    }

    private fun updateButtonAction(itemSelectedOnSpinner: String, editor: SharedPreferences.Editor, maxAmountDialog: Dialog){
        try{
            val etAmount = maxAmountDialog.findViewById<EditText>(R.id.etAmountUpdate)
            val limitAmount = etAmount.text.toString().toInt()
            if(limitAmount < 100){
                throw NumberFormatException()
            }
            val query = "%$itemSelectedOnSpinner%"
            expenseViewModel.getTotalAmountByMonthLive(query).observe(viewLifecycleOwner, Observer { sum ->
                if (sum == null){
                    settingTotal(0.0)
                }else{
                    settingTotal(sum)
                }
            })
            binding.tvLimit.text = "Limit: $limitAmount €"
            etAmount.text.clear()
            editor.apply {
                putInt("limitAmount", limitAmount)
                apply()
            }
            maxAmountDialog.dismiss()
        }catch (e: NumberFormatException){
            Toast.makeText(requireContext(), "Amount must be bigger than 100 €", Toast.LENGTH_SHORT).show()
        }
    }

    private fun settingTotal(total: Double){
        if (total == 0.0) {
            binding.tvTotal.text = "Total : 0 €"
            settingPercent(itemSelectedOnSpinner)
            binding.rvHomeFragment.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
        }
        else {
            binding.tvTotal.text = "Total: $total €"
            settingPercent(itemSelectedOnSpinner)
            binding.rvHomeFragment.visibility = View.VISIBLE
            binding.tvNoData.visibility = View.GONE
        }
    }

    private fun settingPercent(date: String) {
        val limitAmount = initializingSharedPref()
        val query = "%$date%"
        expenseViewModel.getTotalAmountByMonthLive(query).observe(viewLifecycleOwner, Observer { total->
            if (total == null){
                binding.tvPercent.text = "0 %"
                binding.progressBar.progress = 0
            }else{
                binding.tvPercent.text = "${((total / limitAmount) * 100).roundToInt()} %"
                binding.progressBar.progress = ((total / limitAmount) * 100).roundToInt()
            }
        })
    }
}