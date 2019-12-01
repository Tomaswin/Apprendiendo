package com.example.apprendiendo

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.gson.JsonObject
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Build
import android.provider.Settings


class HomeSection : AppCompatActivity() {
    lateinit var mDrawer: DrawerLayout
    private var toolbar: Toolbar? = null
    private var nvDrawer: NavigationView? = null
    private lateinit var listView: ListView
    private lateinit var drawerToggle: ActionBarDrawerToggle
    var interfaceClient: InterfaceClient = RestClientCall()
    var viewDialog = ViewDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_section)
        // Create a new connection of retrofit
        interfaceClient.create()
        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // This will display an Up icon (<-), we will replace it with hamburger later
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout)
        drawerToggle = setupDrawerToggle()
        drawerToggle.setDrawerIndicatorEnabled(true)
        drawerToggle.syncState()

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle)
        setupListView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission()
        }
    }

    private fun askPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, 2084)
    }

    private fun setupListView() {
        viewDialog.showDialog()
        listView = findViewById(R.id.application_list_view)
        interfaceClient.getApplications(listView, applicationContext, viewDialog)
        listView.setOnItemClickListener { adapterView, view, i, l ->
            Log.i("Me apretaron", "")
            var relativeLayout = (view as ViewGroup).getChildAt(2)
            var textView = (relativeLayout as ViewGroup).getChildAt(0)
            var textToRequest = (textView as TextView).text.toString()
            val intent = Intent(this, CourseSection::class.java)
            val bundle = Bundle()
            bundle.putString("key", textToRequest) //Your id
            intent.putExtras(bundle) //Put your id to your next Intent
            startActivity(intent)
            finish()
        }
    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    private fun setupDrawerContent(navigationView:NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
        selectDrawerItem(menuItem)
        true
        }
    }

    fun selectDrawerItem(menuItem: MenuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        var fragment: Fragment? = null
        //var fragmentClass: null
        when (menuItem.itemId) {
            //R.id.nav_first_fragment -> fragmentClass = FirstFragment::class.java
            //R.id.nav_second_fragment -> fragmentClass = SecondFragment::class.java
            //R.id.nav_third_fragment -> fragmentClass = ThirdFragment::class.java
            //else -> fragmentClass = FirstFragment::class.java
        }

        try {
          //  fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Insert the fragment by replacing any existing fragment
        val fragmentManager = supportFragmentManager
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit()

        // Highlight the selected item has been done by NavigationView
        menuItem.isChecked = true
        // Set action bar title
        title = menuItem.title
        // Close the navigation drawer
        mDrawer?.closeDrawers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (drawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig)
    }
}
