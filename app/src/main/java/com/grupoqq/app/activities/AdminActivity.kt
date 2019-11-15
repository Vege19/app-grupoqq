package com.grupoqq.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayout
import com.grupoqq.app.R
import com.grupoqq.app.fragments.AdminBinnaclesFragment
import com.grupoqq.app.fragments.AdminClientsFragment
import com.grupoqq.app.fragments.AdminMechanicsFragments
import com.grupoqq.app.utils.makeVisible
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*

class AdminActivity : FragmentActivity() {

    private val binnacleFragment = AdminBinnaclesFragment()
    private val clientsFragment = AdminClientsFragment()
    private val mechanicsFragment = AdminMechanicsFragments()

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
        //Add fragments to manager
        supportFragmentManager.beginTransaction().add(binnacleFragment, "1").commit()
        supportFragmentManager.beginTransaction().add(clientsFragment, "2").commit()
        supportFragmentManager.beginTransaction().add(mechanicsFragment, "3").commit()
        //Switch tabs with tab layout
        supportFragmentManager.beginTransaction().replace(R.id.adminMainFragment, binnacleFragment) //default fragment
        //On tab selected listener
        adminToolbar.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                //Nothing
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //Nothing
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> supportFragmentManager.beginTransaction().replace(R.id.adminMainFragment, binnacleFragment)
                    1 -> supportFragmentManager.beginTransaction().replace(R.id.adminMainFragment, clientsFragment)
                    else -> supportFragmentManager.beginTransaction().replace(R.id.adminMainFragment, mechanicsFragment)
                }
            }

        })

    }

}
