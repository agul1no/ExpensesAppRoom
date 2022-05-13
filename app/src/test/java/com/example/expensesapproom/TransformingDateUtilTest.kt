package com.example.expensesapproom

import com.example.expensesapproom.utils.TransformingDateUtil
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TransformingDateUtilTest {

    @Test
    fun `creating data for the spinner taking January (0 month) and 2022 (year) returns a list with the past six months`(){
        val result = TransformingDateUtil.creatingDataForTheSpinner(0,2022)
        assertThat(result).isEqualTo(mutableListOf("Jan / 2022", "Dec / 2021", "Nov / 2021","Oct / 2021", "Sep / 2021", "Aug / 2021"))
    }

    @Test
    fun `transforming date from int to string for the spinner taking January (0 month) and 2022 (year) returns a string`(){
        val result = TransformingDateUtil.transformingDateFromIntToString(0,2022)
        assertThat(result).isEqualTo("Jan / 2022")
    }
}