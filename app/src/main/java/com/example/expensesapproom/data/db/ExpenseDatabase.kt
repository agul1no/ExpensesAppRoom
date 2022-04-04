package com.example.expensesapproom.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expensesapproom.data.entities.ExpenseItem

@Database (entities = [ExpenseItem::class], version = 1, exportSchema = false)
abstract class ExpenseDatabase: RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object{
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null //we want only to have an instance of the Database -> singleton

        fun getDatabase(context: Context): ExpenseDatabase{
            val temInstance = INSTANCE
            if(temInstance != null){ //if database does exist we return it
                return temInstance
            }
            synchronized(this){ // if it doesn't exist we create it
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expenses_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}