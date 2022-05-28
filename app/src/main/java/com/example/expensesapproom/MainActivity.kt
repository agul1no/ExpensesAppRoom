package com.example.expensesapproom

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.expensesapproom.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var navDestination: NavDestination? = null

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
            navDestination = nd
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
        when(navDestination?.id){
            R.id.homeFragment -> { finish() }
            R.id.dashboardFragment -> { finish() }
            R.id.searchFragment -> { finish() }
        }
        super.onBackPressed()
    }

}