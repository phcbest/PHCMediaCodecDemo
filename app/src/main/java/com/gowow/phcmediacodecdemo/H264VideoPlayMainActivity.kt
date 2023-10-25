package com.gowow.phcmediacodecdemo

import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.gowow.phcmediacodecdemo.base.BaseActivity
import com.gowow.phcmediacodecdemo.databinding.ActivityH264VideoPlayMainBinding
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.IOException


private const val TAG = "H264VideoPlayMainActivi"

/**
 * [参考文章](https://juejin.cn/post/7213733567606652985)
 */
class H264VideoPlayMainActivity :
    BaseActivity<ActivityH264VideoPlayMainBinding>(ActivityH264VideoPlayMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
    }

    private var mediaCodec: MediaCodec? = null
    private lateinit var videoPath: String
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                uri.encodedPath?.let {
                    videoPath = it
                    initMediaCodec()
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }


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

    inner class MyRun : Runnable {
        override fun run() {
            try {
                //io流方式读取h264
                val bytes: ByteArray = getBytes(videoPath) ?: return
                Log.e(TAG, "bytes size " + bytes.size)
                val inputBuffers = mediaCodec!!.inputBuffers
                //开始位置
                val startIndex = 0
                //h264总字节数
                val totalSize: Int = bytes.size


            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun getBytes(videoPath: String): ByteArray? {
            val inputStream = DataInputStream(FileInputStream(videoPath))
            var len: Int
            val size = 1024
            val buf = ByteArray(size)
            val bos = ByteArrayOutputStream()
            while (inputStream.read(buf, 0, size).also { len = it } != -1) {
                bos.write(buf, 0, len)
            }
            return bos.toByteArray()
        }

    }
}
