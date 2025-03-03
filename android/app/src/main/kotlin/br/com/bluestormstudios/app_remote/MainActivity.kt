package br.com.bluestormstudios.app_remote

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Intent
import android.os.Bundle

class MainActivity : FlutterActivity() {
    private val CHANNEL = "scrcpy_channel"

    override fun configureFlutterEngine(flutterEngine: FlutterPlugin.FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "startScrcpy" -> {
                    val serverAdr = call.argument<String>("serverAdr") ?: ""
                    val videoBitrate = call.argument<Int>("videoBitrate") ?: 8000000
                    val maxHeight = call.argument<Int>("maxHeight") ?: 1920
                    val intent = Intent(this, Scrcpy::class.java).apply {
                        putExtra("serverAdr", serverAdr)
                        putExtra("videoBitrate", videoBitrate)
                        putExtra("maxHeight", maxHeight)
                    }
                    startService(intent)
                    result.success("Scrcpy iniciado")
                }
                else -> result.notImplemented()
            }
        }
    }
}