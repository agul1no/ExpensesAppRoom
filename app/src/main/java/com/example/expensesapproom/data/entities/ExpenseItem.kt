package com.example.expensesapproom.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "expenses_table")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "expense_name")
    var name: String,
    @ColumnInfo(name = "expense_amount")
    var amount: Double,
    @ColumnInfo(name = "expense_comment")
    var comment: String,
    @ColumnInfo(name = "expense_date")
    var date: String,
    @ColumnInfo(name = "expense_category")
    var category: String
)