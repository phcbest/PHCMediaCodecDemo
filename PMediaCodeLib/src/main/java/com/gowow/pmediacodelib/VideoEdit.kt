package com.gowow.pmediacodelib

import android.graphics.BitmapFactory
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import java.io.File
import java.nio.ByteBuffer


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

    fun extractFrames(videoPath: String) {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(videoPath)

        // 寻找视频轨道
        var videoTrackIndex = -1
        for (i in 0 until mediaExtractor.trackCount) {
            val format = mediaExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                videoTrackIndex = i
                break
            }
        }
        if (videoTrackIndex == -1) {
            Log.e(TAG, "No video track found in the video file.")
            return
        }

        mediaExtractor.selectTrack(videoTrackIndex)

        val mediaFormat = mediaExtractor.getTrackFormat(videoTrackIndex)
        val width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH)
        val height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT)

        val mediaCodec =
            MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME) ?: return)
        mediaCodec.configure(mediaFormat, null, null, 0)
        mediaCodec.start()


        val info = MediaCodec.BufferInfo()

        val outputPath = File(videoPath).parent?.plus("/frames/") ?: return
        File(outputPath).mkdirs()
        var frameIndex = 0

        val buffer = ByteBuffer.allocate(1024 * 1024) // 1MB buffer
        while (true) {
            val sampleSize = mediaExtractor.readSampleData(buffer, 0)
            if (sampleSize < 0) {
                break
            }
            val presentationTimeUs = mediaExtractor.sampleTime

            // Write the sample data to the codec's input buffer
            val inputBufferIndex = mediaCodec.dequeueInputBuffer(-1)
            if (inputBufferIndex >= 0) {
                val inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex)
                inputBuffer?.clear()
                buffer.limit(sampleSize)
                inputBuffer?.put(buffer)
                mediaCodec.queueInputBuffer(
                    inputBufferIndex, 0, sampleSize, presentationTimeUs, 0
                )
                mediaExtractor.advance()
            }

            // Get the decoded frame from the codec's output buffer
            val outputBufferIndex = mediaCodec.dequeueOutputBuffer(info, 0)
            if (outputBufferIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                val frameData = ByteArray(info.size)
                outputBuffer?.get(frameData)
                outputBuffer?.clear()

                // Save the frame as an image (you can save it as a file or process it)
                saveFrameAsImage(frameData, width, height, outputPath, frameIndex)
                frameIndex++

                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
            }
        }

        mediaExtractor.release()
        mediaCodec.stop()
        mediaCodec.release()
    }

    private fun saveFrameAsImage(
        frameData: ByteArray, width: Int, height: Int, outputPath: String, frameIndex: Int
    ) {
        if (frameIndex == 0) {
            val decodeByteArray = BitmapFactory.decodeByteArray(frameData, 0, frameData.size)
            Log.i(TAG, "saveFrameAsImage: $decodeByteArray")
        }

        Log.d(TAG, "Saved frame $frameIndex - Size: ${frameData.size} bytes")
    }


}