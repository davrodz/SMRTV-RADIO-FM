package com.smrtv.smrtvradio


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.smrtv.smrtvradio.databinding.ActivityMainBinding
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), ServiceConnection {


   lateinit var audioManager: AudioManager
   //lateinit var vv_fondo: VideoView
   //lateinit var mMediapaFondo: MediaPlayer
   //var mCurrentVideoPosition: Int = 0
    companion object {
        var musicService:MusicService? = null
        var isPlaying:Boolean = false
        @SuppressLint("StaticFieldLeak")
        lateinit var bindingRadio: ActivityMainBinding
        var min15: Boolean =false
        var min30: Boolean =false
        var min60: Boolean =false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingRadio = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingRadio.root)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //bindingRadio.emisoras.isSelected = true
        //bindingRadio.titulo.isSelected = true
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val seek = bindingRadio.seekBarVolumen
        seek.max = maxVolume
        seek.progress = currentVolume
        seek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0)
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })


        /*var like = false
        bindingRadio.smrtv.setOnClickListener {
            like = likeAnimation(bindingRadio.smrtv, R.raw.caritamusica,like)
        }*/

        /*bindingRadio.facebook.setOnClickListener {
            val url = "https://www.facebook.com/SistemaMichoacano/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        bindingRadio.instagram.setOnClickListener {
            val url = "https://www.instagram.com/sistema.michoacano/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        bindingRadio.whatsapp.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=524438660901"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        bindingRadio.web.setOnClickListener {
            val url = "https://sistemamichoacano.tv/radio-fm/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        bindingRadio.youtube.setOnClickListener {
            val url = "https://www.youtube.com/c/SistemaMichoacanodeRadioyTelevisi%C3%B3n"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        bindingRadio.twitter.setOnClickListener {
            val url = "https://twitter.com/SistemaMich"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        */
        bindingRadio.timerBtn.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) showBottomSheetDialog()
            else{
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Modo sueño")
                    .setMessage("¿Quieres detener el modo sueño?")
                    .setPositiveButton("Detener"){ _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        bindingRadio.timerBtn.setBackgroundResource(R.drawable.timer)
                    }
                    .setNegativeButton("No"){dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            }
        }
        bindingRadio.reload.setOnClickListener {
            bindingRadio.playPause.setBackgroundResource(R.drawable.boton_2)
            musicService!!.showNotification(R.drawable.pause)
            Toast.makeText(baseContext, "Se reiniciará la Transmisión", Toast.LENGTH_SHORT).show()
            isPlaying = false
            musicService!!.mediaPlayer!!.pause()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(getString(R.string.url))
            musicService!!.mediaPlayer!!.prepareAsync()
            musicService!!.mediaPlayer!!.setOnPreparedListener{
                musicService!!.mediaPlayer!!.start()
                bindingRadio.playPause.setBackgroundResource(R.drawable.boton_on)
                isPlaying = true
            }
        }
        val intent1 = Intent(this, MusicService::class.java)
        bindService(intent1, this, BIND_AUTO_CREATE)
        startService(intent1)
        }
        private fun playsmrtv(){
            bindingRadio.playPause.setOnClickListener {
                if (isPlaying) {pauseRadio()} else {playRadio()}
            }
        }
        private fun playRadio(){
            bindingRadio.playPause.setBackgroundResource(R.drawable.boton_on)
            musicService!!.showNotification(R.drawable.pause)
            bindingRadio.lineas.playAnimation()
            isPlaying = true
            musicService!!.mediaPlayer!!.start()
        }
        private fun pauseRadio(){
            bindingRadio.playPause.setBackgroundResource(R.drawable.boton_2)
            musicService!!.showNotification(R.drawable.play)
            bindingRadio.lineas.pauseAnimation()
            bindingRadio.lineas.setFrame(0)
            isPlaying = false
            musicService!!.mediaPlayer!!.pause()
        }
        private fun showBottomSheetDialog(){
            val dialog = BottomSheetDialog(this@MainActivity)
            dialog.setContentView(R.layout.bottom_sheet_dialog)
            dialog.show()
            dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
                Toast.makeText(baseContext, "La musica se detendra despues de 15 minutos", Toast.LENGTH_SHORT).show()
                bindingRadio.timerBtn.setBackgroundResource(R.drawable.timer_play)
                min15 = true
                Thread{Thread.sleep(15 * 60000)
                if(min15)
                    if(musicService != null){
                        musicService!!.stopForeground(true)
                        musicService!!.mediaPlayer!!.release()
                        musicService = null}
                    exitProcess(1)}.start()
                dialog.dismiss()
            }
            dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
                Toast.makeText(baseContext, "La musica se detendra despues de 30 minutos", Toast.LENGTH_SHORT).show()
                bindingRadio.timerBtn.setBackgroundResource(R.drawable.timer_play)
                min30 = true
                Thread{Thread.sleep(30 * 60000)
                    if(min30)
                        if(musicService != null){
                            musicService!!.stopForeground(true)
                            musicService!!.mediaPlayer!!.release()
                            musicService = null}
                    exitProcess(1)}.start()
                dialog.dismiss()
            }
            dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
                Toast.makeText(baseContext, "La musica se detendra despues de 60 minutos", Toast.LENGTH_SHORT).show()
                bindingRadio.timerBtn.setBackgroundResource(R.drawable.timer_play)
                min60 = true
                Thread{Thread.sleep(60 * 60000)
                    if(min60)
                        if(musicService != null){
                            musicService!!.stopForeground(true)
                            musicService!!.mediaPlayer!!.release()
                            musicService = null}
                    exitProcess(1)}.start()
                dialog.dismiss()
            }
        }

        private fun likeAnimation(imageView: LottieAnimationView, animation: Int, like: Boolean):Boolean{
        if (!like){
            imageView.setAnimation(animation)
            imageView.repeatCount = LottieDrawable.INFINITE
            imageView.playAnimation()
        }else{
            imageView.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animator : Animator) {
                            imageView.setImageResource(R.drawable.logosmrtvandroid)
                            imageView.alpha = 1f
                        }
                })
        }
        return !like
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
            musicService!!.crearReproductor()
            musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            musicService!!.showNotification(R.drawable.pause)
            bindingRadio.playPause.setBackgroundResource(R.drawable.boton_on)
            playsmrtv()
        }

        fun exitApplication(){
        if(musicService != null){
            musicService!!.audioManager.abandonAudioFocus(musicService)
            musicService!!.stopForeground(true)
            musicService!!.mediaPlayer!!.release()
            musicService = null}
        moveTaskToBack(true)
        exitProcess(0)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
        }

        override fun onPause() {
        super.onPause()
        //mCurrentVideoPosition = mMediapaFondo.currentPosition
        //vv_fondo.pause()//para el video fondo
        }

        override fun onResume() {
        super.onResume()
        //vv_fondo.start() //para el video fondo
        }

        override fun onDestroy() {
        super.onDestroy()
        //mMediapaFondo.release() //fondo video
        if(!MainActivity.isPlaying && MainActivity.musicService != null){
            exitApplication()
        }
        }
        }



