package com.example.expensesapproom.utils

class TransformingDateUtil () {

    companion object{

        fun creatingDataForTheSpinner(month: Int, year: Int): MutableList<String?>{
            /** Creating the mutableListOf Month/Year **/
//            val calendar = Calendar.getInstance()
//            var month = (calendar.get(Calendar.MONTH)) /** January = 0, February = 1 etc... **/
//            var year = calendar.get(Calendar.YEAR)
            var monthCounter = month
            var yearCounter = year
            var spinnerlist = mutableListOf<String?>()
            for (i in 0..5){
                if(month == 0){
                    spinnerlist.add(transformingDateFromIntToString(monthCounter,yearCounter))
                    monthCounter = 12
                    yearCounter -= 1
                }else{
                    spinnerlist.add(transformingDateFromIntToString(monthCounter,yearCounter))
                }
                monthCounter -= 1
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
            val yearStringIterator = year.toString()
            var yearString = ""
            for (char in yearStringIterator.last() downTo yearStringIterator.last() - 1) {
                yearString += char.toString()
            }
//            when (year) {
//                2021 -> yearStringIterator = "21"
//                2022 -> yearStringIterator = "22"
//                2023 -> yearStringIterator = "23"
//                2024 -> yearStringIterator = "24"
//                2025 -> yearStringIterator = "25"
//                2026 -> yearStringIterator = "26"
//                2027 -> yearStringIterator = "27"
//                2028 -> yearStringIterator = "28"
//                2029 -> yearStringIterator = "29"
//                2030 -> yearStringIterator = "30"
//            }
            return "$monthString / $yearStringIterator"
        }

        fun transformingSpinnerInputToEndDate(itemSelectedOnSpinner: String): String{
            // Jan / 22
            var month = formattingMonthFromSpinner(itemSelectedOnSpinner)
            var year = formattingYearFromSpinner(itemSelectedOnSpinner)
            when(month){
                "Jan" -> month = "01-31"
                "Feb" -> month = "02-28"
                "Mar" -> month = "03-31"
                "Apr" -> month = "04-30"
                "Mai" -> month = "05-31"
                "Jun" -> month = "06-30"
                "Jul" -> month = "07-31"
                "Aug" -> month = "08-31"
                "Sep" -> month = "09-30"
                "Oct" -> month = "10-31"
                "Nov" -> month = "11-30"
                "Dec" -> month = "12-31"
            }
            when(year){
                "21" -> year = "2021"
                "22" -> year = "2022"
                "23" -> year = "2023"
                "24" -> year = "2024"
                "25" -> year = "2025"
                "26" -> year = "2026"
                "27" -> year = "2027"
                "28" -> year = "2028"
                "29" -> year = "2029"
                "30" -> year = "2030"
            }
            return "$year-$month"
        }

        fun transformingSpinnerInputToStartDate(itemSelectedOnSpinner: String): String{
            // Jan / 22
            var month = formattingMonthFromSpinner(itemSelectedOnSpinner)
            var year = formattingYearFromSpinner(itemSelectedOnSpinner)
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
            when(year){
                "21" -> year = "2021"
                "22" -> year = "2022"
                "23" -> year = "2023"
                "24" -> year = "2024"
                "25" -> year = "2025"
                "26" -> year = "2026"
                "27" -> year = "2027"
                "28" -> year = "2028"
                "29" -> year = "2029"
                "30" -> year = "2030"
            }
            return "$month-$year"
        }

        fun formattingMonthFromSpinner(itemSelectedOnSpinner: String): String {
            var dateOutput = ""
            var currentChar: Char
            var date = itemSelectedOnSpinner

            for(element in 0..2){
                currentChar = date[element]
                dateOutput = dateOutput + currentChar.toString()
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


