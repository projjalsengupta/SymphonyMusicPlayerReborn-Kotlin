package com.symphony.projjal.utils

import android.content.res.Resources
import android.util.Log
import com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.symphony.mediastorequery.model.Song

object ConversionUtils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun milisToTimeString(milis: Int): String {
        val secs: Int = milis / 1000
        return if (secs / 3600 > 1) {
            String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60)
        } else {
            String.format("%02d:%02d", secs / 60, secs % 60)
        }
    }

    fun timeLineToList(
        timeline: Timeline,
        concatenatingMediaSource: ConcatenatingMediaSource,
        shuffleModeEnabled: Boolean
    ): MutableList<Song> {
        Log.e("WINDOWCOUNT", timeline.windowCount.toString())
        val list = mutableListOf<Song>()
        var index = timeline.getFirstWindowIndex(shuffleModeEnabled)
        while (index != -1) {
            list.add(concatenatingMediaSource.getMediaSource(index).mediaItem.playbackProperties?.tag as Song)
            index = timeline.getNextWindowIndex(index, REPEAT_MODE_ALL, shuffleModeEnabled)
        }
        return list
    }
}