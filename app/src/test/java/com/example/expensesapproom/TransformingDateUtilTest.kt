package com.example.expensesapproom

import com.example.expensesapproom.utils.TransformingDateUtil
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TransformingDateUtilTest {

    /**                             Tests creatingDataForTheSpinner                       */

    @Test
    internal fun `creating data for the spinner taking January (0 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(0,2022)
        assertThat(result).isEqualTo(mutableListOf("Jan / 22", "Dec / 21", "Nov / 21","Oct / 21", "Sep / 21", "Aug / 21"))
    }
    @Test
    internal fun `creating data for the spinner taking February (1 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(1,2022)
        assertThat(result).isEqualTo(mutableListOf("Feb / 22","Jan / 22", "Dec / 21", "Nov / 21","Oct / 21", "Sep / 21"))
    }
    @Test
    internal fun `creating data for the spinner taking March (2 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(2,2022)
        assertThat(result).isEqualTo(mutableListOf("Mar / 22","Feb / 22","Jan / 22", "Dec / 21", "Nov / 21","Oct / 21"))
    }
    @Test
    internal fun `creating data for the spinner taking April (3 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(3,2022)
        assertThat(result).isEqualTo(mutableListOf("Apr / 22","Mar / 22","Feb / 22","Jan / 22", "Dec / 21", "Nov / 21"))
    }
    @Test
    internal fun `creating data for the spinner taking Mai (4 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(4,2022)
        assertThat(result).isEqualTo(mutableListOf("Mai / 22", "Apr / 22","Mar / 22","Feb / 22","Jan / 22", "Dec / 21"))
    }
    @Test
    internal fun `creating data for the spinner taking June (5 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(5,2022)
        assertThat(result).isEqualTo(mutableListOf("Jun / 22", "Mai / 22", "Apr / 22","Mar / 22","Feb / 22","Jan / 22"))
    }
    @Test
    internal fun `creating data for the spinner taking July (6 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(6,2022)
        assertThat(result).isEqualTo(mutableListOf("Jul / 22", "Jun / 22", "Mai / 22", "Apr / 22","Mar / 22","Feb / 22"))
    }
    @Test
    internal fun `creating data for the spinner taking August (7 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(7,2022)
        assertThat(result).isEqualTo(mutableListOf("Aug / 22", "Jul / 22", "Jun / 22", "Mai / 22", "Apr / 22","Mar / 22"))
    }
    @Test
    internal fun `creating data for the spinner taking September (8 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(8,2022)
        assertThat(result).isEqualTo(mutableListOf("Sep / 22", "Aug / 22", "Jul / 22", "Jun / 22", "Mai / 22", "Apr / 22"))
    }
    @Test
    internal fun `creating data for the spinner taking October (9 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(9,2022)
        assertThat(result).isEqualTo(mutableListOf("Oct / 22", "Sep / 22", "Aug / 22", "Jul / 22", "Jun / 22", "Mai / 22"))
    }
    @Test
    internal fun `creating data for the spinner taking November (10 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(10,2022)
        assertThat(result).isEqualTo(mutableListOf("Nov / 22", "Oct / 22","Sep / 22", "Aug / 22", "Jul / 22", "Jun / 22"))
    }
    @Test
    internal fun `creating data for the spinner taking December (11 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(11,2022)
        assertThat(result).isEqualTo(mutableListOf("Dec / 22", "Nov / 22", "Oct / 22","Sep / 22", "Aug / 22", "Jul / 22"))
    }

    /**                        Tests transformingDateFromIntToString                                 */

    @Test
    internal fun `transforming date from int to string for the spinner taking January (0 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(0,2022)
        assertThat(result).isEqualTo("Jan / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking February (1 month) and 2021 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(1,2021)
        assertThat(result).isEqualTo("Feb / 21")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking March (2 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(2,2022)
        assertThat(result).isEqualTo("Mar / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking April (3 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(3,2022)
        assertThat(result).isEqualTo("Apr / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking Mai (4 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(4,2022)
        assertThat(result).isEqualTo("Mai / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking June (5 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(5,2022)
        assertThat(result).isEqualTo("Jun / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking July (6 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(6,2022)
        assertThat(result).isEqualTo("Jul / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking August (7 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(7,2022)
        assertThat(result).isEqualTo("Aug / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking September (8 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(8,2022)
        assertThat(result).isEqualTo("Sep / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking October (9 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(9,2022)
        assertThat(result).isEqualTo("Oct / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking November (10 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(10,2022)
        assertThat(result).isEqualTo("Nov / 22")
    }
    @Test
    internal fun `transforming date from int to string for the spinner taking December (11 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(11,2022)
        assertThat(result).isEqualTo("Dec / 22")
    }

    /**                        Tests transformingSpinnerInputToStartDate                          */

    @Test
    internal fun `transforming input from spinner Jan 21 returns a string with 01-2021`(){
        val result = TransformingDateUtil.transformingSpinnerInputToDate("Jan / 24")
        assertThat(result).isEqualTo("01-2024")
    }

    /**                        Tests formattingMonthFromSpinner                                   */

    @Test
    internal fun `formatting month from spinner input Jan 22 returns a string with Jan`(){
        val result = TransformingDateUtil.formattingMonthFromSpinner("Jan / 22")
        assertThat(result).isEqualTo("Jan")
    }

    /**                       Tests formattingYearFromSpinner                                    */

    @Test
    internal fun `formatting year from spinner input Jan 23 returns a string with 23`(){
        val result = TransformingDateUtil.formattingYearFromSpinner("Jan / 23")
        assertThat(result).isEqualTo("23")
    }
}