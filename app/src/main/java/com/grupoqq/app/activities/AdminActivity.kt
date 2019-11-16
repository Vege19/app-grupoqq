package com.grupoqq.app.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.grupoqq.app.R
import com.grupoqq.app.utils.makeVisible
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*

class AdminActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        toolbarSetup()
    }

    private fun toolbarSetup() {
        adminToolbar.toolbar.title = getString(R.string.admin_title)
        tabsSetup()
    }

    private fun tabsSetup() {
        //Set tabs
        adminToolbar.tabLayout.makeVisible()
        adminToolbar.tabLayout.addTab(adminToolbar.tabLayout.newTab().setText(getString(R.string.admin_tab_binnacles)))
        adminToolbar.tabLayout.addTab(adminToolbar.tabLayout.newTab().setText(getString(R.string.admin_tab_clients)))
        adminToolbar.tabLayout.addTab(adminToolbar.tabLayout.newTab().setText(getString(R.string.admin_tab_mechanics)))

        //On tab selected listener
        adminToolbar.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                //Nothing
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> findNavController(R.id.adminMainFragment).navigate(R.id.adminBinnaclesFragment)
                    1 -> findNavController(R.id.adminMainFragment).navigate(R.id.adminClientsFragment)
                    else -> findNavController(R.id.adminMainFragment).navigate(R.id.adminMechanicsFragments)
                }
            }

        })

    }

    override fun onBackPressed() {
        finish()
    }

}
