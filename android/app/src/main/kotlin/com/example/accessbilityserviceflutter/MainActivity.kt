package com.example.accessbilityserviceflutter

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

object Data{
    var i:Int = 0
    fun setDate(c : Int){
        i = c
    }
}

class MainActivity: FlutterActivity() {

    private val CHANNEL = "test"

    override fun configureFlutterEngine( flutterEngine: FlutterEngine) {

        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            run {
                if (call.method == "getResult") {

                    var count:Int? = call.argument<String>("count")?.toInt()
                    Data.i = count ?: -100
                    println(
                        count
                    )
                    if(count == null ) count = 0
                    result.success( count +1)

                } else {
                    result.notImplemented()
                }
            }
        }
    }
}
