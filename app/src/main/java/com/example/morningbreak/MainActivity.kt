package com.example.morningbreak

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.example.morningbreak.Constant.Companion.INIT_PAGE
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mHomeWatcher: HomeWatcher

    // vars
    var isBound = false
    var service: MusicService? = null
    var conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            service = null
        }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            service = MusicService()
            service = (binder as MusicService.ServiceBinder).getService()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService()
        var music = Intent()
        music.setClass(this, MusicService::class.java)
        startService(music)

        mHomeWatcher = HomeWatcher(this)
        mHomeWatcher.setHomePressedListener(object : HomeWatcher.OnHomePressedListener{
            override fun onHomePressed() {
                service?.pause()
            }

            override fun onHomeLongPpressed() {
                service?.pause()
            }
        })
        mHomeWatcher.startWatch()

        //automatically
//        viewFlip.apply {
//            flipInterval = 2000
//            startFlipping()
//        }

        //next page
        btnPrevPage.setOnClickListener{
            viewFlip.apply {
                setInAnimation(this@MainActivity, android.R.anim.slide_in_left)
                setOutAnimation(this@MainActivity, android.R.anim.slide_out_right)
                showPrevious()
            }
        }

        btnNextPage.setOnClickListener{
            viewFlip.apply {
                setInAnimation(this@MainActivity, R.anim.slide_in_right)
                setOutAnimation(this@MainActivity, R.anim.slide_out_left)
                showNext()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        service?.resume()

    }

    override fun onPause() {
        super.onPause()

        // detect idle screen
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        var isScreenOn = false
        pm?.let {
            isScreenOn = pm.isInteractive
        }

        if(!isScreenOn){
            service?.let {
                it.pause()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unBindService()
        val music = Intent()
        music.setClass(this, MusicService::class.java)
        stopService(music)
    }

    private fun bindService() {
        bindService(
                Intent(this, MusicService::class.java),
                conn, Context.BIND_AUTO_CREATE
        )

        isBound = true
    }

    private fun unBindService() {
        if (isBound) {
            unbindService(conn)
            isBound = false
        }
    }



}