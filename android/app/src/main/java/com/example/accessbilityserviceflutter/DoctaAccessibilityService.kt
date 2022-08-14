package com.example.accessbilityserviceflutter

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity

class DoctaAccessibilityService : AccessibilityService() {
    var mLayout: FrameLayout? = null
    private lateinit var windowManager : WindowManager
    private lateinit var lp : WindowManager.LayoutParams
    private var screenOnOffReceiver: BroadcastReceiver? = null

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
        lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = 150 //WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = 150 //WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER


        mLayout?.background = ContextCompat.getDrawable(this, R.drawable.notif)


        //val inflater = LayoutInflater.from(this)
        //inflater.inflate(R.layout.floating_layout, mLayout)

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

                        windowManager.updateViewLayout(mLayout, lp)

                    }

                }
                return false

            }

        })


        mLayout?.setOnClickListener {
            println("click icon btn")
            val intent = Intent(this, ActivityInfo::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            removeWindow()

            startActivity(intent)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun createWindowOnLockScreen(){
        val keyguard = this.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (keyguard.isKeyguardLocked) {
            println("is locked")
            if(mLayout == null) {
                println("Create cause mLayout is null")
                createWindow()
            }
        } else {
            println("is not locked")
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

                if(strAction == Intent.ACTION_SCREEN_OFF){
                    removeWindow()
                }else if (strAction == Intent.ACTION_USER_PRESENT || strAction == Intent.ACTION_SCREEN_ON) if (myKM.isDeviceLocked) {
                    println("Screen off " + "LOCKED")
                    if(mLayout == null) {
                        createWindow()
                    }
                } else {
                    println("Screen off " + "UNLOCKED")
                    removeWindow()
                }
            }
        }
        applicationContext.registerReceiver(screenOnOffReceiver, theFilter)
    }



    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}