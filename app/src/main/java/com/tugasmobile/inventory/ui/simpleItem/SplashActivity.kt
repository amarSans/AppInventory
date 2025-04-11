package com.tugasmobile.inventory.ui.simpleItem

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.RenderMode
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val lottie = findViewById<LottieAnimationView>(R.id.lottie_empty)
        lottie.setRenderMode(RenderMode.HARDWARE)

        lottie.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                },10 ) // Splash screen 3 detik
            }
        })

        // Delay ke MainActivity selama 2 detik

    }
}