package com.example.expensesapproom.fragments

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HomeFragmentTest{

    @Test
    fun `initializingSharedPrefTest`() {
        val result = HomeFragment().initializingSharedPref()
        assertThat(result).isEqualTo(1000)
    }
}