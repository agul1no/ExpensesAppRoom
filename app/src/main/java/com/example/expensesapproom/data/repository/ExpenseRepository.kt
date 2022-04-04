package com.example.expensesapproom.data.repository

import android.app.DownloadManager
import androidx.lifecycle.LiveData
import com.example.expensesapproom.data.db.ExpenseDao
import com.example.expensesapproom.data.entities.ExpenseItem

class ExpenseRepository (private val expenseDao: ExpenseDao) {

 //   val getAllData: LiveData<List<ExpenseItem>> = expenseDao.getAllData()


    suspend fun insert (expenseItem: ExpenseItem){
        expenseDao.insert(expenseItem)
    }

    suspend fun update (expenseItem: ExpenseItem){
        expenseDao.update(expenseItem)
    }

    suspend fun delete (expenseItem: ExpenseItem){
        expenseDao.delete(expenseItem)
    }

    fun getAllData(): LiveData<List<ExpenseItem>>{
        return expenseDao.getAllData()
    }

    fun findAExpenseByName(query: String): LiveData<List<ExpenseItem>>{
        return expenseDao.findAExpenseByName(query)
    }

    fun getAllDataFromSelectedMonth2(date:String): LiveData<List<ExpenseItem>>{
        return expenseDao.getAllDataFromSelectedMonth2(date)
    }

    fun getAllTheAmountsFromSelectedMonth(date: String): LiveData<List<Double>>{
        return expenseDao.getAllTheAmountsFromSelectedMonth(date)
    }

    fun getTotalAmountByMonthLive(date: String): LiveData<Double>{
        return expenseDao.getTotalAmountByMonthLive(date)
    }

    fun getTotalAmountByMonth(date: String): Double{
        return expenseDao.getTotalAmountByMonth(date)
    }
}