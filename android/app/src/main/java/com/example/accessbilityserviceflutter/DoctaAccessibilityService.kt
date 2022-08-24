package com.example.accessbilityserviceflutter

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout

class DoctaAccessibilityService : AccessibilityService() {
    var mLayout: FrameLayout? = null
    private lateinit var windowManager : WindowManager
    private lateinit var lp : WindowManager.LayoutParams
    private var screenOnOffReceiver: BroadcastReceiver? = null

    private var theX:Int = 0
    private var theY:Int = 50

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()
        registerBroadcastReceiver()
    }

    override fun onDestroy() {
        if(screenOnOffReceiver != null) applicationContext.unregisterReceiver(screenOnOffReceiver)
        screenOnOffReceiver = null
        println("THIS IS DESTROY")
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("ClickableViewAccessibility")
    private fun createWindow(){
        println("add window")
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayout = FrameLayout(this)
        mLayout?.setBackgroundColor(0x00000000)
        lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.x = theX
        lp.y = theY
        //lp.gravity = Gravity.CENTER

        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.floating_layout, mLayout)

        windowManager.addView(mLayout, lp)


        mLayout?.setOnTouchListener( object : View.OnTouchListener {
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {

                when(event?.action){
                    MotionEvent.ACTION_DOWN ->{
                        x = lp.x.toDouble()
                        y = lp.y.toDouble()

                        px = event.rawX.toDouble()
                        py = event.rawY.toDouble()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        lp.x = (x + event.rawX - px).toInt()
                        lp.y = (y + event.rawY - py).toInt()

                        theX = lp.x
                        theY = lp.y

                        windowManager.updateViewLayout(mLayout, lp)

                    }

                }
                return false

            }

        })


        val closeBtn = mLayout?.findViewById(R.id.closeBtn) as ImageButton
        val titleInfo = mLayout?.findViewById(R.id.titleInformation) as TextView
        val content = mLayout?.findViewById(R.id.textInfo) as TextView
        val fieldBtns = mLayout?.findViewById(R.id.fieldBtns) as ConstraintLayout
        val fieldContent = mLayout?.findViewById(R.id.fieldContent) as ConstraintLayout
        val iconApp = mLayout?.findViewById(R.id.iconApp) as ImageButton

        iconApp.setOnClickListener {
            windowManager.removeView(mLayout)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            windowManager.addView(mLayout,lp)
            fieldContent.visibility = View.VISIBLE
            content.text = Data.i.toString()
            fieldBtns.visibility = View.VISIBLE
            titleInfo.visibility = View.VISIBLE
            iconApp.visibility = View.INVISIBLE
        }
        iconApp.setOnTouchListener( object : View.OnTouchListener {
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {

                when(event?.action){
                    MotionEvent.ACTION_DOWN ->{
                        x = lp.x.toDouble()
                        y = lp.y.toDouble()

                        px = event.rawX.toDouble()
                        py = event.rawY.toDouble()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        lp.x = (x + event.rawX - px).toInt()
                        lp.y = (y + event.rawY - py).toInt()
                        theX = lp.x
                        theY = lp.y
                        windowManager.updateViewLayout(mLayout, lp)

                    }

                }
                return false

            }

        })



        closeBtn.setOnClickListener {
            windowManager.removeView(mLayout)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            windowManager.addView(mLayout,lp)
            fieldContent.visibility = View.INVISIBLE
            fieldBtns.visibility = View.INVISIBLE
            titleInfo.visibility = View.INVISIBLE
            iconApp.visibility = View.VISIBLE
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("CommitPrefEdits")
    override fun onSystemActionsChanged() {
        val preferences = this.getSharedPreferences("Docta", Context.MODE_PRIVATE)
        if(preferences.getBoolean("runActivity", false)){
            createWindow()
            preferences.edit().putBoolean("runActivity", false).apply()
        }
    }

    private fun removeWindow(){
        println("remove window")
        if(mLayout?.windowToken != null){
            windowManager.removeView(mLayout)
            mLayout = null

        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onServiceConnected() {
        // -----------------------------------------------------------------------------------------------------
        createWindowOnLockScreen()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createWindowOnLockScreen(){
        val keyguard = this.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (keyguard.isKeyguardLocked) {
            println("is locked")
            //println("is enabled accessebility service : ${isAccessibilityEnabled()}")
            if(mLayout == null) {
                println("Create cause mLayout is null")
                createWindow()
            }
        } else {
            println("is not locked")
            //println("is enabled accessebility service : ${isAccessibilityEnabled()}")
            removeWindow()
        }
    }

    private fun registerBroadcastReceiver() {
        val theFilter = IntentFilter()
        /** System Defined Broadcast  */
        theFilter.addAction(Intent.ACTION_SCREEN_ON)
        theFilter.addAction(Intent.ACTION_SCREEN_OFF)
        theFilter.addAction(Intent.ACTION_USER_PRESENT)
        theFilter.addAction(Intent.FLAG_ACTIVITY_NO_USER_ACTION.toString())


        screenOnOffReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onReceive(context: Context, intent: Intent) {
                val strAction = intent.action
                println("Action is : $strAction")
                val myKM = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager

                if(strAction == Intent.ACTION_SCREEN_OFF ){
                    removeWindow()
                }else if (strAction == Intent.ACTION_USER_PRESENT || strAction == Intent.ACTION_SCREEN_ON) if (myKM.isDeviceLocked) {
                    println("Screen off " + "LOCKED")
                    println("is enabled accessebility service : ${this@DoctaAccessibilityService.accessibilityButtonController.isAccessibilityButtonAvailable}")
                    if(mLayout == null) {
                        createWindow()
                    }
                } else {
                    println("Screen off " + "UNLOCKED")
                    println("is enabled accessebility service : ${this@DoctaAccessibilityService.accessibilityButtonController.isAccessibilityButtonAvailable}")
                    removeWindow()
                }
            }
        }
        applicationContext.registerReceiver(screenOnOffReceiver, theFilter)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
/*
  private fun isAccessibilityEnabled(): Boolean {
        var accessibilityEnabled = 0
        try {
            accessibilityEnabled =
                Settings.Secure.getInt(this.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
            println("ACCESSIBILITY: $accessibilityEnabled")
        } catch (e: Settings.SettingNotFoundException) {
            println("Error finding setting, default accessibility to not found: " + e.message)
        }

        return if (accessibilityEnabled == 1) {
            println("***ACCESSIBILIY IS ENABLED***: ")
            true
        } else {
            println( "***ACCESSIBILIY IS DISABLED***")
            false
        }

    }
* */