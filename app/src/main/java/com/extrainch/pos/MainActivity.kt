package com.extrainch.pos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.extrainch.pos.databinding.ActivityMainBinding
import com.extrainch.pos.patterns.MySingleton
import com.extrainch.pos.ui.notification.NotificationActivity
import com.extrainch.pos.ui.profile.addmerchant.AddMerchantActivity
import com.extrainch.pos.utils.Constants
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    var preferences: SharedPreferences? = null
    var SHARED_PREF_NAME = "pos_pref"
    var MERCHANT_NAME = "merchantName"
    var merchantName: String? = null
    var userName: String? = null

    var editor: SharedPreferences.Editor? = null
    var MERCHANT_ID = "merchantId"

    var token: String? = null
    var merchantId: Int? = null

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

        if (preferences!!.contains("merchantId")) {
            merchantName = preferences!!.getString(MERCHANT_NAME, "merchantName")
        } else {
            val mN = Intent(this, AddMerchantActivity::class.java)
            mN.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mN)
        }

        userName = (preferences?.getString("FirstName", "FirstName")
                + " " + preferences?.getString("LastName", "LastName"))

        binding!!.merchantName.text = merchantName
        binding!!.fullName.text = userName

        editor?.remove("supplierArr")
        editor?.apply()

        getMerchantDetails()
    }

    private fun getMerchantDetails() {
        val url = Constants.BASE_URL + "Merchant/?merchantId="
        if (preferences!!.contains("merchantId")) {
            merchantId = Integer.valueOf(preferences!!.getString(MERCHANT_ID, "merchantId"))
        } else {
            val mer = Intent(this, AddMerchantActivity::class.java)
            mer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mer)
        }
        token = preferences!!.getString("Token", "Token")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url + merchantId, null, Response.Listener { response: JSONObject ->
                try {
                    Log.d("response", response.toString())
                    Log.d("get merchantName", response.getString("merchantName"))
                    Log.d("get countryId", response.getString("countryId"))
                    editor = preferences!!.edit()
                    editor!!.putString("merchantName", response.getString("merchantName"))
                    editor!!.putString("countryId", response.getString("countryId"))
                    editor!!.apply()

                    binding!!.merchantName.text = preferences!!.getString("merchantName", "merchantName")
                } catch (e: Exception) {
                    Log.i("profile", Log.getStackTraceString(e))
                }
            }, Response.ErrorListener { error: VolleyError ->
                Log.e("error", error.toString())
                Toast.makeText(
                    this,
                    "loading failed!",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json"
                params["Authorization"] = "Bearer $token"
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        MySingleton.getInstance(this.applicationContext).addToRequestQueue(jsonObjectRequest)
    }

    override fun onResume() {
        super.onResume()
        getMerchantDetails()
    }

    override fun onRestart() {
        super.onRestart()
        getMerchantDetails()
    }
}