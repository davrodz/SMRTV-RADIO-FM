package com.smrtv.smrtvradio

import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.util.Patterns
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MusicService:Service(), AudioManager.OnAudioFocusChangeListener{
    private var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    lateinit var audioManager: AudioManager

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }
    inner class MyBinder:Binder(){
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun crearReproductor(){
        if (URLUtil.isValidUrl(getString(R.string.url)) && Patterns.WEB_URL.matcher(getString(R.string.url)).matches()){
            try {
                if (MainActivity.musicService!!.mediaPlayer == null) MainActivity.musicService!!.mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }
                MainActivity.musicService!!.mediaPlayer!!.reset()
                MainActivity.musicService!!.mediaPlayer!!.setDataSource(getString(R.string.url))
                MainActivity.musicService!!.mediaPlayer!!.prepareAsync()
            }catch (e: Exception){
                Log.e(ContentValues.TAG, "Fallo al cargar la url", e)
                Toast.makeText(this,"Lamentamos las molestias ahora mismo estamos fuera del aire", Toast.LENGTH_SHORT).show()
                MainActivity.musicService!!.mediaPlayer!!.release()}
            MainActivity.musicService!!.mediaPlayer!!.setOnPreparedListener{
                MainActivity.musicService!!.mediaPlayer!!.start()
                MainActivity.isPlaying = true
            }
             MainActivity.musicService!!.mediaPlayer!!.setOnErrorListener(MediaPlayer.OnErrorListener {
                     mediaPlayer, i, i2 ->
                 Toast.makeText(this,
                     "Algo salio mal.",
                     Toast.LENGTH_LONG).show()
                 true
             })
        }else{
            Toast.makeText(this,"Por el momento la transmisión está caída, regresa más tarde.", Toast.LENGTH_SHORT).show()
        }
    }

    fun showNotification(playPauseBtn: Int){

        val pruebaIntent = Intent(baseContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, pruebaIntent, PendingIntent.FLAG_IMMUTABLE)
        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)
        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_IMMUTABLE)
        val status = if (playPauseBtn == R.drawable.pause){getString(R.string.nEscuchando)}else{getString(R.string.nPausa)}
        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(getString(R.string.nTitulo))
            .setContentText(status)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logonotifi))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.radio_fm, "FM", null)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.close_thick, "Exit", exitPendingIntent)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(13,notification)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0){
            MainActivity.bindingRadio.playPause.setBackgroundResource(R.drawable.boton_off)
            MainActivity.isPlaying = false
            mediaPlayer!!.pause()
            showNotification(R.drawable.play)

        }else{
            MainActivity.bindingRadio.playPause.setBackgroundResource(R.drawable.boton_on)
            MainActivity.isPlaying = true
            mediaPlayer!!.start()
            showNotification(R.drawable.pause)
        }
    }

}