package br.com.bluestormstudios.app_remote

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Intent
import android.os.Bundle
import android.view.Surface
import io.flutter.embedding.engine.renderer.FlutterRenderer
import io.flutter.view.TextureRegistry

class MainActivity : FlutterActivity() {
    private val CHANNEL = "scrcpy_channel"
    private var textureEntry: TextureRegistry.SurfaceTextureEntry? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "startScrcpy" -> {
                    val serverAdr = call.argument<String>("serverAdr") ?: ""
                    val videoBitrate = call.argument<Int>("videoBitrate") ?: 8000000
                    val maxHeight = call.argument<Int>("maxHeight") ?: 1920
                    
                    // Criar uma entrada de textura
                    textureEntry = flutterEngine.renderer.createSurfaceTexture()
                    val surfaceTexture = textureEntry!!.surfaceTexture()
                    val surface = Surface(surfaceTexture)

                    val intent = Intent(this, Scrcpy::class.java).apply {
                        putExtra("serverAdr", serverAdr)
                        putExtra("videoBitrate", videoBitrate)
                        putExtra("maxHeight", maxHeight)
                    }
                    startService(intent)
                    
                    // Iniciar o Scrcpy com a Surface
                    val scrcpy = Scrcpy()
                    scrcpy.start(surface, serverAdr, maxHeight, videoBitrate)

                    result.success(textureEntry!!.id())
                }
                else -> result.notImplemented()
            }
        }
    }

    override fun onDestroy() {
        textureEntry?.release()
        super.onDestroy()
    }
}