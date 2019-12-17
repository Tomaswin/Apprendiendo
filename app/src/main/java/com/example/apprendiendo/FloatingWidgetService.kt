package com.example.apprendiendo

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import kotlin.math.roundToInt

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
    lateinit var textView: TextView
    lateinit var btnReturn: Button
    lateinit var btnNext: Button
    lateinit var pageCounter: TextView
    lateinit var imageView: ImageView
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        courseRequest = intent.getStringExtra("extra")
        initService()
        return super.onStartCommand(intent, flags, startId)

    }

    private fun initService(){
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)
        btnReturn = mChatHeadView?.findViewById(R.id.btn_return) as Button
        btnNext = mChatHeadView?.findViewById(R.id.btn_next) as Button
        textView = mChatHeadView?.findViewById(R.id.textview) as TextView
        pageCounter = mChatHeadView?.findViewById(R.id.page_counter) as TextView
        imageView = mChatHeadView?.findViewById(R.id.collapsed_iv) as ImageView
        interfaceClient.create()
        stepsList = interfaceClient.getSteps(courseRequest, applicationContext)
        pageCounter.text = (steps + 1).toString() + "/" + stepsList.count().toString()
        //Add the view to the window.
        val params: WindowManager.LayoutParams
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                Intent.FLAG_DEBUG_LOG_RESOLUTION,
                PixelFormat.TRANSLUCENT
            )
        } else {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                Intent.FLAG_DEBUG_LOG_RESOLUTION,
                PixelFormat.TRANSLUCENT
            )
        }

        //Specify the chat head position
        params.gravity = getGravity(stepsList[steps].positionY) or getGravity(stepsList[steps].positionX)
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
        var lastAction: Int = 0
        var initialX: Int = 0
        var initialY: Int = 0
        var initialTouchX: Float = 0.toFloat()
        var initialTouchY: Float = 0.toFloat()
        if(steps == 0){
            btnReturn.text = "Return App"
        }else{
            btnReturn.text = getText(R.string.return_key)
        }
        textView.text = stepsList[steps].image
        btnReturn.setOnClickListener {
            if(steps == 0){
                val intent = Intent(this@FloatingWidgetService, HomeSection::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                //close the service and remove the chat heads
                stopSelf()
            }else{
                //viewPager.setCurrentItem(steps - 1, true)
                btnReturn.text = getText(R.string.return_key)
                btnNext.text = getText(R.string.next)
                steps -= 1
                textView.text = stepsList[steps].image
                pageCounter.text = (steps + 1).toString() + "/" + stepsList.count().toString()
                params.gravity = getGravity(stepsList[steps].positionY) or getGravity(stepsList[steps].positionX)
                params.x = 0
                params.y = 100
                mWindowManager!!.updateViewLayout(mChatHeadView, params)
                if(steps == 0){
                    btnReturn.text = "Return App"
                    btnNext.text = getText(R.string.next)
                }else{
                    btnReturn.text = getText(R.string.return_key)
                }
            }
        }
        btnNext.setOnClickListener {
            // checking for last page
            // if last page home screen will be launched
            if (steps + 1 < stepsList.size) {
                btnNext.text = getText(R.string.next)
                btnReturn.text = getText(R.string.return_key)
                steps += 1
                textView.text = stepsList[steps].image
                pageCounter.text = (steps + 1).toString() + "/" + stepsList.count().toString()
                params.gravity = getGravity(stepsList[steps].positionY) or getGravity(stepsList[steps].positionX)
                params.x = 0
                params.y = 100
                mWindowManager!!.updateViewLayout(mChatHeadView, params)
                //viewPager.setCurrentItem(steps + 1, true)
                // move to next screen
                if(steps == stepsList.size - 1){
                    btnNext.text = "Return App"
                    btnReturn.text = getText(R.string.return_key)
                }else{
                    btnNext.text = getText(R.string.next)
                }
            } else {
                btnNext.text = "Return App"
                btnReturn.text = getText(R.string.return_key)
                val intent = Intent(this@FloatingWidgetService, HomeSection::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //close the service and remove the chat heads
                stopSelf()
            }
        }
    }

    private fun getGravity(position: String): Int {
        when (position) {
            "LEFT" -> return Gravity.LEFT
            "RIGHT" -> return Gravity.RIGHT
            "TOP" -> return Gravity.TOP
            "BOTTOM" -> return Gravity.BOTTOM
            else -> { // Note the block
                return Gravity.TOP
            }
        }
    }

    private fun getItem(i: Int) {
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

    fun convertDpToPixel(dp: Float, context: Context): Int {
        return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun convertPixelsToDp(px: Float, context: Context): Int {
        return (px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
}