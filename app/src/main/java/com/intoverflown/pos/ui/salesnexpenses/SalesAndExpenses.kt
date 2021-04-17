package com.intoverflown.pos.ui.salesnexpenses

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.intoverflown.pos.MainActivity
import com.intoverflown.pos.databinding.ActivitySalesAndExpensesBinding
import com.intoverflown.pos.ui.salesnexpenses.ui.main.SectionsPagerAdapter

class SalesAndExpenses : AppCompatActivity() {

    private var binding: ActivitySalesAndExpensesBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesAndExpensesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // pager
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding!!.viewPager // findViewById(R.id.view_pager)


        // pager adapter
        viewPager.adapter = sectionsPagerAdapter

        // call tabs id
        val tabs: TabLayout = binding!!.tabs // findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        // float btn
        val fab: FloatingActionButton = binding!!.fab // findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        binding!!.titleTabbedFragment.setOnClickListener {
            val intentBackHome = Intent(this, MainActivity::class.java)
            intentBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentBackHome)
        }
    }
}