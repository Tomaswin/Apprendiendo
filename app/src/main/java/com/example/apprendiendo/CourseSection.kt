package com.example.apprendiendo

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.net.Uri
import android.widget.Toast
import android.content.pm.PackageManager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T





class CourseSection : AppCompatActivity() {
    lateinit var mDrawer: DrawerLayout
    private var toolbar: Toolbar? = null
    private var nvDrawer: NavigationView? = null
    private lateinit var listView: ListView
    private lateinit var drawerToggle: ActionBarDrawerToggle
    var interfaceClient: InterfaceClient = RestClientCall()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_section)
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
        val b = intent.extras
        if (b != null){
            b.getString("key")?.let { setupListView(it) }
        }
    }

    private fun setupListView(textToRequest: String) {
        listView = findViewById(R.id.course_list_view)
        interfaceClient.getCourses(textToRequest, listView, applicationContext)
        listView.setOnItemClickListener { adapterView, view, i, l ->
            Log.i("Me apretaron", "")
            var relativeLayout = (view as ViewGroup).getChildAt(2)
            var textView = (relativeLayout as ViewGroup).getChildAt(0)
            var textToRequest = (textView as TextView).text.toString()



            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                startService(Intent(this, FloatingWidgetService::class.java))
                finish()
            } else if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, FloatingWidgetService::class.java))
                finish()
            } else {
                askPermission()
                Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show()
            }

            var packageName = "com." + textToRequest.toLowerCase()
            try {
                packageManager.getApplicationInfo(packageName, 0)
                var launchIntent = packageManager.getLaunchIntentForPackage("com." + textToRequest.toLowerCase())
                startActivity(launchIntent)

            } catch (e: PackageManager.NameNotFoundException) {
                //No existe esa app, indicarselo
            }
        }
    }

    private fun askPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, 2084)
    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
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

