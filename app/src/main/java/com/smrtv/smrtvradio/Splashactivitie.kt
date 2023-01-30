package com.smrtv.smrtvradio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.smrtv.smrtvradio.databinding.ActivitySplashactivitieBinding

class Splashactivitie : AppCompatActivity() {
    lateinit var binding : ActivitySplashactivitieBinding
    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashactivitieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        handler = Handler(Looper.myLooper()!!)
        quitarSplash()
    }

    fun quitarSplash(){
        handler.postDelayed({
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        },2500)
    }
}