package com.intoverflown.pos.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.intoverflown.pos.databinding.FragmentHomeBinding
import com.intoverflown.pos.ui.inventory.InventoryActivityMain
import com.intoverflown.pos.ui.registerbranch.NewBranchActivity
import com.intoverflown.pos.ui.salesnexpenses.SalesAndExpenses

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding!!.root

        navigateFromHomeUI()

        return view
    }

    private fun navigateFromHomeUI() {
        binding!!.homeSalesExp.setOnClickListener {
            val saleExp = Intent(this.context, SalesAndExpenses::class.java)
            saleExp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(saleExp)
        }

        binding!!.homeMerchants.setOnClickListener {
            val merchant = Intent(this.context, NewBranchActivity::class.java)
            merchant.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(merchant)
        }

        binding!!.homeInventory.setOnClickListener {
            val inventory = Intent(this.context, InventoryActivityMain::class.java)
            inventory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(inventory)
        }
    }
}