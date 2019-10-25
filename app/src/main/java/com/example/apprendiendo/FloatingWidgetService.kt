package com.example.apprendiendo

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.text.Html
import android.view.*
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_first_time_app.*

class FloatingWidgetService : Service() {

    private var mWindowManager: WindowManager? = null
    private var mChatHeadView: View? = null
    lateinit var dotsLayout: LinearLayout
    lateinit var listTutorialSliders: IntArray
    lateinit var viewPager: ViewPager
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //Inflate the chat head layout we created
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)

        //Add the view to the window.
        val params: WindowManager.LayoutParams
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }

        //Specify the chat head position
        params.gravity = Gravity.TOP or Gravity.RIGHT        //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        //Add the view to the window
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(mChatHeadView, params)

        //Set the close button.
        val closeButton = mChatHeadView!!.findViewById<ImageView>(R.id.buttonClose)
        closeButton.setOnClickListener {
            val intent = Intent(this@FloatingWidgetService, HomeSection::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            //close the service and remove the chat heads
            stopSelf()
        }

        //Drag and move chat head using user's touch action.
        val chatHeadImage = mChatHeadView!!.findViewById<ImageView>(R.id.collapsed_iv)
        chatHeadImage.setOnTouchListener(object : View.OnTouchListener {
            private var lastAction: Int = 0
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        lastAction = event.action
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        val layoutExpanded = mChatHeadView!!.findViewById<LinearLayout>(R.id.layoutExpanded)
                        layoutExpanded.visibility = View.VISIBLE
                        val layoutCollapsed = mChatHeadView!!.findViewById<RelativeLayout>(R.id.layoutCollapsed)
                        layoutCollapsed.visibility = View.GONE

                        viewPager = mChatHeadView?.findViewById(R.id.viewPager) as ViewPager
                        dotsLayout = mChatHeadView?.findViewById(R.id.layoutDots) as LinearLayout
                        var btnReturn = mChatHeadView?.findViewById(R.id.btn_return) as Button
                        var btnNext = mChatHeadView?.findViewById(R.id.btn_next) as Button

                        listTutorialSliders = intArrayOf(
                            R.layout.welcome_slide1,
                            R.layout.welcome_slide2,
                            R.layout.welcome_slide3,
                            R.layout.welcome_slide4
                        )

                        addBottomDots(0)

                        var myViewPagerAdapter = MyViewPagerAdapter()
                        viewPager.adapter = myViewPagerAdapter
                        btnReturn.setOnClickListener {
                            val intent = Intent(this@FloatingWidgetService, HomeSection::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                            //close the service and remove the chat heads
                            stopSelf()
                        }

                        btnNext.setOnClickListener {
                            // checking for last page
                            // if last page home screen will be launched
                            val current = getItem(+1)
                            if (current < listTutorialSliders.size) {
                                // move to next screen
                                viewPager.currentItem = current
                            } else {

                            }
                        }

                        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                            override fun onPageScrollStateChanged(state: Int) {
                            }

                            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                            }

                            override fun onPageSelected(position: Int) {
                                addBottomDots(position)
                            }

                        })
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        //Update the layout with new X & Y coordinate
                        mWindowManager!!.updateViewLayout(mChatHeadView, params)
                        lastAction = event.action
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        mWindowManager!!.updateViewLayout(mChatHeadView, params)
                        lastAction = event.action
                        return true
                    }
                }
                return false
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

    override fun onDestroy() {
        super.onDestroy()
        if (mChatHeadView != null) mWindowManager!!.removeView(mChatHeadView)
    }
}