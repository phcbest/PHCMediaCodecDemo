package com.gowow.phcmediacodecdemo

import android.media.MediaCodec
import android.media.MediaFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import androidx.media3.decoder.DecoderException
import com.gowow.phcmediacodecdemo.base.BaseActivity
import com.gowow.phcmediacodecdemo.databinding.ActivityH265VideoPlayMainBinding

/**
 * [参考文章](https://juejin.cn/post/7213733567606652985)
 */
class H265VideoPlayMainActivity :
    BaseActivity<ActivityH265VideoPlayMainBinding>(ActivityH265VideoPlayMainBinding::inflate) {
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
//        mediaCodec?.configure()
    }
}