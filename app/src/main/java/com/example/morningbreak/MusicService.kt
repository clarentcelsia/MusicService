package com.example.morningbreak

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.Toast

class MusicService : Service(), MediaPlayer.OnErrorListener {

    val mBinder: IBinder = ServiceBinder()
    var mPlayer: MediaPlayer? = null
    var length = 0

    inner class ServiceBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    // get your music by convert mp3 file into json then create new raw res
    override fun onCreate() {
        super.onCreate()

        mPlayer = MediaPlayer.create(this, R.raw.calm)
        mPlayer?.setOnErrorListener(this)
        mPlayer?.let {
            it.apply {
                isLooping = true
                setVolume(50F, 50F)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(mPlayer!=null){
            mPlayer?.start()
        }
        return START_NOT_STICKY
    }


    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        Toast.makeText(this, "Music player failed", Toast.LENGTH_SHORT).show()

        mPlayer = p0
        mPlayer?.let {
            try {
                it.apply {
                    stop()
                    release()
                }
            } finally {
                mPlayer = null
            }
        }
        return false
    }

    fun pause() {
        mPlayer?.let {
            if (mPlayer!!.isPlaying) {
                it.pause()
                length = it.currentPosition
            }
        }
    }

    fun resume() {
        mPlayer?.let {
            if (!mPlayer!!.isPlaying) {
                it.apply {
                    seekTo(length)
                    start()
                }
            }
        }
    }

    //start
    //stop

    override fun onDestroy() {
        super.onDestroy()

        mPlayer?.let {
            try {
                it.apply {
                    stop()
                    release()
                }
            } finally {
                mPlayer = null
            }
        }
    }
}


