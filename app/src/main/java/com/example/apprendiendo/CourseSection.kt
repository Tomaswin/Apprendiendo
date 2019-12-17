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
import android.R.string.cancel
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_contact.view.*


class CourseSection : AppCompatActivity() {
    lateinit var mDrawer: DrawerLayout
    private var toolbar: Toolbar? = null
    private var nvDrawer: NavigationView? = null
    private lateinit var listView: ListView
    private lateinit var drawerToggle: ActionBarDrawerToggle
    var interfaceClient: InterfaceClient = RestClientCall()
    var viewDialog = ViewDialog(this)
    var returnVal = false
    lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_section)
        // Create a new connection of retrofit
        interfaceClient.create()
        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar)
        toolbar?.title = "Seleccionar curso"
        setSupportActionBar(toolbar)
        nvDrawer = findViewById(R.id.nvView)
        setupDrawerContent(nvDrawer!!)

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
        if (b != null) {
            b.getString("key")?.let { setupListView(it) }
        }
    }

    private fun setupListView(applicationName: String) {
        viewDialog.showDialog()
        listView = findViewById(R.id.course_list_view)
        interfaceClient.getCourses(applicationName, listView, applicationContext, viewDialog)
        listView.setOnItemClickListener { adapterView, view, i, l ->
            var relativeLayout = (view as ViewGroup).getChildAt(2)
            var textView = (relativeLayout as ViewGroup).getChildAt(0)
            var textToRequest = (textView as TextView).text.toString()
            var installApp = true

            var packageName = "com." + applicationName.toLowerCase()
            if (!openApp(packageName)) {
                installApp = false
                installTheApp(packageName)
            }

            if (installApp) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && installApp) {
                    var intent = Intent(this, FloatingWidgetService::class.java)
                    intent.putExtra("extra", textToRequest)
                    startService(intent)
                    finish()
                } else if (Settings.canDrawOverlays(this)) {
                    var intent = Intent(this, FloatingWidgetService::class.java)
                    intent.putExtra("extra", textToRequest)
                    startService(intent)
                    finish()
                } else {
                    askPermission()
                    Toast.makeText(
                        this,
                        "You need System Alert Window Permission to do this",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openApp(packageApp: String): Boolean {
        try {
            packageManager.getApplicationInfo(packageApp, 0)
            var launchIntent = packageManager.getLaunchIntentForPackage(packageApp)
            startActivity(launchIntent)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            installTheApp(packageApp)
            return false
        }

    }

    private fun installTheApp(packageApp: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No se ha encontrado la aplicacion para hacer el curso")
        builder.setMessage("Ups, no tenes la aplicacion para hacer el curso. Queres instalarla?")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.cancel()
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageApp")
                    )
                )
            } catch (anfe: android.content.ActivityNotFoundException) {
            }
            dialog.cancel()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.cancel()
        }

        builder.show()
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
        return ActionBarDrawerToggle(
            this,
            mDrawer,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    fun selectDrawerItem(menuItem: MenuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        var fragmentClass: ContactFragment?
        when (menuItem.itemId) {
            R.id.nav_first_fragment -> fragmentClass = ContactFragment("Contanos que curso queres agregar y que te gustaria que tenga")
            R.id.nav_second_fragment -> fragmentClass = ContactFragment("Contanos que problema tuviste, si es posible tambien indicar el modelo del celular")
            else ->fragmentClass = ContactFragment("No deberia estar aca")
        }

        toolbar?.title = menuItem.title
        // Insert the fragment by replacing any existing fragment
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.flContent, fragmentClass!!).commit()

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

    override fun onBackPressed() {
        if(supportFragmentManager.fragments.size != 0){
            fragmentManager?.beginTransaction()?.remove(fragmentManager?.findFragmentById(R.id.flContent)!!)?.commit()
            toolbar?.title = "Seleccionar curso"
        }else{
            val intent = Intent(this, HomeSection::class.java)
            startActivity(intent)
            finish()
        }
    }
}

