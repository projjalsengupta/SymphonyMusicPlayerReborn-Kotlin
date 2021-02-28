package com.symphony.projjal

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.symphony.projjal.services.MusicService
import com.symphony.projjal.utils.LruCache
import io.paperdb.Paper

class SymphonyApplication : Application() {
    companion object {
        lateinit var applicationInstance: SymphonyApplication
            private set
    }

    var isBound = false
    var musicService: MusicService? = null
        set(service) {
            field = service
            if (field != null) {
                for (item in musicServiceNotNullListeners) {
                    item(service!!)
                }
                musicServiceNotNullListeners.clear()
            }
        }

    private val musicServiceNotNullListeners: MutableList<(MusicService) -> Unit> = mutableListOf()
    val lruCache: LruCache = LruCache()

    fun getNonNullMusicService(onMusicServiceAvailable: (MusicService) -> Unit) {
        val service = musicService
        if (service != null) {
            onMusicServiceAvailable(service)
        } else {
            if (!isBound) {
                startService()
            }
            musicServiceNotNullListeners.add(onMusicServiceAvailable)
        }
    }

    override fun onCreate() {
        super.onCreate()
        applicationInstance = this@SymphonyApplication

        Paper.init(applicationContext)

        startService()
    }

    private fun startService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private val musicServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            val binder = service as MusicService.MusicServiceBinder
            musicService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            musicService = null
        }
    }


}