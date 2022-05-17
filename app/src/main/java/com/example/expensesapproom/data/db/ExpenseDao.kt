package com.example.expensesapproom.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.expensesapproom.data.entities.ExpenseItem

@Dao
interface ExpenseDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseItem: ExpenseItem)

    @Update
    suspend fun update (expenseItem: ExpenseItem)

    @Delete
    suspend fun delete (expenseItem: ExpenseItem)

    @Query ("SELECT * FROM expenses_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<ExpenseItem>>

    @Query ("SELECT * FROM expenses_table WHERE expense_name LIKE :query OR expense_date LIKE :query OR expense_amount LIKE :query")
    fun findAExpenseByNameDateOrAmount(query: String): LiveData<List<ExpenseItem>>

    @Query ("SELECT * FROM expenses_table WHERE expense_date LIKE :date ORDER BY id DESC")
    fun getAllDataFromSelectedMonth(date:String): LiveData<List<ExpenseItem>>

    @Query ("SELECT expense_amount FROM expenses_table WHERE expense_date LIKE :date")
    fun getAllTheAmountsFromSelectedMonth2(date: String): List<Double>

    @Query ("SELECT SUM(expense_amount) FROM expenses_table WHERE expense_date LIKE :date")
    fun getTotalAmountByMonthLive(date:String): LiveData<Double>

    @Query ("SELECT SUM(expense_amount) FROM expenses_table WHERE expense_date LIKE :date")
    fun getTotalAmountByMonth(date:String): Double

    @Query("SELECT SUM(expense_amount) FROM expenses_table WHERE expense_category LIKE :category AND expense_date LIKE :date")
    fun getTotalAmountByCategoryAndDate(category: String, date: String): Double
}