package com.example.expensesapproom.data.repository

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

    fun findAExpenseByNameDateOrAmount(query: String): LiveData<List<ExpenseItem>>{
        return expenseDao.findAExpenseByNameDateOrAmount(query)
    }

    fun getAllDataFromSelectedMonth(date:String): LiveData<List<ExpenseItem>>{
        return expenseDao.getAllDataFromSelectedMonth(date)
    }

    fun getTotalAmountByMonthLive(date: String): LiveData<Double>{
        return expenseDao.getTotalAmountByMonthLive(date)
    }

    fun getTotalAmountByMonth(date: String): Double{
        return expenseDao.getTotalAmountByMonth(date)
    }
    fun getTotalAmountByCategoryAndDate(category: String, date: String): Double{
        return expenseDao.getTotalAmountByCategoryAndDate(category, date)
    }
}