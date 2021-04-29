package com.intoverflown.pos.ui.registerbranch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.intoverflown.pos.MainActivity
import com.intoverflown.pos.api_response.LoginResponse
import com.intoverflown.pos.data.SharedPreferenceManager
import com.intoverflown.pos.databinding.ActivityNewBranchBinding
import com.intoverflown.pos.ui.login.LoginActivity
import com.intoverflown.pos.utils.Constants
import org.json.JSONException
import org.json.JSONObject


class NewBranchActivity : AppCompatActivity() {

    private var binding: ActivityNewBranchBinding? = null
    private val context : SharedPreferenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBranchBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.upBtn.setOnClickListener {
            val x = Intent(this, MainActivity::class.java)
            x.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(x)
        }
        /** Start*/
        val sharedPreferences = this.getSharedPreferences("volleyLogin", Context.MODE_PRIVATE)
        val usr = LoginResponse(
            sharedPreferences.getString("Id", "Id"),
            sharedPreferences.getString("FirstName", "FirstName"),
            sharedPreferences.getString("LastName", "LastName"),
            sharedPreferences.getString("Username", ""),
            sharedPreferences.getString("Role", "Role"),
            sharedPreferences.getString("Token", "Token")
        )
        // Store the user in shared pref
        SharedPreferenceManager.getInstance(applicationContext).getUser(usr)
        Log.d("id", usr.Id.toString())
        Log.d("token", usr.Token.toString())
        /** End*/

//        val sharedPreferences = this.getSharedPreferences("volleyLogin", Context.MODE_PRIVATE)
//
//
//        val uid = SharedPreferenceManager.getInstance(this@NewBranchActivity).getUser().Id
//        val token = SharedPreferenceManager.getInstance(this@NewBranchActivity).getUserTest()
//        val token = SharedPreferenceManager.getInstance(applicationContext).getTestToken()
//        Log.d("uid", uid)
//        Log.d("token pref", token)

        // post new merchant branch
        binding!!.regMerchantBtn.setOnClickListener {
            val url = Constants.BASE_URL + "MerchantBranch/Create"
//            postNewMerchant(url, uid.toString(), token.toString())
        }
    }

    private fun postNewMerchant(url: String, uid: String, token: String) {
        val queue = Volley.newRequestQueue(applicationContext)
        val jsonObject = JSONObject()

        val region = binding!!.regMerchantRegion.text.toString().trim()
        val branchName = binding!!.regMerchantMerchantBranch.text.toString().trim()
        val address = binding!!.regMerchantAddress.text.toString().trim()
        val phone = binding!!.regMerchantPhone.text.toString().trim()
        val contactPerson = binding!!.regMerchantMerchantName.text.toString().trim()

        if (region.isNotEmpty() && branchName.isNotEmpty() && address.isNotEmpty()
            && phone.isNotEmpty() && contactPerson.isNotEmpty()) {
            try {
                jsonObject.put("RegionId", region)
                jsonObject.put("BranchName", branchName)
                jsonObject.put("Address", address)
                jsonObject.put("Phone", phone)
                jsonObject.put("ContactPerson", contactPerson)
                jsonObject.put("CreatedById", uid)

                Log.d("Post", jsonObject.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, url, jsonObject, Response.Listener { response ->
                    Log.d("Post ", response.toString())
                    Toast.makeText(applicationContext, "Successful", Toast.LENGTH_SHORT).show()

                    val h = Intent(this, MainActivity::class.java)
                    h.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(h)

                }, Response.ErrorListener { error ->
                    Log.e("error", error.toString())
                    Toast.makeText(applicationContext, "Registration failed!", Toast.LENGTH_SHORT)
                        .show()
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Authorization"] = "Bearer $token"
                    params["Content-Type"] = "application/json"
                    return params
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            queue.add(jsonObjectRequest)
        } else {
            Toast.makeText(applicationContext, "Fill all details!", Toast.LENGTH_SHORT).show()
        }
    }
}