package com.example.apprendiendo

import android.app.PendingIntent.getActivity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.*
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
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.Image
import com.squareup.picasso.Picasso
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.view.marginLeft
import kotlin.math.roundToInt
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.MotionEvent
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.RelativeLayout
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T










class FloatingWidgetService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mWindowManager: WindowManager? = null
    private var mChatHeadView: View? = null
    lateinit var dotsLayout: LinearLayout
    lateinit var viewPager: ViewPager
    var stepsList = ArrayList<StepsModel>()
    var courseRequest = ""
    var interfaceClient: InterfaceClient = RestClientCall()
    var steps = 0
    lateinit var btnReturn: Button
    lateinit var btnNext: Button

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        courseRequest = intent.getStringExtra("extra")
        initService()
        return super.onStartCommand(intent, flags, startId)

    }

    private fun initService(){
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)
        btnReturn = mChatHeadView?.findViewById(R.id.btn_return) as Button
        btnNext = mChatHeadView?.findViewById(R.id.btn_next) as Button

        interfaceClient.create()
        stepsList = interfaceClient.getSteps(courseRequest, applicationContext)

        //Add the view to the window.
        val params: WindowManager.LayoutParams
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )
        } else {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )
        }

        //Specify the chat head position
        params.gravity = Gravity.TOP or Gravity.LEFT        //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        //Add the view to the window
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(mChatHeadView, params)
        mWindowManager!!.updateViewLayout(mChatHeadView, params)

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
        viewPager = mChatHeadView?.findViewById(R.id.viewPager) as ViewPager
        var myViewPagerAdapter = MyViewPagerAdapter()
        viewPager.adapter = myViewPagerAdapter
        viewPager.beginFakeDrag()
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
                        getItem(0)
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        val layoutExpanded = mChatHeadView!!.findViewById<LinearLayout>(R.id.layoutExpanded)
                        layoutExpanded.visibility = View.VISIBLE
                        val layoutCollapsed = mChatHeadView!!.findViewById<RelativeLayout>(R.id.layoutCollapsed)
                        layoutCollapsed.visibility = View.GONE
                        val params: WindowManager.LayoutParams
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            params = WindowManager.LayoutParams(
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                                PixelFormat.TRANSLUCENT
                            )
                        } else {
                            params = WindowManager.LayoutParams(
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.TYPE_PHONE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                                PixelFormat.TRANSLUCENT
                            )
                        }
                        params.gravity = Gravity.TOP or Gravity.LEFT        //Initially view will be added to top-left corner
                        params.x = 0
                        params.y = 100
                        mWindowManager!!.updateViewLayout(mChatHeadView, params)
                        layoutCollapsed.setOnClickListener {
                            val layoutExpanded = mChatHeadView!!.findViewById<LinearLayout>(R.id.layoutExpanded)
                            layoutExpanded.visibility = View.VISIBLE
                            val layoutCollapsed = mChatHeadView!!.findViewById<RelativeLayout>(R.id.layoutCollapsed)
                            layoutCollapsed.visibility = View.GONE
                            val params: WindowManager.LayoutParams
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                params = WindowManager.LayoutParams(
                                    WindowManager.LayoutParams.MATCH_PARENT,
                                    WindowManager.LayoutParams.MATCH_PARENT,
                                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                                    PixelFormat.TRANSLUCENT
                                )
                            } else {
                                params = WindowManager.LayoutParams(
                                    WindowManager.LayoutParams.MATCH_PARENT,
                                    WindowManager.LayoutParams.MATCH_PARENT,
                                    WindowManager.LayoutParams.TYPE_PHONE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                                    PixelFormat.TRANSLUCENT
                                )
                            }
                            params.gravity = Gravity.TOP or Gravity.LEFT        //Initially view will be added to top-left corner
                            params.x = 0
                            params.y = 100
                            mWindowManager!!.updateViewLayout(mChatHeadView, params)
                        }



                        if(viewPager.currentItem == 0){
                            btnReturn.text = "Return App"
                        }else{
                            btnReturn.text = getText(R.string.return_key)
                        }

                        btnReturn.setOnClickListener {
                            if(viewPager.currentItem == 0){
                                val intent = Intent(this@FloatingWidgetService, HomeSection::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)

                                //close the service and remove the chat heads
                                stopSelf()
                            }else{
                                viewPager.setCurrentItem(steps - 1, true)
                                btnReturn.text = getText(R.string.return_key)
                                btnNext.text = getText(R.string.next)
                                val layoutExpanded = mChatHeadView!!.findViewById<LinearLayout>(R.id.layoutExpanded)
                                layoutExpanded.visibility = View.GONE
                                val layoutCollapsed = mChatHeadView!!.findViewById<RelativeLayout>(R.id.layoutCollapsed)
                                layoutCollapsed.visibility = View.VISIBLE
                                setWrapContent()
                            }
                        }
                        btnNext.setOnClickListener {
                            // checking for last page
                            // if last page home screen will be launched
                            if (steps + 1 < stepsList.size) {
                                btnNext.text = getText(R.string.next)
                                btnReturn.text = getText(R.string.return_key)
                                viewPager.setCurrentItem(steps + 1, true)
                                // move to next screen
                                val layoutExpanded = mChatHeadView!!.findViewById<LinearLayout>(R.id.layoutExpanded)
                                layoutExpanded.visibility = View.GONE
                                val layoutCollapsed = mChatHeadView!!.findViewById<RelativeLayout>(R.id.layoutCollapsed)
                                layoutCollapsed.visibility = View.VISIBLE
                                setWrapContent()
                            } else {
                                val intent = Intent(this@FloatingWidgetService, HomeSection::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                //close the service and remove the chat heads
                                stopSelf()
                            }
                        }

                        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                            override fun onPageScrollStateChanged(state: Int) {
                            }

                            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                            }

                            override fun onPageSelected(position: Int) {}

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

    private fun setWrapContent(){
        val params: WindowManager.LayoutParams
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )
        } else {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )
        }
        params.gravity = Gravity.TOP or Gravity.LEFT        //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100
        mWindowManager!!.updateViewLayout(mChatHeadView, params)
    }

    private fun getItem(i: Int) {
        steps = viewPager.currentItem + i
        if(steps == 0){
            btnReturn.text = "Return App"
        }else if(steps + 1 == stepsList.size){
            btnNext.text = "Return App"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChatHeadView != null) mWindowManager!!.removeView(mChatHeadView)
    }

    inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null
        private var enabled = false

        override fun getCount(): Int {
            return stepsList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater!!.inflate(R.layout.content_expandable_service, container, false)
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val marginParams = MarginLayoutParams(imageView.getLayoutParams())
            marginParams.setMargins(350,2500,0, 0)
            val layoutParams = LinearLayout.LayoutParams(marginParams)
            imageView.layoutParams = layoutParams
            Picasso.with(applicationContext).load(stepsList[position].image).placeholder(R.mipmap.ic_launcher).into(imageView)
            val textView = view.findViewById<TextView>(R.id.textView)
            container.addView(view)

            return view
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }


        override fun destroyItem(collection: View, position: Int, o: Any) {
            var view: View? = o as View
            (collection as ViewPager).removeView(view)
            //views.remove(position)
            view = null
        }

    }

    fun convertDpToPixel(dp: Float, context: Context): Int {
        return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun convertPixelsToDp(px: Float, context: Context): Int {
        return (px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
}