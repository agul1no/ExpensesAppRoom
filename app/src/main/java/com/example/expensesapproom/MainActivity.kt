package com.example.expensesapproom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.expensesapproom.databinding.ActivityMainBinding
import com.example.expensesapproom.fragments.DashboardFragment
import com.example.expensesapproom.fragments.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setBackgroundDrawable(ColorDrawable(R.drawable.background_nav_bottom_menu))

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == R.id.addFragment || nd.id == R.id.splashFragment || nd.id == R.id.viewPagerFragment || nd.id == R.id.updateFragment) {
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }

            if(nd.id == R.id.homeFragment && nd.id == R.id.dashboardFragment && nd.id == R.id.searchFragment){
                onBackPressed()
            }
        }

        //it hides the status bar
        //window.insetsController?.hide(WindowInsets.Type.statusBars())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.homefragment_top_menu, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}