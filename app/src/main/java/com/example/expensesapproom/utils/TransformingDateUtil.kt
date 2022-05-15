package com.example.expensesapproom.utils

class TransformingDateUtil () {

    companion object{

        fun creatingDataForTheSpinner(month: Int, year: Int): MutableList<String?>{
            var monthCounter = month
            var yearCounter = year
            var spinnerlist = mutableListOf<String?>()
            for (i in 0..5){
                if(monthCounter == 0){
                    spinnerlist.add(transformingDateFromIntToString(monthCounter,yearCounter))
                    monthCounter = 12
                    yearCounter --
                }else{
                    spinnerlist.add(transformingDateFromIntToString(monthCounter,yearCounter))
                }
                monthCounter --
            }
            return spinnerlist
        }

        fun transformingDateFromIntToString(month: Int, year: Int): String? {
            var monthString: String? = null

            when (month) {
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
            // 2021
            val yearStringIterator = year.toString()
            var yearString = ""
            for (i in yearStringIterator.length -1 downTo yearStringIterator.length-2) {
                yearString = yearStringIterator[i].toString() + yearString
            }
            return "$monthString / $yearString"
        }

        fun transformingSpinnerInputToDate(itemSelectedOnSpinner: String): String{
            var month = formattingMonthFromSpinner(itemSelectedOnSpinner)
            val yearIterator = formattingYearFromSpinner(itemSelectedOnSpinner)
            var year = ""
            when(month){
                "Jan" -> month = "01"
                "Feb" -> month = "02"
                "Mar" -> month = "03"
                "Apr" -> month = "04"
                "Mai" -> month = "05"
                "Jun" -> month = "06"
                "Jul" -> month = "07"
                "Aug" -> month = "08"
                "Sep" -> month = "09"
                "Oct" -> month = "10"
                "Nov" -> month = "11"
                "Dec" -> month = "12"
            }

            for (i in yearIterator.length -1 downTo yearIterator.length-2) {
                year = yearIterator[i].toString() + year
            }
            year = "20$year"

            return "$month-$year"
        }

        fun formattingMonthFromSpinner(itemSelectedOnSpinner: String): String {
            var dateOutput = ""
            var currentChar: Char
            var date = itemSelectedOnSpinner

            for(element in 0..2){
                currentChar = date[element]
                dateOutput += currentChar.toString()
            }
            return dateOutput
        }

        fun formattingYearFromSpinner(itemSelectedOnSpinner: String): String {
            var dateOutput = ""
            var currentChar: Char
            var date = itemSelectedOnSpinner
            //var date = "Jan / 21"

            for(i in date.length-1 downTo 6){
                currentChar = date[i]
                dateOutput = currentChar.toString() + dateOutput
            }
            return dateOutput
        }
    }
}


