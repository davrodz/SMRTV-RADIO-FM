package com.smrtv.smrtvradio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PLAY -> if (MainActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.EXIT -> {
                MainActivity().exitApplication()
            }
        }
    }
    private fun playMusic(){
        MainActivity.isPlaying = true
        MainActivity.musicService!!.mediaPlayer!!.start()
        MainActivity.musicService!!.showNotification(R.drawable.pause)
        MainActivity.bindingRadio.playPause.setBackgroundResource(R.drawable.boton_on)
    }
    private fun pauseMusic(){
        MainActivity.isPlaying = false
        MainActivity.musicService!!.mediaPlayer!!.pause()
        MainActivity.musicService!!.showNotification(R.drawable.play)
        MainActivity.bindingRadio.playPause.setBackgroundResource(R.drawable.boton_off)
    }
}