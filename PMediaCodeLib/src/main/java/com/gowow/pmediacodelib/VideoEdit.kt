package com.gowow.pmediacodelib

import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log


private const val TAG = "VideoEdit"

object VideoEdit {
    fun getVideoInfo(path: String) {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(path)
        var videoFormat: MediaFormat? = null
        Log.i(TAG, "getVideoInfo: 轨道数量 ${mediaExtractor.trackCount}")
        for (i in 0..mediaExtractor.trackCount) {
            val trackFormat = mediaExtractor.getTrackFormat(i)
            if (trackFormat.getString(MediaFormat.KEY_MIME)?.contains("video") == true) {
                videoFormat = trackFormat
                mediaExtractor.selectTrack(i)
                break
            }
        }
    }
}