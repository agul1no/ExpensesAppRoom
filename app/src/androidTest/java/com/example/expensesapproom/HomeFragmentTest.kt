package com.example.expensesapproom

import com.example.expensesapproom.fragments.HomeFragment
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class HomeFragmentTest {

    private lateinit var homeFragment: HomeFragment

    @Before
    fun setup(){
        homeFragment = HomeFragment()
    }

    @Test
    fun initializingSharedPrefTest (){
        val result = homeFragment.initializingSharedPref()
        assertThat(result).isEqualTo(1000)
    }
}