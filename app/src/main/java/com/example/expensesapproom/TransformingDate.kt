package com.example.expensesapproom

import java.util.*

class TransformingDate {

}

fun creatingDataForTheSpinner(): MutableList<String?>{
    /** Creating the mutableListOf Month/Year **/
    val calendar = Calendar.getInstance()
    var month = (calendar.get(Calendar.MONTH)) /** January = 0, February = 1 etc... **/
    var year = calendar.get(Calendar.YEAR)

    var spinnerlist = mutableListOf<String?>()
    for (i in 0..5){
        if(month == 0){
            spinnerlist.add(transformingDateFromIntToString(month,year))
            month = 12
            year -= 1
        }else{
            spinnerlist.add(transformingDateFromIntToString(month,year))
        }
        month -= 1
    }
    return spinnerlist
}

fun transformingDateFromIntToString(month: Int, year: Int): String? {
    var monthString: String? = null
    var yearString: String? = null
    when(month){
        0 -> monthString = "Jan"
        1 -> monthString = "Feb"
        2 -> monthString = "Mar"
        3 -> monthString = "Apr"
        4 -> monthString = "Mai"
        5 -> monthString = "Jun"
        6 -> monthString = "Jul"
        7 -> monthString = "Aug"
        8 -> monthString = "Sep"
        9 -> monthString = "Oct"
        10 -> monthString = "Nov"
        11 -> monthString = "Dec"
    }
    when(year){
        2021 -> yearString = "21"
        2022 -> yearString = "22"
        2023 -> yearString = "23"
        2024 -> yearString = "24"
        2025 -> yearString = "25"
        2026 -> yearString = "26"
        2027 -> yearString = "27"
        2028 -> yearString = "28"
        2029 -> yearString = "29"
        2030 -> yearString = "30"
    }
    return "$monthString / $yearString"
}