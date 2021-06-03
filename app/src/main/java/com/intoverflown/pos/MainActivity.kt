package com.intoverflown.pos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.intoverflown.pos.databinding.ActivityMainBinding
import com.intoverflown.pos.ui.notification.NotificationActivity
import com.intoverflown.pos.ui.profile.addmerchant.AddMerchantActivity

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    var preferences: SharedPreferences? = null
    var SHARED_PREF_NAME = "pos_pref"
    var MERCHANT_NAME = "merchantName"
    var merchantName: String? = null
    var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)  //setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = binding!!.navView //findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.navigation_home, R.id.navigation_settings, R.id.navigation_profile))
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding!!.mainNotification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)

        if (preferences!!.contains("merchantName")) {
            merchantName = preferences?.getString(MERCHANT_NAME, "merchantName")
        } else {
            val mN = Intent(this, AddMerchantActivity::class.java)
            mN.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mN)
        }

        userName = (preferences?.getString("FirstName", "FirstName")
                + " " + preferences?.getString("LastName", "LastName"))

        binding!!.merchantName.text = merchantName
        binding!!.fullName.text = userName
    }
}