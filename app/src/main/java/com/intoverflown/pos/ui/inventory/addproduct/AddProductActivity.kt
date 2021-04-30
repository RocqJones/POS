package com.intoverflown.pos.ui.inventory.addproduct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.toolbox.Volley
import com.intoverflown.pos.databinding.ActivityAddProductBinding
import com.intoverflown.pos.utils.Constants
import org.json.JSONObject

class AddProductActivity : AppCompatActivity() {

    private var binding : ActivityAddProductBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

//        val uid = SharedPreferenceManager.getInstance(applicationContext).getUser()
//        val token = SharedPreferenceManager.getInstance(applicationContext).getUser().Token
//        Log.d("uid", uid.toString())
//        Log.d("token pref", token.toString())

        binding!!.addBackBtn.setOnClickListener {
//            val i = Intent(this, InventoryActivityMain::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(i)
            val url = Constants.BASE_URL + "MerchantBranch/Create"
//            addNewProduct(url, uid.toString(), token.toString())
        }
    }

    private fun addNewProduct(url: String, uid: String, token: String) {
        val queue = Volley.newRequestQueue(applicationContext)
        val jsonObject = JSONObject()


    }
}