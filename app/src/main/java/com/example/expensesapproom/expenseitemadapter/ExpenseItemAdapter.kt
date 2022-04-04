package com.example.expensesapproom.expenseitemadapter

import android.app.AlertDialog
import android.content.ContentProvider
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.expensesapproom.R
import com.example.expensesapproom.data.entities.ExpenseItem
import com.example.expensesapproom.data.viewmodel.ExpenseViewModel
import com.example.expensesapproom.databinding.ExpenseItemBinding
import com.example.expensesapproom.databinding.FragmentHomeBinding
import kotlinx.coroutines.NonDisposableHandle.parent

class ExpenseItemAdapter(
    private val expenseViewModel: ExpenseViewModel,
    private var context: Context
) : RecyclerView.Adapter<ExpenseItemAdapter.ExpenseViewHolder>() {

    private var expenseList = emptyList<ExpenseItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val expenseBiding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ExpenseViewHolder(expenseBiding)

//        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
//        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val curExpenseItem = expenseList[position]
        holder.bind(curExpenseItem, context, expenseViewModel)
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    fun setData(expenseItems: List<ExpenseItem>){
        this.expenseList = expenseItems
        notifyDataSetChanged()
    }

    class ExpenseViewHolder(private val expenseBiding: ExpenseItemBinding) : RecyclerView.ViewHolder(expenseBiding.root){
        fun bind(curExpenseItem: ExpenseItem, context: Context,expenseViewModel: ExpenseViewModel){
            expenseBiding.tvName.text = curExpenseItem.name
            expenseBiding.tvAmount.text = "${curExpenseItem.amount} €"
            expenseBiding.tvDate.text = curExpenseItem.date
            expenseBiding.root.setOnLongClickListener { expenseItem ->
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setTitle("Confirm Dialog")
                alertDialogBuilder.setMessage("Are you sure you want to delete ${curExpenseItem.name}?")
                alertDialogBuilder.setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                    expenseViewModel.delete(curExpenseItem)
                })
                alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                })
                alertDialogBuilder.show()
                return@setOnLongClickListener true
            }
        }
    }
}