package com.gowow.pmediacodelib

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import java.nio.ByteBuffer


private const val TAG = "VideoExtractFrames"

/**
 * 图帧抽取相关
 */
class VideoExtractFrames {
    fun getVideoFirstFrames(path: String): Bitmap? {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(path)
        var codec: MediaCodec? = null
        var videoFormat: MediaFormat? = null
        for (i in 0..mediaExtractor.trackCount) {
            val trackFormat = mediaExtractor.getTrackFormat(i)
            if (trackFormat.getString(MediaFormat.KEY_MIME)?.contains("video") == true) {
                videoFormat = trackFormat
                mediaExtractor.selectTrack(i)
                break
            }
        }
        if (videoFormat == null) {
            return null
        }
        val imageFormat = ImageFormat.YUV_420_888
        val colorFormat = COLOR_FormatYUV420Flexible
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat)
        videoFormat.setInteger(
            MediaFormat.KEY_WIDTH, videoFormat.getInteger(MediaFormat.KEY_WIDTH) / 4
        )
        videoFormat.setInteger(
            MediaFormat.KEY_HEIGHT, videoFormat.getInteger(MediaFormat.KEY_HEIGHT) / 4
        )

        val duration = videoFormat.getLong(MediaFormat.KEY_DURATION)

        codec = MediaCodec.createDecoderByType(videoFormat.getString(MediaFormat.KEY_MIME)!!)
        val imageReader = ImageReader
            .newInstance(
                videoFormat.getInteger(MediaFormat.KEY_WIDTH),
                videoFormat.getInteger(MediaFormat.KEY_HEIGHT),
                imageFormat,
                3
            )
        val imageReaderHandlerThread = Looper.myLooper()?.let { Handler(it) }
//        imageReader.setOnImageAvailableListener(
//            MyOnImageAvailableListener(callBack),
//            imageReaderHandlerThread
//        )
        codec.configure(videoFormat, imageReader.surface, null, 0)
        codec.start()
        val bufferInfo = MediaCodec.BufferInfo()
        val timeOut = (5 * 1000).toLong() //10ms

        val inputDone = false
        val outputDone = false
        var inputBuffers: Array<ByteBuffer?>? = null


        //开始进行解码。
//        int count = 1;
//        while (!outputDone) {}
        return null
    }
}