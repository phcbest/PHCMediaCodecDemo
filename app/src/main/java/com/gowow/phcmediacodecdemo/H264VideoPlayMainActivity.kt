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
                var startIndex = 0
                //h264总字节数
                val totalSize: Int = bytes.size
                while (true) {
                    // 检查是否符合解码条件
                    if (totalSize == 0 || startIndex >= totalSize) {
                        break
                    }
                    // 查找下一帧的起始位置
                    val nextFrameStart = findByFrame(bytes, startIndex + 1, totalSize)
                    if (nextFrameStart == -1) break
                    val info = MediaCodec.BufferInfo()
                    // 获取可用的输入缓冲区索引
                    val inIndex = mediaCodec?.dequeueInputBuffer(10000)
                    if ((inIndex ?: -1) >= 0) {
                        // 根据索引获取可用的缓冲区
                        val byteBuffer = inIndex?.let { inputBuffers[it] }
                        // 清空缓冲区
                        byteBuffer?.clear()
                        // 将数据填充到缓冲区
                        byteBuffer?.put(bytes, startIndex, nextFrameStart - startIndex)
                        // 填充数据后通知MediaCodec查询该索引的缓冲区
                        mediaCodec?.queueInputBuffer(
                            inIndex ?: -1, 0, nextFrameStart - startIndex, 0, 0
                        )
                        // 为下一帧做准备，下一帧的起始位置就是前一帧的结束位置
                        startIndex = nextFrameStart
                    } else {
                        // 等待可用的缓冲区
                        continue
                    }
                    // 从MediaCodec的输出缓冲区获取索引
                    val outIndex = mediaCodec?.dequeueOutputBuffer(info, 10000)
                    Log.e(TAG, "outIndex $outIndex")
                    if ((outIndex ?: -1) >= 0) {
                        try {
                            // 暂时以线程休眠的方式减缓播放速度
                            Thread.sleep(33)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        // 如果Surface已绑定，则将数据渲染到Surface并释放缓冲区
                        mediaCodec?.releaseOutputBuffer(outIndex ?: -1, true)
                    } else {
                        Log.e(TAG, "没有解码成功")
                    }
                }

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

    // 读取一帧数据
    private fun findByFrame(bytes: ByteArray, start: Int, totalSize: Int): Int {
        for (i in start until totalSize - 4) {
            // 根据0x00000001作为分隔符来读取实际数据
            if (bytes[i] == 0x00.toByte() && bytes[i + 1] == 0x00.toByte() && bytes[i + 2] == 0x00.toByte() && bytes[i + 3] == 0x01.toByte()) {
                return i
            }
        }
        return -1
    }
}
