package com.intoverflown.pos.data

import android.content.Context
import android.content.Intent
import com.intoverflown.pos.api_response.LoginResponse
import com.intoverflown.pos.ui.signin.LoginActivity

class SharedPreferenceManager(context: Context) {
    private val SHARED_PREF_NAME = "volleyLogin"

    private val KEY_ID = "Id"
    private val KEY_FIRSTNAME = "FirstName"
    private val KEY_LASTNAME = "LastName"
    private val KEY_USERNAME = "Username"
    private val KEY_ROLE = "Role"
    private val KEY_TOKEN = "Token"
    private var mInstance: SharedPreferenceManager? = null
    private var ctx: Context? = null

    companion object {
        @Volatile
        private var INSTANCE: SharedPreferenceManager? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPreferenceManager(context).also {
                    INSTANCE = it
                }
            }
    }

    /** This method will store the user data in shared preferences */
    fun userLogin(loginResponse: LoginResponse) {
        val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString(KEY_ID, loginResponse.Id)
        editor?.putString(KEY_FIRSTNAME, loginResponse.FirstName)
        editor?.putString(KEY_LASTNAME, loginResponse.LastName)
        editor?.putString(KEY_USERNAME, loginResponse.Username)
        editor?.putString(KEY_ROLE, loginResponse.Role)
        editor?.putString(KEY_TOKEN, loginResponse.Token)
        editor?.apply()
    }

    /** This method will check whether user is already logged in or not */
    fun isLoggedIn(): Boolean {
        val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences?.getString(KEY_TOKEN, null) != null
    }

    /** This method will serve the logged in user  */
    fun getUser(): LoginResponse {
        val sharedPreferences = ctx!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return LoginResponse(
            sharedPreferences.getString(KEY_ID, null)!!,
            sharedPreferences.getString(KEY_FIRSTNAME, null)!!,
            sharedPreferences.getString(KEY_LASTNAME, null)!!,
            sharedPreferences.getString(KEY_USERNAME, null)!!,
            sharedPreferences.getString(KEY_ROLE, null)!!,
            sharedPreferences.getString(KEY_TOKEN, null)!!
        )
    }

    /** This method will logout the user */
    fun logout() {
        val sharedPreferences = ctx!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        ctx!!.startActivity(Intent(ctx, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }
}