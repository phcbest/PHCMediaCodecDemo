package com.gowow.phcmediacodecdemo

import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Bundle
import android.view.SurfaceHolder
import com.gowow.phcmediacodecdemo.base.BaseActivity
import com.gowow.phcmediacodecdemo.databinding.ActivityH264VideoPlayMainBinding
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.IOException

/**
 * [参考文章](https://juejin.cn/post/7213733567606652985)
 */
class H264VideoPlayMainActivity :
    BaseActivity<ActivityH264VideoPlayMainBinding>(ActivityH264VideoPlayMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMediaCodec()
    }

    private var mediaCodec: MediaCodec? = null


    private fun initMediaCodec() {
        //解码器按照类型设置为aac
        mediaCodec = MediaCodec.createDecoderByType("video/avc")
        //创建视频配置
        val mediaFormat = MediaFormat.createVideoFormat("video/avc", 540, 960)
        //视频解码预期帧速率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        //绑定解码Surface
        binding.surfaceVideo.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mediaCodec?.configure(mediaFormat, binding.surfaceVideo.holder.surface, null, 0)
                decodePlay()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })
    }

    private fun decodePlay() {
        mediaCodec?.start()
        Thread(MyRun()).start()
    }

    class MyRun : Runnable {
        override fun run() {
            try {
                //io流方式读取h264
                var bytes: ByteArray? = null
//                bytes = getBytes(videoPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

//        private fun getBytes(videoPath: Any): ByteArray? {
//            DataInputStream(FileInputStream(videoPath))
//        }

    }
}
