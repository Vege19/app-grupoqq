package com.grupoqq.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.grupoqq.app.R
import com.grupoqq.app.utils.makeVisible
import kotlinx.android.synthetic.main.activity_mechanic.*
import kotlinx.android.synthetic.main.layout_actionbar.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*

class MechanicActivity : AppCompatActivity() {

    companion object {
        var mechanicId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic)
        mechanicId = intent.getStringExtra("MECHANIC_KEY")!!

        toolbarSetup()
        tabsSetup()
    }

    private fun toolbarSetup() {
        mechanicToolbar.toolbar.title = "Perfil de mecánico"
    }

    private fun tabsSetup() {
        mechanicToolbar.tabLayout.makeVisible()
        mechanicToolbar.tabLayout.addTab(tabLayout.newTab().setText("Perfil"))
        mechanicToolbar.tabLayout.addTab(tabLayout.newTab().setText("Bitácoras"))
        mechanicToolbar.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) { }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> findNavController(R.id.mechanicMainFragment).navigate(R.id.mechanicProfileFragment)
                    else -> findNavController(R.id.mechanicMainFragment).navigate(R.id.mechanicBinnaclesFragment)
                }
            }
        })
    }

}
