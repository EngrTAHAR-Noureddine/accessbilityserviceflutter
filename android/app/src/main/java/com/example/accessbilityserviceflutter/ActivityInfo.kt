package com.example.accessbilityserviceflutter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine


class ActivityInfo : FlutterActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_info)
    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onDestroy() {
        val preferences = this.getSharedPreferences("Docta", Context.MODE_PRIVATE)
        preferences.edit().putBoolean("runActivity", true).apply()

        println("destroy activity : ${preferences.getBoolean("runActivity", false)}")

        super.onDestroy()
    }
}