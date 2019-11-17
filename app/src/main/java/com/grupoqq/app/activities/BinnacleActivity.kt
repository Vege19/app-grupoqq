package com.grupoqq.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.grupoqq.app.R
import com.grupoqq.app.utils.makeVisible
import kotlinx.android.synthetic.main.activity_binnacle.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*

class BinnacleActivity : AppCompatActivity() {

    companion object {
        var binnacleId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binnacle)

        toolbarSetup()
        tabsSetup()

    }

    private fun toolbarSetup() {
        binnacleId = intent.getStringExtra("BINNACLE_KEY")!!
        binnacleToolbar.toolbar.title = "Bitácora"
        binnacleToolbar.toolbar.setNavigationIcon(R.drawable.ic_back)
        binnacleToolbar.toolbar.setNavigationOnClickListener { finish() }

    }

    private fun tabsSetup() {
        binnacleToolbar.tabLayout.makeVisible()
        //Add tabs
        binnacleToolbar.tabLayout.addTab(binnacleToolbar.tabLayout.newTab().setText("Detalles"))
        binnacleToolbar.tabLayout.addTab(binnacleToolbar.tabLayout.newTab().setText("Servicios"))

        binnacleToolbar.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) { }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> findNavController(R.id.binnacleMainFragment).navigate(R.id.binnacleDetailsFragment)
                    else -> findNavController(R.id.binnacleMainFragment).navigate(R.id.binnacleServicesFragment)
                }
            }
        })
    }

    override fun onBackPressed() {
        finish()
    }

}
