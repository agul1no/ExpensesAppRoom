package com.example.expensesapproom.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.expensesapproom.data.entities.ExpenseItem
import com.example.expensesapproom.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ExpenseDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var expenseDatabase: ExpenseDatabase
    private lateinit var expenseDao: ExpenseDao

    @Before
    fun setup(){
        expenseDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ExpenseDatabase::class.java
        ).allowMainThreadQueries().build()
        expenseDao = expenseDatabase.expenseDao()

        // some ExpenseDao functions don't contain livedata and they can not be tested in a runningBlock
        // That's why I set the data here
        runBlocking {
            val expenseItem = ExpenseItem(1,"Test Item",1.0,"Comment","01-05-2022","Rent")
            val expenseItem2 = ExpenseItem(2,"Test Item 2",10.0,"Comment","05-05-2022","Rent")
            val expenseItem3 = ExpenseItem(3,"Test Item 3",20.0,"Comment","05-06-2022","Petrol")
            val expenseItem4 = ExpenseItem(4,"Test Item 4",50.0,"Comment","20-06-2022","Grocery Shopping")
            expenseDao.insert(expenseItem)
            expenseDao.insert(expenseItem2)
            expenseDao.insert(expenseItem3)
            expenseDao.insert(expenseItem4)
        }
    }

    @After
    fun teardown(){
        expenseDatabase.close()
    }

    @Test
    fun insert() = runBlocking {
        val expenseItem = ExpenseItem(1,"Test Item",1.0,"Comment","01-05-2022","Rent")
        expenseDao.insert(expenseItem)

        val allExpenseItems = expenseDao.getAllData().getOrAwaitValue()

        assertThat(allExpenseItems).contains(expenseItem)
    }

    @Test
    fun delete() = runBlocking {
        val expenseItem = ExpenseItem(1,"Test Item",1.0,"Comment","01-05-2022","Rent")
        expenseDao.insert(expenseItem)
        expenseDao.delete(expenseItem)

        val allExpenseItems = expenseDao.getAllData().getOrAwaitValue()

        assertThat(allExpenseItems).doesNotContain(expenseItem)
    }

    @Test
    fun getTotalAmountByMonth() {
        val totalAmountByMonth = expenseDao.getTotalAmountByMonth("%05-2022%")
        assertThat(totalAmountByMonth).isEqualTo(11.0)
    }

    @Test
    fun getTotalAmountByCategoryAndDateWithRentAndMai(){
        val totalAmountByCategoryAndDate = expenseDao.getTotalAmountByCategoryAndDate("Rent","%05-2022%")
        assertThat(totalAmountByCategoryAndDate).isEqualTo(11.0)
    }

    @Test
    fun getTotalAmountByCategoryAndDateWithPetrolAndJune(){
        val totalAmountByCategoryAndDate = expenseDao.getTotalAmountByCategoryAndDate("Petrol","%06-2022%")
        assertThat(totalAmountByCategoryAndDate).isEqualTo(20.0)
    }
}