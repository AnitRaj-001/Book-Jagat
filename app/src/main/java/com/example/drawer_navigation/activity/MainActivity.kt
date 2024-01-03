package com.example.drawer_navigation.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.drawer_navigation.R
import com.example.drawer_navigation.adapter.about_fragment
import com.example.drawer_navigation.fragment.dashboard_fragment
import com.example.drawer_navigation.fragment.favourite_fragment
import com.example.drawer_navigation.fragment.profile_fragment
import com.google.android.material.navigation.NavigationView

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    var previousmenuitem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        coordinatorLayout = findViewById(R.id.cordinator_layout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigation_view)
        setuptoolbar()
        openDashBoard()


        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.OpenDrawer,
            R.string.closeDrawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousmenuitem != null) {
                previousmenuitem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousmenuitem = it


            when (it.itemId) {
                R.id.dashboard -> {
                    openDashBoard()

                    drawerLayout.closeDrawers()
                }

                R.id.favourite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, favourite_fragment())
                        .commit()
                    supportActionBar?.title = "Favourite"

                    drawerLayout.closeDrawers()
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, profile_fragment())
                        .commit()
                    supportActionBar?.title = "Profile"

                    drawerLayout.closeDrawers()
                }

                R.id.about -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, about_fragment())
                        .commit()
                    supportActionBar?.title = "About App"

                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setuptoolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashBoard() {
        val fragment = dashboard_fragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "Dashboard"
        navigationView.setCheckedItem(R.id.dashboard)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when (frag) {
            !is dashboard_fragment -> openDashBoard()
            else -> super.onBackPressed()
        }
    }
}

