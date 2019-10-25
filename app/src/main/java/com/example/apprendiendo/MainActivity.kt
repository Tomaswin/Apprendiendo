package com.example.apprendiendo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_first_time_app.*

class MainActivity : AppCompatActivity() {
    lateinit var dotsLayout: LinearLayout
    lateinit var listTutorialSliders: IntArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_app)
        var prefManager = PrefManager(this)
        if (!prefManager.isFirstTimeLaunch) {
            //TODO REMOVE THE COMMENT BELOW, THE LINE OF OUTSIDE IS FOR CHANGE THE SCREEN TO THE MAIN APPLICATION
            launchMainActivity(prefManager)
            finish()
        }
        //TODO CHECK IF THIS IS NECCESARY Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        var viewPager = findViewById(R.id.viewPager) as ViewPager
        dotsLayout = findViewById(R.id.layoutDots) as LinearLayout
        var btnSkip = findViewById(R.id.btn_skip) as Button
        var btnNext = findViewById(R.id.btn_next) as Button

        listTutorialSliders = intArrayOf(
            R.layout.welcome_slide1,
            R.layout.welcome_slide2,
            R.layout.welcome_slide3,
            R.layout.welcome_slide4
        )

        addBottomDots(0)
        changeStatusBarColor()

        var myViewPagerAdapter = MyViewPagerAdapter()
        viewPager.adapter = myViewPagerAdapter
        btnSkip.setOnClickListener { launchMainActivity(prefManager) }
        btnNext.setOnClickListener {
            // checking for last page
            // if last page home screen will be launched
            val current = getItem(+1)
            if (current < listTutorialSliders.size) {
                // move to next screen
                viewPager.currentItem = current
            } else {
                launchMainActivity(prefManager)
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                addBottomDots(position)

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == listTutorialSliders.size - 1) {
                    // last page. make button text to GOT IT
                    btnNext.text = getString(R.string.start)
                    btnSkip.visibility = View.GONE
                } else {
                    // still pages are left
                    btnNext.text = getString(R.string.next)
                    btnSkip.visibility = View.VISIBLE
                }
            }

        })
    }

    private fun addBottomDots(currentPage: Int) {
        var dots = arrayOfNulls<TextView>(listTutorialSliders.size)

        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)

        dotsLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.setText(Html.fromHtml("&#8226;"))
            dots[i]?.setTextSize(35f)
            dots[i]?.setTextColor(colorsInactive[currentPage])
            dotsLayout.addView(dots[i])
        }

        if (dots.size > 0)
            dots[currentPage]?.setTextColor(colorsActive[currentPage])
    }

    private fun getItem(i: Int): Int {
        return viewPager.currentItem + i
    }

    private fun launchMainActivity(prefManager: PrefManager) {
        prefManager.isFirstTimeLaunch = false
        startActivity(Intent(this, HomeSection::class.java))
        finish()
    }


    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }


    inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null

        override fun getCount(): Int {
            return listTutorialSliders.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = layoutInflater!!.inflate(listTutorialSliders[position], container, false)
            container.addView(view)

            return view
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}