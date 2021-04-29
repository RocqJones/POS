package com.intoverflown.pos.data

import android.content.Context
import android.content.Intent
import android.drm.DrmInfoRequest.ACCOUNT_ID
import com.intoverflown.pos.api_response.LoginResponse
import com.intoverflown.pos.ui.login.LoginActivity


class SharedPreferenceManager(context: Context) {
    private val SHARED_PREF_NAME = "pos_pref"

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

    /** START TESTING */
    fun userLoginTest(tk: String) {
        val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString(KEY_TOKEN, "Token")
        editor?.apply()
    }

    fun getUserTest() {
        val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences?.getString(KEY_TOKEN, "Token")
    }

    var sharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun getTestId(): String? {
        return if (sharedPref.getString(KEY_ID, null) != null)
            sharedPref.getString(KEY_ID, null) else null
    }

    fun getTestToken(): String? {
        return if (sharedPref.getString(KEY_TOKEN, null) != null)
            sharedPref.getString(KEY_TOKEN, null) else null
    }
    /** END TESTING */

    /** This method will check whether user is already logged in or not */
    fun isLoggedIn(): Boolean {
        val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences?.getString(KEY_TOKEN, null) != null
    }

    /** This method will serve the logged in user  */
    fun getUser(response: LoginResponse) {
        val sharedPreferences = ctx?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences?.getString(KEY_ID, response.Id)
        sharedPreferences?.getString(KEY_FIRSTNAME, response.FirstName)
        sharedPreferences?.getString(KEY_LASTNAME, response.LastName)
        sharedPreferences?.getString(KEY_USERNAME, response.Username)
        sharedPreferences?.getString(KEY_ROLE, response.Role)
        sharedPreferences?.getString(KEY_TOKEN, response.Token)
    }

    /** This method will logout the user */
    fun logout() {
        val sharedPreferences = ctx!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        ctx!!.startActivity(
            Intent(
                ctx,
                LoginActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }
}