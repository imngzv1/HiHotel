package com.example.hihotel

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hihotel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)
        updateAdminMenuVisibility()

    }

    fun updateAdminMenuVisibility() {
        val userStatus = getUserStatus()
        val menu = binding.navView.menu
        val adminFragment = menu.findItem(R.id.navigation_admin)
        adminFragment.isVisible = userStatus == "admin"
        binding.navView.invalidate()
    }


    private fun getUserStatus(): String? {
        val sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_status", "user")
    }


    fun hideBottomNav() {
        binding.navView.visibility = View.GONE
    }

    fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE
    }


}