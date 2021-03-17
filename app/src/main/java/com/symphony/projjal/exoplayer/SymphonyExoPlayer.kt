package com.symphony.projjal.exoplayer

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.symphony.bitmaputils.BitmapUtils
import com.symphony.bitmaputils.BitmapUtils.drawableToBitmap
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.R
import com.symphony.projjal.SymphonyApplication.Companion.applicationInstance
import com.symphony.projjal.glide.ErrorDrawable.getSongPlaceHolderDrawable
import com.symphony.projjal.services.MusicService
import com.symphony.projjal.utils.ConversionUtils.dpToPx
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SymphonyExoPlayer(val context: Context, val service: Service) : EventListener {
    private val job = Job()
    private val playerScope = CoroutineScope(Dispatchers.Main + job)

    private val NOTIFICATION_ID = 10000

    private val bitmapGenerationQueue: MutableList<Long> = mutableListOf()

    var playerNotificationManager: PlayerNotificationManager? = null
    var mediaSession: MediaSessionCompat? = null
    private var mediaSessionConnector: MediaSessionConnector? = null
    private val mediaDescriptionAdapter: MediaDescriptionAdapter =
        object : MediaDescriptionAdapter {
            override fun getCurrentSubText(player: Player): String? {
                val song = player.currentMediaItem?.playbackProperties?.tag as Song?
                return song?.album
            }

            override fun getCurrentContentTitle(player: Player): String {
                val song = player.currentMediaItem?.playbackProperties?.tag as Song?
                return song?.title ?: ""
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                return null
            }

            override fun getCurrentContentText(player: Player): String? {
                val song = player.currentMediaItem?.playbackProperties?.tag as Song?
                return song?.artist
            }

            override fun getCurrentLargeIcon(player: Player, callback: BitmapCallback): Bitmap? {
                val song = player.currentMediaItem?.playbackProperties?.tag as Song? ?: return null
                val cachedBitmap = applicationInstance.lruCache.getBitmap(song.id.toString())
                if (cachedBitmap != null) {
                    return cachedBitmap
                }
                playerScope.launch(Dispatchers.IO) {
                    if (song.id !in bitmapGenerationQueue) {
                        bitmapGenerationQueue.add(song.id)
                        var data = BitmapUtils.getByteDataFromFile(context, song.fileUri)
                        if (data == null) {
                            data = BitmapUtils.getByteDataFromUri(context, song.albumArtUri)
                        }
                        val bitmap: Bitmap? = if (data == null) {
                            drawableToBitmap(
                                getSongPlaceHolderDrawable(context),
                                dpToPx(500),
                                dpToPx(500)
                            )
                        } else {
                            BitmapFactory.decodeByteArray(data, 0, data.size)
                        }
                        if (bitmap != null) {
                            applicationInstance.lruCache.putBitmap(song.id.toString(), bitmap)
                            callback.onBitmap(bitmap)
                        }
                    }
                }
                return null
            }
        }

    private var handler: Handler = Handler(Looper.getMainLooper())
    private val updateProgressTask = object : Runnable {
        override fun run() {
            listeners.forEach {
                it.onPlaybackPositionChanged(playbackPosition.toInt(), duration.toInt())
            }
            handler.postDelayed(this, 1000)
        }
    }

    private lateinit var player: SimpleExoPlayer

    private var mediaSource: ConcatenatingMediaSource? = null
    private var shuffleOrder: SymphonyShuffleOrder? = null
    private val userAgent = Util.getUserAgent(context, "Symphony")
    private val defaultMediaSource = DefaultDataSourceFactory(context, userAgent)
    private val progressiveMediaSource = ProgressiveMediaSource.Factory(defaultMediaSource)

    var shuffle: Boolean
        get() = player.shuffleModeEnabled
        set(value) {
            if (value) {
                val symphonyShuffleOrder = SymphonyShuffleOrder(size)
                shuffleOrder = symphonyShuffleOrder
                mediaSource?.setShuffleOrder(symphonyShuffleOrder)
            }
            player.shuffleModeEnabled = value
            playerScope.launch(Dispatchers.IO) {
                Paper.book("player").write("shuffleOrder", shuffleOrder ?: SymphonyShuffleOrder(0))
                Paper.book("player").write("shuffle", value)
            }
        }

    fun setShuffleWithoutResettingShuffleOrder(shuffle: Boolean) {
        var symphonyShuffleOrder = shuffleOrder
        if (symphonyShuffleOrder == null) {
            symphonyShuffleOrder = SymphonyShuffleOrder(size)
        }
        mediaSource?.setShuffleOrder(symphonyShuffleOrder)
        player.shuffleModeEnabled = shuffle
    }

    fun changeShuffle() {
        shuffle = !shuffle
    }

    var repeat
        get() = player.repeatMode
        set(value) {
            player.repeatMode = value
            playerScope.launch(Dispatchers.IO) {
                Paper.book("player").write("repeat", value)
            }
        }

    fun changeRepeat() {
        when (repeat) {
            REPEAT_MODE_OFF -> repeat = REPEAT_MODE_ALL
            REPEAT_MODE_ALL -> repeat = REPEAT_MODE_ONE
            REPEAT_MODE_ONE -> repeat = REPEAT_MODE_OFF
        }
    }

    private var playWhenReady: Boolean
        get() = player.playWhenReady
        set(value) {
            player.playWhenReady = value
        }

    val audioSessionId: Int get() = player.audioSessionId

    val isPlaying: Boolean get() = player.isPlaying

    fun changePlayPause() {
        playWhenReady = !isPlaying
    }

    fun play() {
        playWhenReady = true
    }

    fun pause() {
        playWhenReady = false
    }

    val duration: Long get() = player.duration

    var playbackPosition: Long
        get() = player.currentPosition
        set(value) {
            player.seekTo(value)
        }

    val size: Int get() = mediaSource?.size ?: 0

    val isQueueEmpty: Boolean get() = size == 0

    val currentTimeline: Timeline?
        get() {
            return try {
                player.currentTimeline
            } catch (ignored: Exception) {
                null
            }
        }

    val currentSong: Song?
        get() = convertMediaItemToSong(player.currentMediaItem)

    var currentWindowIndex: Int
        get() = player.currentWindowIndex
        set(value) {
            player.seekTo(value, C.TIME_UNSET)
        }

    fun playPrevious(forcePrevious: Boolean = false) {
        if (size == 0) {
            return
        }
        if (forcePrevious || player.currentPosition < 5000) {
            currentWindowIndex =
                currentTimeline?.getPreviousWindowIndex(
                    currentWindowIndex,
                    REPEAT_MODE_ALL,
                    shuffle
                )
                    ?: currentWindowIndex
            playWhenReady = true
        } else {
            player.seekTo(0)
        }
    }

    fun playNext() {
        if (size == 0) {
            return
        }
        currentWindowIndex =
            currentTimeline?.getNextWindowIndex(currentWindowIndex, REPEAT_MODE_ALL, shuffle)
                ?: currentWindowIndex
        playWhenReady = true
    }

    fun getPositionInPlayingOrder(windowIndex: Int, shuffle: Boolean): Int {
        if (!shuffle) {
            return windowIndex
        }
        val symphonyShuffleOrder = shuffleOrder ?: return 0
        if (symphonyShuffleOrder.indexInShuffled.isEmpty()) {
            return 0
        }
        return symphonyShuffleOrder.indexInShuffled[windowIndex]
    }

    fun setPositionInPlayingOrder(position: Int) {
        currentWindowIndex = if (!shuffle) {
            position
        } else {
            val symphonyShuffleOrder = shuffleOrder ?: return
            if (symphonyShuffleOrder.shuffled.isEmpty()) {
                return
            }
            symphonyShuffleOrder.shuffled[position]
        }
    }

    fun getQueueInPlayingOrder(): MutableList<Song> {
        val songs = mutableListOf<Song>()
        val symphonyMediaSource = mediaSource ?: return songs
        if (!shuffle) {
            for (i in 0 until symphonyMediaSource.size) {
                val song = convertMediaItemToSong(symphonyMediaSource.getMediaSource(i).mediaItem)
                if (song != null) {
                    songs.add(song)
                }
            }
        } else {
            val symphonyShuffleOrder = shuffleOrder
            if (symphonyShuffleOrder == null) {
                for (i in 0 until symphonyMediaSource.size) {
                    val song =
                        convertMediaItemToSong(symphonyMediaSource.getMediaSource(i).mediaItem)
                    if (song != null) {
                        songs.add(song)
                    }
                }
            } else {
                for (i in 0 until symphonyMediaSource.size) {
                    val song = convertMediaItemToSong(
                        symphonyMediaSource.getMediaSource(symphonyShuffleOrder.shuffled[i]).mediaItem
                    )
                    if (song != null) {
                        songs.add(song)
                    }
                }
            }
        }
        return songs
    }

    fun getQueueInUnshuffledOrder(): MutableList<Song> {
        val songs = mutableListOf<Song>()
        val symphonyMediaSource = mediaSource ?: return songs
        for (i in 0 until symphonyMediaSource.size) {
            val song = convertMediaItemToSong(symphonyMediaSource.getMediaSource(i).mediaItem)
            if (song != null) {
                songs.add(song)
            }
        }
        return songs
    }

    private fun convertMediaItemToSong(mediaItem: MediaItem?): Song? {
        return mediaItem?.playbackProperties?.tag as Song?
    }

    private var isPreparing = false
    private var isListShuffling = false

    fun playList(songs: MutableList<Song>, position: Int = 0, playWhenReady: Boolean = true) {
        shuffle = false
        mediaSource = createConcatenatingMediaSource(songs)
        player.setMediaSource(mediaSource!!)
        setDefaultWindowIndex(position)
        isPreparing = true
        player.prepare()
        this.playWhenReady = playWhenReady
    }

    fun shuffleList(
        songs: MutableList<Song>,
        playWhenReady: Boolean = true,
        windowIndex: Int = -1,
        shuffleOrder: SymphonyShuffleOrder? = null
    ) {
        shuffle = false
        mediaSource = createConcatenatingMediaSource(songs)
        player.setMediaSource(mediaSource!!)
        if (shuffleOrder != null) {
            this.shuffleOrder = shuffleOrder
        }
        val symphonyShuffleOrder = this.shuffleOrder
        if (windowIndex != -1) {
            setDefaultWindowIndex(windowIndex)
        } else if (symphonyShuffleOrder != null && symphonyShuffleOrder.firstIndex != C.INDEX_UNSET) {
            setDefaultWindowIndex(symphonyShuffleOrder.firstIndex)
        }
        isPreparing = true
        isListShuffling = true
        player.prepare()
        this.playWhenReady = playWhenReady
    }

    fun setDefaultWindowIndex(windowIndex: Int) {
        player.seekToDefaultPosition(windowIndex)
    }

    fun addToQueue(songs: MutableList<Song>) {
        for (song in songs) {
            addToQueue(song)
        }
    }

    fun addToQueue(song: Song) {
        currentTimeline?.getLastWindowIndex(shuffle)?.plus(1)?.let {
            player.addMediaSource(
                it,
                createMediaSource(song)
            )
        }
    }

    fun addToNextPosition(songs: MutableList<Song>) {
        var currentPosition = currentWindowIndex
        for (song in songs) {
            currentPosition =
                currentTimeline?.getNextWindowIndex(currentPosition, REPEAT_MODE_OFF, shuffle) ?: 0
            player.addMediaSource(
                currentPosition,
                createMediaSource(song)
            )
        }
    }

    fun addToNextPosition(song: Song) {
        player.addMediaSource(
            currentTimeline?.getNextWindowIndex(currentWindowIndex, REPEAT_MODE_OFF, shuffle) ?: 0,
            createMediaSource(song)
        )
    }

    private fun createConcatenatingMediaSource(songs: MutableList<Song>): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        val mediaSources: MutableList<MediaSource> = mutableListOf()
        for (item in songs) {
            mediaSources.add(progressiveMediaSource.createMediaSource(createMediaItem(item)))
        }
        concatenatingMediaSource.addMediaSources(mediaSources)
        val symphonyShuffleOrder = SymphonyShuffleOrder(concatenatingMediaSource.size)
        shuffleOrder = symphonyShuffleOrder
        concatenatingMediaSource.setShuffleOrder(symphonyShuffleOrder)
        return concatenatingMediaSource
    }

    private fun createMediaSource(song: Song): MediaSource {
        return progressiveMediaSource.createMediaSource(createMediaItem(song))
    }

    private fun createMediaItem(song: Song): MediaItem {
        return MediaItem.Builder().setUri(song.fileUri).setTag(song).build()
    }

    private fun init() {
        player = SimpleExoPlayer.Builder(context).build()
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build(), true
        )
        player.addListener(this@SymphonyExoPlayer)
        val mediaSessionCompat = MediaSessionCompat(context, "symphony")
        mediaSession = mediaSessionCompat
        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat)
        mediaSessionConnector?.setPlayer(player)
        mediaSessionConnector?.setEnabledPlaybackActions(
            PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_SEEK_TO
                    or PlaybackStateCompat.ACTION_FAST_FORWARD
                    or PlaybackStateCompat.ACTION_REWIND
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                    or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
        )
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            getChannelId(),
            R.string.notification_channel_name,
            R.string.notification_channel_description,
            NOTIFICATION_ID,
            mediaDescriptionAdapter,
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (isPlaying) {
                        startForeground(notification)
                    } else {
                        service.stopForeground(false)
                    }
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                }
            })
        playerNotificationManager?.setPlayer(player)
        playerNotificationManager?.setSmallIcon(R.drawable.ic_statusbar)
        playerNotificationManager?.setColorized(true)
        playerNotificationManager?.setMediaSessionToken(mediaSession!!.sessionToken)
        playerNotificationManager?.setUseNextActionInCompactView(true)
        playerNotificationManager?.setUsePreviousActionInCompactView(true)
        playerNotificationManager?.setControlDispatcher(object : ControlDispatcher {
            override fun dispatchPrepare(player: Player): Boolean {
                return true
            }

            override fun dispatchSetPlayWhenReady(
                player: Player,
                playWhenReady: Boolean
            ): Boolean {
                this@SymphonyExoPlayer.playWhenReady = playWhenReady
                return true
            }

            override fun dispatchSeekTo(
                player: Player,
                windowIndex: Int,
                positionMs: Long
            ): Boolean {
                playbackPosition = positionMs
                return true
            }

            override fun dispatchPrevious(player: Player): Boolean {
                playPrevious(false)
                return true
            }

            override fun dispatchNext(player: Player): Boolean {
                playNext()
                return true
            }

            override fun dispatchRewind(player: Player): Boolean {
                return false
            }

            override fun dispatchFastForward(player: Player): Boolean {
                return false
            }

            override fun dispatchSetRepeatMode(player: Player, repeatMode: Int): Boolean {
                repeat = repeatMode
                return true
            }

            override fun dispatchSetShuffleModeEnabled(
                player: Player,
                shuffleModeEnabled: Boolean
            ): Boolean {
                shuffle = shuffleModeEnabled
                return true
            }

            override fun dispatchStop(player: Player, reset: Boolean): Boolean {
                pause()
                return false
            }

            override fun isRewindEnabled(): Boolean {
                return false
            }

            override fun isFastForwardEnabled(): Boolean {
                return false
            }
        })

        val songs = Paper.book("player").read("queue", mutableListOf<Song>())

        val currentWindowIndex = Paper.book("player").read("currentWindowIndex", 0)

        val shuffleOrder = Paper.book("player").read("shuffleOrder", SymphonyShuffleOrder(0))
        val repeat = Paper.book("player").read("repeat", 0)
        val shuffle = Paper.book("player").read("shuffle", false)

        if (shuffle) {
            shuffleList(songs, false, currentWindowIndex, shuffleOrder)
        } else {
            playList(songs, currentWindowIndex, false)
        }
        this.repeat = repeat
    }

    private fun startForeground(notification: Notification) {
        try {
            val intent = Intent(context, MusicService::class.java)
            ContextCompat.startForegroundService(context, intent)
            service.startForeground(NOTIFICATION_ID, notification)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getChannelId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("music", "Audio playback service")
        } else {
            return "music"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_MIN
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    fun release() {
        handler.removeCallbacks(updateProgressTask)
        player.release()
        mediaSession?.isActive = false
        mediaSession?.release()
        playerNotificationManager?.setPlayer(null)
    }

    val listeners: MutableList<EventListener> = mutableListOf()

    fun addListener(listener: EventListener, invokeCallbacks: Boolean = false) {
        listeners.add(listener)
        if (invokeCallbacks) {
            val songs = getQueueInPlayingOrder()
            val positionInPlayingOrder = getPositionInPlayingOrder(currentWindowIndex, shuffle)
            listener.onPlayingQueueChanged(songs)
            listener.onSongChanged(positionInPlayingOrder, currentSong)
            listener.onShuffleChanged(shuffle)
            listener.onRepeatChanged(repeat)
            listener.onIsPlayingChanged(isPlaying)
            listener.onPlaybackPositionChanged(playbackPosition.toInt(), duration.toInt())
        }
    }

    fun removeListener(listener: EventListener) {
        listeners.remove(listener)
    }

    interface EventListener {
        fun onPlayingQueueChanged(queue: MutableList<Song>)
        fun onShuffleChanged(shuffle: Boolean)
        fun onRepeatChanged(repeat: Int)
        fun onIsPlayingChanged(isPlaying: Boolean)
        fun onSongChanged(position: Int, song: Song?)
        fun onPlaybackPositionChanged(playbackPosition: Int, duration: Int)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            handler.post(updateProgressTask)
        } else {
            handler.removeCallbacks(updateProgressTask)
        }
        listeners.forEach {
            it.onIsPlayingChanged(isPlaying)
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        listeners.forEach {
            it.onRepeatChanged(repeatMode)
        }
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        val songs = getQueueInPlayingOrder()
        val positionInPlayingOrder =
            getPositionInPlayingOrder(currentWindowIndex, shuffle)
        listeners.forEach {
            it.onShuffleChanged(shuffleModeEnabled)
            it.onPlayingQueueChanged(songs)
            it.onSongChanged(positionInPlayingOrder, currentSong)
        }
        playerScope.launch(Dispatchers.IO) {
            Paper.book("player").write("currentWindowIndex", positionInPlayingOrder)
            Paper.book("player").write("queue", songs)
        }
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        if (reason == TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
            val songs = getQueueInPlayingOrder()
            val positionInPlayingOrder = getPositionInPlayingOrder(currentWindowIndex, shuffle)
            listeners.forEach {
                it.onPlayingQueueChanged(songs)
                it.onSongChanged(
                    positionInPlayingOrder,
                    songs[positionInPlayingOrder]
                )
                it.onPlaybackPositionChanged(0, songs[0].duration)
            }
            mediaSession?.isActive = timeline.windowCount > 0
            playerScope.launch(Dispatchers.IO) {
                Paper.book("player").write("currentWindowIndex", positionInPlayingOrder)
                Paper.book("player").write("queue", songs)
            }
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        if (isPreparing && state == STATE_READY) {
            if (isListShuffling) {
                setShuffleWithoutResettingShuffleOrder(true)
                isListShuffling = false
            }
            isPreparing = false
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        if (
            reason == MEDIA_ITEM_TRANSITION_REASON_AUTO ||
            reason == MEDIA_ITEM_TRANSITION_REASON_SEEK
        ) {
            val song = convertMediaItemToSong(mediaItem)
            val shuffle = shuffle
            val positionInPlayingOrder = getPositionInPlayingOrder(currentWindowIndex, shuffle)
            listeners.forEach {
                it.onSongChanged(positionInPlayingOrder, song)
                it.onPlaybackPositionChanged(0, song?.duration ?: 0)
            }
            playerScope.launch(Dispatchers.IO) {
                Paper.book("player").write("currentWindowIndex", getPositionInPlayingOrder(positionInPlayingOrder, shuffle))
            }
        }
    }

    init {
        init()
    }
}
