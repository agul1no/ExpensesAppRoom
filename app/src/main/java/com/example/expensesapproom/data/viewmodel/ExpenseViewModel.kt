package com.example.expensesapproom.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.expensesapproom.data.db.ExpenseDatabase
import com.example.expensesapproom.data.entities.ExpenseItem
import com.example.expensesapproom.data.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseViewModel (application: Application): AndroidViewModel(application) {

    //val getAllDataFromSelectedMonth: LiveData<List<ExpenseItem>>
    //val getAllData: LiveData<List<ExpenseItem>>
    private val repository: ExpenseRepository

    init {
        val expenseDao = ExpenseDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)
        //getAllData = repository.getAllData
        //getAllDataFromSelectedMonth = repository.getAllDataFromSelectedMonth
    }

    fun insert(expenseItem: ExpenseItem){
        viewModelScope.launch (Dispatchers.IO) {
            repository.insert(expenseItem)
        }
    }

    fun update(expenseItem: ExpenseItem){
        viewModelScope.launch (Dispatchers.IO) {
            repository.update(expenseItem)
        }
    }

    fun delete(expenseItem: ExpenseItem){
        viewModelScope.launch (Dispatchers.IO) {
            repository.delete(expenseItem)
        }
    }

    fun getAllData(): LiveData<List<ExpenseItem>>{
        return repository.getAllData()
    }

    fun findAExpenseByNameDateOrAmount(query:String): LiveData<List<ExpenseItem>>{
        return repository.findAExpenseByNameDateOrAmount(query)
    }

    fun getAllDataFromSelectedMonth(date:String): LiveData<List<ExpenseItem>>{
        return repository.getAllDataFromSelectedMonth(date)
    }

    fun getTotalAmountByMonthLive(date: String): LiveData<Double>{
        return repository.getTotalAmountByMonthLive(date)
    }

    fun getTotalAmountByMonth(date: String): Double{
        return repository.getTotalAmountByMonth(date)
    }

    fun getTotalAmountByCategoryAndDate(category: String, date: String): Double{
        return repository.getTotalAmountByCategoryAndDate(category, date)
    }
}